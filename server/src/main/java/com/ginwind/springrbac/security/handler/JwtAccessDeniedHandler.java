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

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(
                JSON.toJSONString(
                        Result.error(MessageConstant.INSUFFICIENT_PERMISSIONS)
                )
        );
    }
}
