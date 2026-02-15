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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtProperties jwtProperties; // 新增
    private final JwtUtil jwtUtil;


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
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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
                                         StringRedisTemplate stringRedisTemplate,
                                         JwtProperties jwtProperties,
                                         JwtUtil jwtUtil) {

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

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationEntryPoint,
                jwtProperties,
                jwtUtil
        );

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

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

                .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }
}