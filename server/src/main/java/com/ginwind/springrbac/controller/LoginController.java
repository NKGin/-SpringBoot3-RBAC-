package com.ginwind.springrbac.controller;


import com.ginwind.springrbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;


    @PreAuthorize("hasAnyAuthority('user:list')")
    @GetMapping("/hello")
    public String hello(){

        return "Hello";
    }
    @GetMapping("/admin")
    public String admin(){

        return "This is admin";
    }



}
