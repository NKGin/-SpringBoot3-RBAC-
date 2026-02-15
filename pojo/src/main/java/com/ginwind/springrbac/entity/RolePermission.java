package com.ginwind.springrbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色权限关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("role_permission")
public class RolePermission {

    /**
     * 角色ID
     */
    @TableId(value = "role_id")
    private Integer roleId;

    /**
     * 权限ID
     */
    @TableField("permission_id")
    private Integer permissionId;
}
