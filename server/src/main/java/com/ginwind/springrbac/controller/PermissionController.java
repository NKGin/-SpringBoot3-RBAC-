package com.ginwind.springrbac.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ginwind.springrbac.dto.PermissionDTO;
import com.ginwind.springrbac.dto.PermissionPageDTO;
import com.ginwind.springrbac.entity.Permission;
import com.ginwind.springrbac.result.Result;
import com.ginwind.springrbac.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询权限
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<Page<Permission>> page(PermissionPageDTO dto) {
        return Result.success(permissionService.pageQuery(dto));
    }

    /**
     * 查询所有权限
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.list());
    }

    /**
     * 新增权限
     */
    @PostMapping
    @PreAuthorize("hasAuthority('permission:add')")
    public Result save(@RequestBody PermissionDTO dto) {
        permissionService.savePermission(dto);
        return Result.success();
    }

    /**
     * 修改权限
     */
    @PutMapping
    @PreAuthorize("hasAuthority('permission:edit')")
    public Result update(@RequestBody PermissionDTO dto) {
        permissionService.updatePermission(dto);
        return Result.success();
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:remove')")
    public Result delete(@PathVariable Integer id) {
        permissionService.removeById(id);
        return Result.success();
    }
}
