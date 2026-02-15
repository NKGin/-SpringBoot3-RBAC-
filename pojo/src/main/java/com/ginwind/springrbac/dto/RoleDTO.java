package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class RoleDTO implements Serializable {
    private Integer id;
    private String roleName;
    private String description;
    // 前端传来的权限ID列表
    private List<Integer> permissionIds;
}