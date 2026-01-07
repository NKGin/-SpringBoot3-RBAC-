package com.ginwind.springrbac.security.config;


import com.ginwind.springrbac.security.filter.JwtAuthenticationFilter;
import com.ginwind.springrbac.security.filter.JwtLoginFilter;
import com.ginwind.springrbac.security.handler.JwtAccessDeniedHandler;
import com.ginwind.springrbac.security.handler.JwtAuthenticationEntryPoint;
import com.ginwind.springrbac.security.handler.LoginFailureHandler;
import com.ginwind.springrbac.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final LoginFailureHandler loginFailureHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAccessDeniedHandler accessDeniedHandler,
                          LoginFailureHandler loginFailureHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 登录 Filter（JSON 登录）
     */
    @Bean
    public JwtLoginFilter jwtLoginFilter(AuthenticationManager authenticationManager) {

        JwtLoginFilter filter =
                new JwtLoginFilter(authenticationManager);

        filter.setAuthenticationFailureHandler(loginFailureHandler);
        return filter;
    }

    /**
     * Security 核心配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtLoginFilter jwtLoginFilter)
            throws Exception {

                // 1. 关闭 csrf（JWT 必须）
        http.csrf(AbstractHttpConfigurer::disable)
                // 绑定filterChain管理路径
                .securityMatcher("/login")
                // 2. 不使用 formLogin
                .formLogin(AbstractHttpConfigurer::disable)

                // 3. 请求授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/auth/verify","/logout","/doc.html","/doc.html",
                                "/webjars/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 4. 添加过滤器
                // 登录过滤器（替换 UsernamePasswordAuthenticationFilter）
                .addFilterAt(jwtLoginFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // JWT 校验过滤器
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // 5. 异常处理
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );


        return http.build();
    }


}
