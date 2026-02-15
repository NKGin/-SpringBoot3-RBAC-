package com.ginwind.springrbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginwind.springrbac.constant.MessageConstant;
import com.ginwind.springrbac.dto.PermissionDTO;
import com.ginwind.springrbac.dto.PermissionPageDTO;
import com.ginwind.springrbac.entity.Permission;
import com.ginwind.springrbac.mapper.PermissionMapper;
import com.ginwind.springrbac.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    /**
     * 分页查询权限
     */
    @Override
    public Page<Permission> pageQuery(PermissionPageDTO dto) {
        Page<Permission> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getName()), Permission::getPermissionName, dto.getName())
                .orderByDesc(Permission::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 保存权限
     */
    @Override
    public void savePermission(PermissionDTO dto) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionPath, dto.getPermissionPath())
                .eq(Permission::getPermissionMethod, dto.getPermissionMethod());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException(MessageConstant.ALREADY_EXIST);
        }
        Permission permission = new Permission();
        BeanUtils.copyProperties(dto, permission);
        this.save(permission);
    }

    /**
     * 更新权限
     */
    @Override
    public void updatePermission(PermissionDTO dto) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(dto, permission);
        this.updateById(permission);
    }
}
