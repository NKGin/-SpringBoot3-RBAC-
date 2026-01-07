package com.ginwind.springrbac.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("role")
public class Role {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;           // 角色ID

    @TableField("name")
    private String roleName;      // 角色名称

    @TableField("description")
    private String description;   // 描述


    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
