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

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<UserVO> pageQuery(UserPageDTO dto) {
        Page<User> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 模糊查询用户名
        wrapper.like(StringUtils.hasText(dto.getUsername()), User::getUsername, dto.getUsername())
                .orderByDesc(User::getCreateTime);

        Page<User> userPage = this.page(page, wrapper);

        // 转换 Entity -> VO
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(UserDTO userDTO) {
        // 1. 校验用户名唯一
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException(MessageConstant.ALREADY_EXIST);
        }

        // 2. 保存用户基本信息
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setId(null);
        // 设置默认密码并加密
        user.setPassword(passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD));
        user.setStatus(StatusConstant.ENABLE);
        this.save(user);

        // 3. 保存用户角色关联
        saveUserRoles(user.getId(), userDTO.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        this.updateById(user);

        // 更新角色：先删除旧关系，再插入新关系
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userDTO.getId());
        userRoleMapper.delete(wrapper);

        saveUserRoles(userDTO.getId(), userDTO.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        // 1. 先删除关联表（解除外键绑定）
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRole::getUserId, ids);
        userRoleMapper.delete(wrapper);

        // 2. 再删除用户表
        boolean result = this.removeByIds(ids);
        System.out.println("用户表删除结果: " + result);
    }

    @Override
    public void updateStatus(Long id, String status) {
        User user = User.builder().id(id).status(status).build(); // 假设User加了@Builder，或者用setter
        // 如果User没加@Builder注解，用下面方式：
        // User user = new User(); user.setId(id); user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public void resetPassword(Long id) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD));
        this.updateById(user);
    }

    @Override
    public void editPassword(PasswordEditDTO dto) {
        // 只能修改自己的密码，或者在Controller层控制
        User user = this.getById(dto.getUserId());
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException(MessageConstant.PASSWORD_ERROR);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
    }

    @Override
    public UserVO getUserDetail(Long id) {
        User user = this.getById(id);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 查询角色ID列表
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        List<Integer> roleIds = userRoleMapper.selectList(wrapper).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        vo.setRoleIds(roleIds);

        return vo;
    }

    /**
     * 辅助方法：保存用户角色关系
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
            // 批量插入需要 Mapper 支持，或者循环插入
            // 这里演示循环插入，实际生产建议使用 MyBatisPlus 的 saveBatch
            userRoles.forEach(userRoleMapper::insert);
        }
    }
}