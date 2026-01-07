package com.ginwind.springrbac.security.filter;

import com.alibaba.fastjson.JSON;


import com.ginwind.springrbac.dto.LoginDTO;
import com.ginwind.springrbac.result.Result;
import com.ginwind.springrbac.security.domain.LoginUser;
import com.ginwind.springrbac.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtils jwtUtils;
    @Autowired
    private StringRedisTemplate  stringRedisTemplate;

    public JwtLoginFilter(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/login"); // 拦截登录接口
    }

    /**
     * ① 接收前端 JSON 登录数据
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginDTO loginRequest = JSON.parseObject(
                    request.getInputStream(),
                    StandardCharsets.UTF_8,
                    LoginDTO.class
            );
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    );

            return this.getAuthenticationManager()
                    .authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("登录数据解析失败");
        }
    }

    /**
     * ② 登录成功
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException {

        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwt = JwtUtils.generateToken(loginUser.getUsername(),loginUser.getAuthorities());

        //jwt键
        String tokenKey = "token_"+jwt;
        //存储redis白名单
        stringRedisTemplate.opsForValue().set(tokenKey, jwt,JwtUtils.JWT_TTL/1000, TimeUnit.SECONDS);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.success(jwt))

        );
    }
}
