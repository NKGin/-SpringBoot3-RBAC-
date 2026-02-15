package com.ginwind.springrbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginwind.springrbac.dto.PermissionDTO;
import com.ginwind.springrbac.dto.PermissionPageDTO;
import com.ginwind.springrbac.entity.Permission;

/**
 * 权限服务接口
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 分页查询权限
     */
    Page<Permission> pageQuery(PermissionPageDTO dto);

    /**
     * 保存权限
     */
    void savePermission(PermissionDTO dto);

    /**
     * 更新权限
     */
    void updatePermission(PermissionDTO dto);
}
