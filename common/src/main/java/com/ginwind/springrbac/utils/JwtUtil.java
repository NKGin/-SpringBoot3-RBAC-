package com.ginwind.springrbac.utils;

import com.ginwind.springrbac.constant.JwtClaimsConstant;
import com.ginwind.springrbac.enumeration.RedisKey;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成 JWT token 并存入 Redis
     * 去掉了 static，以便使用 stringRedisTemplate
     */
    public String generateToken(String key, String username, long ttlMillis, Collection<? extends GrantedAuthority> permissions) {
        // 1. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        if (permissions != null && !permissions.isEmpty()) {
            claims.put(JwtClaimsConstant.PERMISSIONS, permissions
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttlMillis);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSecretKey(key), SignatureAlgorithm.HS256)
                .compact();

        // 2. 【补全逻辑】将 Token 存入 Redis，实现双重校验 (白名单机制)
        // Key: token:eyJxh...  Value: username (或者存 1)
        // 过期时间与 JWT 本身保持一致
        String redisKey = getTokenRedisKey(token);
        stringRedisTemplate.opsForValue().set(redisKey, username, ttlMillis, TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 校验 Token 是否合法
     * 修复了异常处理逻辑，统一返回 boolean，方便 Filter 调用
     */
    public boolean validateToken(String key, String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            // 1. 校验 JWT 格式、签名、过期时间 (JJWT 库完成)
            // 如果解析失败，parserBuilder 会直接抛出异常，进入 catch 块
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(key))
                    .build()
                    .parseClaimsJws(token);

            // 2. 校验 Redis 中是否存在 (处理注销/强制下线)
            String redisKey = getTokenRedisKey(token);
            String redisValue = stringRedisTemplate.opsForValue().get(redisKey);

            if (!StringUtils.hasText(redisValue)) {
                log.warn("Token 在 Redis 中不存在 (可能已注销或强制失效): {}", token);
                return false;
            }

            return true;

        } catch (ExpiredJwtException e) {
            log.info("Token 已过期: {}", e.getMessage());
            // 如果你希望在 Filter 层给前端返回具体的 "Token Expired" 错误码，
            // 这里可以抛出自定义异常，但标准做法是返回 false，由 Filter 处理 401
            return false;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Token 签名无效或格式错误: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token 解析失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 注销/删除 Token
     * (新增方法，配合 validateToken 使用)
     */
    public void deleteToken(String token) {
        if (StringUtils.hasText(token)) {
            stringRedisTemplate.delete(getTokenRedisKey(token));
        }
    }

    /**
     * 解析 Claims
     * 去掉 static
     */
    public Claims getClaimsFromToken(String key, String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(key))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析 Token Claims 失败", e);
            throw new RuntimeException("无法解析 Token"); // 或者抛出自定义异常
        }
    }

    /**
     * 获取用户名
     * 去掉 static
     */
    public String getUsernameFromToken(String key, String token) {
        try {
            return getClaimsFromToken(key, token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取权限列表
     * 去掉 static
     */
    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String key, String token) {
        try {
            Claims claims = getClaimsFromToken(key, token);
            List<String> permissions = claims.get(JwtClaimsConstant.PERMISSIONS, List.class);

            if (permissions == null || permissions.isEmpty()) {
                return new ArrayList<>();
            }

            return permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("解析权限失败", e);
            return new ArrayList<>();
        }
    }

    private SecretKey getSecretKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    private String getTokenRedisKey(String token) {
        return RedisKey.TOKEN.getPrefix()+token;
    }
}