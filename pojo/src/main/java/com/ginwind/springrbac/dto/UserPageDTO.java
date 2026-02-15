package com.ginwind.springrbac.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserPageDTO implements Serializable {
    private int page = 1;
    private int pageSize = 10;
    private String username; // 支持按用户名模糊搜索
}