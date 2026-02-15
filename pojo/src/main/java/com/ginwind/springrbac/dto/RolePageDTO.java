package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class RolePageDTO implements Serializable {
    private int page = 1;
    private int pageSize = 10;
    private String roleName;
}