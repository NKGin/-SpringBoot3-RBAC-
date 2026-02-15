package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录传输对象
 */
@Data
public class LoginDTO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}