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

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成JWT token并存入Redis
     */
    public String generateToken(String key, String username, long ttlMillis, Collection<? extends GrantedAuthority> permissions) {
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

        String redisKey = getTokenRedisKey(token);
        stringRedisTemplate.opsForValue().set(redisKey, username, ttlMillis, TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 校验Token是否合法
     */
    public boolean validateToken(String key, String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(key))
                    .build()
                    .parseClaimsJws(token);

            String redisKey = getTokenRedisKey(token);
            String redisValue = stringRedisTemplate.opsForValue().get(redisKey);

            if (!StringUtils.hasText(redisValue)) {
                log.warn("Token 在 Redis 中不存在 (可能已注销或强制失效): {}", token);
                return false;
            }

            return true;

        } catch (ExpiredJwtException e) {
            log.info("Token 已过期: {}", e.getMessage());
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
     * 注销/删除Token
     */
    public void deleteToken(String token) {
        if (StringUtils.hasText(token)) {
            stringRedisTemplate.delete(getTokenRedisKey(token));
        }
    }

    /**
     * 解析Claims
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
            throw new RuntimeException("无法解析 Token");
        }
    }

    /**
     * 获取用户名
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

    /**
     * 获取密钥
     */
    private SecretKey getSecretKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取Token的Redis Key
     */
    private String getTokenRedisKey(String token) {
        return RedisKey.TOKEN.getPrefix() + token;
    }
}
