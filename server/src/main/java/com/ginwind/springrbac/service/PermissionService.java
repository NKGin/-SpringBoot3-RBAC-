package com.ginwind.springrbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginwind.springrbac.dto.PermissionDTO;
import com.ginwind.springrbac.dto.PermissionPageDTO;
import com.ginwind.springrbac.entity.Permission;

public interface PermissionService extends IService<Permission> {
    Page<Permission> pageQuery(PermissionPageDTO dto);
    void savePermission(PermissionDTO dto);
    void updatePermission(PermissionDTO dto);
}