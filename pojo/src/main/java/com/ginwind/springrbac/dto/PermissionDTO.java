package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class PermissionDTO implements Serializable {
    private Integer id;
    private String permissionName;
    private String permissionPath; // 路径，如 /user/**
    private String permissionMethod; // 方法，如 GET, POST
    private String description;
}