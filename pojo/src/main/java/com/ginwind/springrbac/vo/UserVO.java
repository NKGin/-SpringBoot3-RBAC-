package com.ginwind.springrbac.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String status;
    private LocalDateTime createTime;
    private List<Integer> roleIds; // 回显当前拥有的角色ID
}