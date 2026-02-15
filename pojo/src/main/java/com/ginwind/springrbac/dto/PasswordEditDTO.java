package com.ginwind.springrbac.dto;

import lombok.Data;

@Data
public class PasswordEditDTO {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}