package com.ginwind.springrbac.security.filter;

import com.ginwind.springrbac.constant.*;
import com.ginwind.springrbac.properties.JwtProperties;
import com.ginwind.springrbac.security.handler.*;
import com.ginwind.springrbac.utils.JwtUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
// 1. 【移除】这里去掉了 @Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 2. 【修改】去掉 @Autowired，改为 final 修饰，确保必须通过构造函数注入
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;

    // 3. 【新增】全参构造函数，外部实例化时必须把这些工具类传进来
    public JwtAuthenticationFilter(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                   JwtProperties jwtProperties,
                                   JwtUtil jwtUtil) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtProperties = jwtProperties;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        // ... 下面的业务逻辑保持不变 ...
        // ... 代码省略，和原来一样 ...
        String authHeader = request.getHeader(JwtClaimsConstant.TOKEN_HEADER);
        if (HttpMethodConstant.OPTIONS.equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith(JwtClaimsConstant.TOKEN_PREFIX)) {
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
                if (authHeader.startsWith(JwtClaimsConstant.TOKEN_PREFIX)) {token = authHeader.substring(JwtClaimsConstant.TOKEN_PREFIX.length());}
                else  {token = authHeader;}
                log.info("请求存在Token");
            }

            if (!jwtUtil.validateToken(jwtProperties.getAdminSecretKey(),token)) {
                log.info("Token非法");
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtil.getUsernameFromToken(jwtProperties.getAdminSecretKey(),token);
                List<SimpleGrantedAuthority> authorities = jwtUtil.getAuthoritiesFromToken(jwtProperties.getAdminSecretKey(),token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (AuthenticationException e) {
            jwtAuthenticationEntryPoint.commence(request, response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }
}