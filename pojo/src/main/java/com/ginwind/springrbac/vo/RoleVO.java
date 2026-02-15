package com.ginwind.springrbac.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleVO {
    private Integer id;
    private String roleName;
    private String description;
    private LocalDateTime createTime;
    // 用于回显，告知前端该角色当前拥有哪些权限ID
    private List<Integer> permissionIds;
}