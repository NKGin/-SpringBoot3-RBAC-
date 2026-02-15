package com.ginwind.springrbac.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ginwind.springrbac.constant.MessageConstant;
import com.ginwind.springrbac.dto.PasswordEditDTO;
import com.ginwind.springrbac.dto.UserDTO;
import com.ginwind.springrbac.dto.UserPageDTO;
import com.ginwind.springrbac.result.Result;
import com.ginwind.springrbac.service.UserService;
import com.ginwind.springrbac.utils.SecurityUtils;
import com.ginwind.springrbac.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<Page<UserVO>> page(UserPageDTO userPageDTO) {
        Page<UserVO> page = userService.pageQuery(userPageDTO);
        return Result.success(page);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    public Result save(@RequestBody UserDTO userDTO) {
        userService.saveUser(userDTO);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @PutMapping
    @PreAuthorize("hasAuthority('user:update')")
    public Result update(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return Result.success();
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<UserVO> getById(@PathVariable Long id) {
        UserVO userVO = userService.getUserDetail(id);
        return Result.success(userVO);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result delete(@PathVariable List<Long> ids) {
        if (ids.contains(1L)) {
            return Result.error("不可删除管理员");
        }
        userService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 启用/禁用用户
     */
    @PostMapping("/status/{id}/{status}")
    @PreAuthorize("hasAuthority('user:update')")
    public Result status(@PathVariable Long id, @PathVariable String status) {
        log.info("启用/禁用账号:传入参数id：{}，status：{}", id, status);
        userService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/resetPassword/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public Result resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        passwordEditDTO.setUserId(SecurityUtils.getCurrentUserId());
        try {
            userService.editPassword(passwordEditDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
