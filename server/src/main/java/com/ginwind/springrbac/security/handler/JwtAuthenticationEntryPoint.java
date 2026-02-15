package com.ginwind.springrbac.security.handler;

import com.alibaba.fastjson.JSON;
import com.ginwind.springrbac.constant.HttpMethodConstant;
import com.ginwind.springrbac.constant.MessageConstant;
import com.ginwind.springrbac.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(HttpMethodConstant.UTF_8);
        response.setContentType(HttpMethodConstant.BODY_JSON);

        Result result = Result.error(MessageConstant.JWT_INVALID);

        response.getWriter().write(JSON.toJSONString(result));
    }
}
