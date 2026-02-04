package com.ginwind.springrbac.enumeration; // 建议放在 constant 包下

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKey {

    // 定义枚举项，括号里是对应的值
    TOKEN("token:"),
    USER_LOGIN("user:login:"), // 以后可以扩展其他 Key
    ORDER_ID("order:id:");

    private final String prefix;
}