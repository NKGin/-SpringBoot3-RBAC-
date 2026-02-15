package com.ginwind.springrbac.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 */
@Data
public class RoleVO {

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 权限ID列表
     */
    private List<Integer> permissionIds;
}
