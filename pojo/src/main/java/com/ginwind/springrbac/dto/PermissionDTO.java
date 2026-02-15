package com.ginwind.springrbac.dto;

import lombok.Data;

/**
 * 权限DTO
 */
@Data
public class PermissionDTO {

    /**
     * 权限ID
     */
    private Integer id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限路径
     */
    private String permissionPath;

    /**
     * 权限方法
     */
    private String permissionMethod;

    /**
     * 权限描述
     */
    private String description;
}
