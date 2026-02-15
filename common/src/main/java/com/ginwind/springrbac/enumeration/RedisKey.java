package com.ginwind.springrbac.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKey {

    TOKEN("token:"),
    USER_LOGIN("user:login:"),
    ORDER_ID("order:id:");

    private final String prefix;
}