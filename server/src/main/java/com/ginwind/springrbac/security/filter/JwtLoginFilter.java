package com.ginwind.springrbac.security.filter;

import com.alibaba.fastjson.JSON;
import com.ginwind.springrbac.constant.HttpMethodConstant;
import com.ginwind.springrbac.dto.LoginDTO;
import com.ginwind.springrbac.properties.JwtProperties;
import com.ginwind.springrbac.result.Result;
import com.ginwind.springrbac.security.domain.LoginUser;
import com.ginwind.springrbac.utils.JwtUtil;
import com.ginwind.springrbac.vo.LoginVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * JWT登录过滤器
 */
@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;

    public JwtLoginFilter(AuthenticationManager authenticationManager,
                          StringRedisTemplate stringRedisTemplate,
                          JwtProperties jwtProperties, JwtUtil jwtUtil) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtProperties = jwtProperties;
        this.jwtUtil = jwtUtil;

        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/login");
    }

    /**
     * 尝试认证
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

            if (loginRequest == null) {
                loginRequest = new LoginDTO();
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    );

            return this.getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 认证成功处理
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException {

        LoginUser loginUser = (LoginUser) authResult.getPrincipal();

        try {
            String jwt = jwtUtil.generateToken(
                    jwtProperties.getAdminSecretKey(),
                    loginUser.getUsername(),
                    jwtProperties.getAdminTtl(),
                    loginUser.getAuthorities());

            String tokenKey = jwtProperties.getTokenRedisKey() + jwt;
            stringRedisTemplate.opsForValue().set(
                    tokenKey,
                    jwt,
                    jwtProperties.getAdminTtl() / 1000,
                    TimeUnit.SECONDS
            );

            response.setContentType(HttpMethodConstant.BODY_JSON);
            response.setCharacterEncoding(HttpMethodConstant.UTF_8);
            response.getWriter().write(JSON.toJSONString(Result.success(new LoginVO(loginUser.getId(), loginUser.getUsername(), jwt))));

        } catch (Exception e) {
            log.error("登录认证成功，但后续处理（如Redis）失败: {}", e.getMessage());

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(HttpMethodConstant.BODY_JSON);

            Result<?> errorResult = Result.error("登录成功，但系统内部错误（可能是Redis连接失败）");
            response.getWriter().write(JSON.toJSONString(errorResult));
        }
    }
}
