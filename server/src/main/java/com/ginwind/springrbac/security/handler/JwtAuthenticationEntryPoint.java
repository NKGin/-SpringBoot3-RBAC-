package com.ginwind.springrbac.security.handler;

import com.alibaba.fastjson.JSON;

import com.ginwind.springrbac.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JwtAuthenticationEntryPoint
 *
 * 用途：
 *   当客户端请求需要认证的资源时，如果没有提供有效的认证信息（如 JWT 为空、过期、无效），
 *   或者请求未经过认证，就会触发这个 Handler。
 *
 * 触发时机：
 *   1. 用户未登录访问受保护接口（SecurityContext 中没有 Authentication）
 *   2. JWT Token 为空、过期或签名不正确，由 JWT Filter 抛出的 AuthenticationException
 *
 * 职责：
 *   - 返回统一的 JSON 响应给前端
 *   - HTTP 状态码为 401（Unauthorized）
 *   - 消息中包含具体的异常信息（如“JWT 已过期”、“JWT 无效”等）
 *
 * 注意：
 *   - 与 LoginFailureHandler 不同，LoginFailureHandler 只处理登录失败（用户名或密码错误）
 *   - 与 AccessDeniedHandler 不同，AccessDeniedHandler 处理的是“已登录但权限不足”的情况（HTTP 403）
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        // 设置 HTTP 状态码 401（未认证）
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 设置响应内容类型和编码
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 构建统一 JSON 响应
        Result result = Result.error(authException.getMessage());

        // 返回给前端
        response.getWriter().write(JSON.toJSONString(result));
    }
}
