package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class PasswordEditDTO implements Serializable {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}