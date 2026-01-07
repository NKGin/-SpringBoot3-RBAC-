package com.ginwind.springrbac.controller;


import com.ginwind.springrbac.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/hello")
    public String hello(){

        return "Hello";
    }
    @GetMapping("/admin")
    public String admin(){

        return "This is admin";
    }



}
