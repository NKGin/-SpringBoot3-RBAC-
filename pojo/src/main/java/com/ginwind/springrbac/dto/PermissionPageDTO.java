package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class PermissionPageDTO implements Serializable {
    private int page = 1;
    private int pageSize = 10;
    private String name; // 按权限名称搜索
}