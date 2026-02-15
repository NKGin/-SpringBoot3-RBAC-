package com.ginwind.springrbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginwind.springrbac.constant.MessageConstant;
import com.ginwind.springrbac.dto.RoleDTO;
import com.ginwind.springrbac.dto.RolePageDTO;
import com.ginwind.springrbac.entity.Role;
import com.ginwind.springrbac.entity.RolePermission;
import com.ginwind.springrbac.entity.UserRole;
import com.ginwind.springrbac.mapper.RoleMapper;
import com.ginwind.springrbac.mapper.RolePermissionMapper;
import com.ginwind.springrbac.mapper.UserRoleMapper;
import com.ginwind.springrbac.service.RoleService;
import com.ginwind.springrbac.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 分页查询角色
     */
    @Override
    public Page<Role> pageQuery(RolePageDTO dto) {
        Page<Role> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getRoleName()), Role::getRoleName, dto.getRoleName())
                .orderByDesc(Role::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 保存角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleDTO roleDTO) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, roleDTO.getRoleName());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException(MessageConstant.ALREADY_EXIST);
        }

        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        this.save(role);

        saveRolePermissions(role.getId(), roleDTO.getPermissionIds());
    }

    /**
     * 更新角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleDTO roleDTO) {
        log.info("前端传入roleDTO id{}，permissionids{}", roleDTO.getId(), roleDTO.getPermissionIds());
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        this.updateById(role);

        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleDTO.getId());
        rolePermissionMapper.delete(wrapper);

        saveRolePermissions(roleDTO.getId(), roleDTO.getPermissionIds());
        log.info("修改成功");
    }

    /**
     * 删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Integer id) {
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getRoleId, id);
        if (userRoleMapper.selectCount(userRoleWrapper) > 0) {
            throw new RuntimeException("当前角色已分配给用户，无法删除");
        }

        this.removeById(id);

        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(rpWrapper);
    }

    /**
     * 获取角色详情
     */
    @Override
    public RoleVO getRoleDetail(Integer id) {
        Role role = this.getById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, id);
        List<Integer> permIds = rolePermissionMapper.selectList(wrapper).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        vo.setPermissionIds(permIds);

        return vo;
    }

    /**
     * 保存角色权限关联
     */
    private void saveRolePermissions(Integer roleId, List<Integer> permissionIds) {
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Integer permId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                rolePermissionMapper.insert(rp);
            }
        }
    }
}
