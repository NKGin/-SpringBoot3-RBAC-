package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    // 前端传递的角色ID列表
    private List<Integer> roleIds;
}