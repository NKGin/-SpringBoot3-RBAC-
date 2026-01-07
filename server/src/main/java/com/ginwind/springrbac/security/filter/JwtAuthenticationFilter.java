package com.ginwind.springrbac.security.filter;


import com.ginwind.springrbac.security.domain.LoginUser;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtUtils jwtUtils;

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
            System.out.println("运行了认证");


            String token = authHeader.substring(7);

            // 2 token 非法，放行（交给 Security 返回 401/403）
            if (!jwtUtils.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3 从 token 还原 LoginUser


            // 4 防止重复设置
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                "TODO",                     // principal
                                null,
                                null    // 权限
                        );
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

