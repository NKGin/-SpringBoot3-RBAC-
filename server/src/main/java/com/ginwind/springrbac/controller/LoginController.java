package com.ginwind.springrbac.controller;

import com.ginwind.springrbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 */
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 测试接口
     */
    @PreAuthorize("hasAnyAuthority('user:list')")
    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    /**
     * 管理员接口
     */
    @GetMapping("/admin")
    public String admin() {
        return "This is admin";
    }
}
