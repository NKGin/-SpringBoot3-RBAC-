package com.ginwind.springrbac.security.handler;

import com.alibaba.fastjson.JSON;
import com.ginwind.springrbac.constant.MessageConstant;
import com.ginwind.springrbac.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
// 接口                 发生阶段   用户状态          处理的异常类型           常见 HTTP 码   对应的 Filter
// AccessDeniedHandler 访问资源时  已登录 (权限不足)  AccessDeniedException 403 Forbidden ExceptionTranslationFilter
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.getWriter().write(
                JSON.toJSONString(
                        Result.error(MessageConstant.INSUFFICIENT_PERMISSIONS)
                )
        );
    }
}
