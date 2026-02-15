package com.ginwind.springrbac.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户DTO
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色ID列表
     */
    private List<Integer> roleIds;
}
