package com.ginwind.springrbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginwind.springrbac.constant.MessageConstant;
import com.ginwind.springrbac.constant.PasswordConstant;
import com.ginwind.springrbac.constant.StatusConstant;
import com.ginwind.springrbac.dto.PasswordEditDTO;
import com.ginwind.springrbac.dto.UserDTO;
import com.ginwind.springrbac.dto.UserPageDTO;
import com.ginwind.springrbac.entity.User;
import com.ginwind.springrbac.entity.UserRole;
import com.ginwind.springrbac.mapper.UserMapper;
import com.ginwind.springrbac.mapper.UserRoleMapper;
import com.ginwind.springrbac.service.UserService;
import com.ginwind.springrbac.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     */
    @Override
    public Page<UserVO> pageQuery(UserPageDTO dto) {
        Page<User> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(dto.getUsername()), User::getUsername, dto.getUsername())
                .orderByDesc(User::getCreateTime);

        Page<User> userPage = this.page(page, wrapper);

        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());

        Page<UserVO> voPage = new Page<>();
        BeanUtils.copyProperties(userPage, voPage);
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 保存用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(UserDTO userDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException(MessageConstant.ALREADY_EXIST);
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD));
        user.setStatus(StatusConstant.ENABLE);
        this.save(user);

        saveUserRoles(user.getId(), userDTO.getRoleIds());
    }

    /**
     * 更新用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        this.updateById(user);

        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userDTO.getId());
        userRoleMapper.delete(wrapper);

        saveUserRoles(userDTO.getId(), userDTO.getRoleIds());
    }

    /**
     * 批量删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRole::getUserId, ids);
        userRoleMapper.delete(wrapper);

        boolean result = this.removeByIds(ids);
        System.out.println("用户表删除结果: " + result);
    }

    /**
     * 更新用户状态
     */
    @Override
    public void updateStatus(Long id, String status) {
        User user = User.builder().id(id).status(status).build();
        this.updateById(user);
    }

    /**
     * 重置用户密码
     */
    @Override
    public void resetPassword(Long id) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD));
        this.updateById(user);
    }

    /**
     * 修改密码
     */
    @Override
    public void editPassword(PasswordEditDTO dto) {
        User user = this.getById(dto.getUserId());
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException(MessageConstant.PASSWORD_ERROR);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
    }

    /**
     * 获取用户详情
     */
    @Override
    public UserVO getUserDetail(Long id) {
        User user = this.getById(id);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        List<Integer> roleIds = userRoleMapper.selectList(wrapper).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        vo.setRoleIds(roleIds);

        return vo;
    }

    /**
     * 保存用户角色关联
     */
    private void saveUserRoles(Long userId, List<Integer> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            for (Integer roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoles.add(ur);
            }
            userRoles.forEach(userRoleMapper::insert);
        }
    }
}
