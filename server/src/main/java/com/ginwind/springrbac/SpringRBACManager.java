package com.ginwind.springrbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringRBACManager {
    public static void main(String[] args) {
        SpringApplication.run(SpringRBACManager.class, args);
    }

    @GetMapping
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
