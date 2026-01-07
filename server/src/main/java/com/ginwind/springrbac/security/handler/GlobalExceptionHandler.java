package com.ginwind.springrbac.security.handler;

import com.ginwind.springrbac.result.Result;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseBody
    public Result handleAccessDenied(AuthorizationDeniedException e) {
        return Result.error("权限不足，禁止访问");
    }
}
