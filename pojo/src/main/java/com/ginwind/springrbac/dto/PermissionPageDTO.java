package com.ginwind.springrbac.dto;

import lombok.Data;

@Data
public class PermissionPageDTO {
    private int page = 1;
    private int pageSize = 10;
    private String name;
}