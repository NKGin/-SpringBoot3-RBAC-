package com.ginwind.springrbac.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("permission")
public class Permission {

    /**
     * 权限ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 权限名称
     */
    @TableField("name")
    private String permissionName;

    /**
     * 权限路径
     */
    @TableField("path")
    private String permissionPath;

    /**
     * 权限方法
     */
    @TableField("method")
    private String permissionMethod;

    /**
     * 权限描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建人ID
     */
    @TableField(value = "create_id", fill = FieldFill.INSERT)
    private Long createId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    @TableField(value = "update_id", fill = FieldFill.INSERT_UPDATE)
    private Long updateId;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
