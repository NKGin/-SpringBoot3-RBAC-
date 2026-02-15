package com.ginwind.springrbac.dto;

import lombok.Data;

@Data
public class UserPageDTO {
    private int page = 1;
    private int pageSize = 10;
    private String username;
}