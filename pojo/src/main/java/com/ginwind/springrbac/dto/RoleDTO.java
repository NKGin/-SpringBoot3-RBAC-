package com.ginwind.springrbac.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色DTO
 */
@Data
public class RoleDTO {

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限ID列表
     */
    private List<Integer> permissionIds;
}
