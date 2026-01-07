package com.ginwind.springrbac.security.handler;

import com.alibaba.fastjson.JSON;

import com.ginwind.springrbac.constant.MessageConstant;
import com.ginwind.springrbac.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 用户认证校验失败
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ServletOutputStream out = response.getOutputStream();
        String msg;

        if (exception instanceof BadCredentialsException) {
            msg = MessageConstant.LOGIN_ERROR;

        } else if (exception instanceof LockedException) {
            msg = MessageConstant.ACCOUNT_LOCKED;

        } else if (exception instanceof DisabledException) {
            msg = MessageConstant.ACCOUNT_BANED;

        } else if (exception instanceof AccountExpiredException) {
            msg = MessageConstant.ACCOUNT_EXPIRED;

        } else if (exception instanceof CredentialsExpiredException) {
            msg = MessageConstant.PASSWORD_EXPIRED;
        } else {
            msg =  exception.getMessage();
        }

        String jsonstring = JSON.toJSONString(Result.error(msg));
        out.write(jsonstring.getBytes(StandardCharsets.UTF_8));

        out.flush();
        out.close();
    }
}
