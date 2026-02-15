package com.ginwind.springrbac.utils;


import com.ginwind.springrbac.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 确保已登录且 Principal 不是匿名用户字符串
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getId();
        }
        // 未登录或匿名访问时返回默认值（如系统管理员ID 1L）
        return 1L;
    }
}