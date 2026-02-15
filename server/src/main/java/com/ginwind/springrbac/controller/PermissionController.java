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

//@CrossOrigin(origins ="http://localhost:3000")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<Page<Permission>> page(PermissionPageDTO dto) {
        return Result.success(permissionService.pageQuery(dto));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.list());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:add')")
    public Result save(@RequestBody PermissionDTO dto) {
        permissionService.savePermission(dto);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('permission:edit')")
    public Result update(@RequestBody PermissionDTO dto) {
        permissionService.updatePermission(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:remove')")
    public Result delete(@PathVariable Integer id) {
        // TODO: 可选增强 - 检查是否有角色关联了该权限，如果有则禁止删除
        permissionService.removeById(id);
        return Result.success();
    }
}