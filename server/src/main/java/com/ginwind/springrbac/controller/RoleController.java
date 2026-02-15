package com.ginwind.springrbac.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ginwind.springrbac.dto.RoleDTO;
import com.ginwind.springrbac.dto.RolePageDTO;
import com.ginwind.springrbac.entity.Role;
import com.ginwind.springrbac.result.Result;
import com.ginwind.springrbac.service.RoleService;
import com.ginwind.springrbac.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<Page<Role>> page(RolePageDTO dto) {
        return Result.success(roleService.pageQuery(dto));
    }

    /**
     * 查询所有角色
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<Role>> list() {
        return Result.success(roleService.list());
    }

    /**
     * 根据ID查询角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:query')")
    public Result<RoleVO> getById(@PathVariable Integer id) {
        log.info("角色id{},拥有的权限：{}", id, roleService.getRoleDetail(id).getPermissionIds());
        return Result.success(roleService.getRoleDetail(id));
    }

    /**
     * 新增角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('role:add')")
    public Result save(@RequestBody RoleDTO roleDTO) {
        roleService.saveRole(roleDTO);
        return Result.success();
    }

    /**
     * 修改角色
     */
    @PutMapping
    @PreAuthorize("hasAuthority('role:edit')")
    public Result update(@RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:remove')")
    public Result delete(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return Result.success();
    }
}
