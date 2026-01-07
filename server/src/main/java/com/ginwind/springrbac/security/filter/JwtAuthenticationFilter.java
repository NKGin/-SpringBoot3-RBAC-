package com.ginwind.springrbac.security.filter;


import com.ginwind.springrbac.properties.JwtProperties;
import com.ginwind.springrbac.security.handler.*;
import com.ginwind.springrbac.utils.JwtUtils;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        String authHeader = request.getHeader("Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        // 1 没 token，直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (request.getRequestURI().contains("/login")
                    ||request.getRequestURI().contains("/logout")
                    ||request.getRequestURI().contains("/doc.html")
                    ||request.getRequestURI().contains("/v3/api-docs")
                    ||request.getRequestURI().contains("/webjars")
                    ||request.getRequestURI().contains("/swagger-ui")
            ) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {

            String token = null;
            if (authHeader != null) {
                token = authHeader.substring(7);
                log.info("Token存在");
            }

            // 2 token 非法，放行（交给 Security 返回 401/403）
            if (!jwtUtils.validateToken(jwtProperties.getAdminSecretKey(),token)) {
                log.info("Token非法");
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // 1. 解析用户名 (填入 Principal)
                String username = JwtUtils.getUsernameFromToken(jwtProperties.getAdminSecretKey(),token);

                // 2. 解析权限 (填入 Authorities)
                List<SimpleGrantedAuthority> authorities = JwtUtils.getAuthoritiesFromToken(jwtProperties.getAdminSecretKey(),token);

                // 3. 构建完整的认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,       // 参数1：用户名 (或者 UserDetails)
                                null,           // 参数2：密码 (已认证不需要密码)
                                authorities     // 参数3：刚才解析出来的权限列表
                        );

                // 4. 放入上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (AuthenticationException e) {
            jwtAuthenticationEntryPoint.commence(request, response, e);
            return; // 捕获异常后直接返回，避免继续放行
        }
        // 5放行
        filterChain.doFilter(request, response);
    }
}

