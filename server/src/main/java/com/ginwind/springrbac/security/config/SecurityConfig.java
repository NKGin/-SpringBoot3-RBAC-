package com.ginwind.springrbac.security.config;

import com.ginwind.springrbac.properties.JwtProperties;
import com.ginwind.springrbac.security.filter.JwtAuthenticationFilter;
import com.ginwind.springrbac.security.filter.JwtLoginFilter;
import com.ginwind.springrbac.security.handler.JwtAccessDeniedHandler;
import com.ginwind.springrbac.security.handler.JwtAuthenticationEntryPoint;
import com.ginwind.springrbac.security.handler.LoginFailureHandler;
import com.ginwind.springrbac.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    // 1. 【修改】移除 jwtAuthenticationFilter 字段，新增构建它所需的 dependencies
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtProperties jwtProperties; // 新增
    private final JwtUtil jwtUtil;

    // 2. 【修改】构造函数注入基础组件（不再注入 Filter 本身）
    public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAccessDeniedHandler accessDeniedHandler,
                          LoginFailureHandler loginFailureHandler,
                          JwtProperties jwtProperties,
                          JwtUtil jwtUtil
                          ) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.jwtProperties = jwtProperties;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 登录 Filter（JSON 登录）
     */
    @Bean
    public JwtLoginFilter jwtLoginFilter(AuthenticationManager authenticationManager,
                                         StringRedisTemplate stringRedisTemplate, // 新增参数
                                         JwtProperties jwtProperties,
                                         JwtUtil jwtUtil) {           // 新增参数

        // 使用新的构造函数，把 Redis 和 配置传进去
        JwtLoginFilter filter = new JwtLoginFilter(
                authenticationManager,
                stringRedisTemplate,
                jwtProperties,
                jwtUtil
        );

        filter.setAuthenticationFailureHandler(loginFailureHandler);
        return filter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtLoginFilter jwtLoginFilter) throws Exception {

        // 3. 【新增】在这里手动 new 出过滤器，传入构造函数需要的组件
        // 这样既解决了依赖问题，又避免了 @Component 导致的重复执行
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationEntryPoint,
                jwtProperties,
                jwtUtil
        );

        http.csrf(AbstractHttpConfigurer::disable)
                // 4. 【删除】千万不要写 .securityMatcher("/login")！
                // 如果写了这行，整个 Security 链就只管 /login 一个接口，其他接口全部失效。

                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/auth/verify",
                                "/logout",
                                "/doc.html",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 添加过滤器
                .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)

                // 5. 【使用】使用刚才手动 new 出来的对象
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }
}