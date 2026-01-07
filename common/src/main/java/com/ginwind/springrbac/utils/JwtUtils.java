package com.ginwind.springrbac.utils;

import com.ginwind.springrbac.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class JwtUtils {
    
    @Autowired
    private  StringRedisTemplate stringRedisTemplate;
    /**
     * 根据用户名生成 JWT token
     */
    public static String generateToken(String key,String username,Collection<? extends GrantedAuthority> permissions) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("permissions", permissions
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(
                        getSecretKey(key) ,
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    /**
     * 校验 Token 是否合法
     * 1. 校验格式、签名、过期时间 (由 JJWT 库完成)
     * 2. 校验 Redis 中是否存在 (由业务逻辑完成，用于处理注销/黑名单)
     *
     * @param token JWT 字符串
     * @return true=合法, false=非法
     */
    public boolean validateToken(String key,String token) {
        if (token == null || token.isEmpty()) return false;

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(key) )
                    .build()
                    .parseClaimsJws(token);

            // Redis 校验逻辑...
            String redisStr = stringRedisTemplate.opsForValue().get("token_" + token);
            return !ObjectUtils.isEmpty(redisStr);

        } catch (ExpiredJwtException e) {
            System.out.println("Token 已过期");
            return false;

        } catch (io.jsonwebtoken.security.SecurityException e) {
            // 这里改用了 SecurityException，它包含了 SignatureException
            System.out.println("Token 签名无效或安全性验证失败");
            return false;

        } catch (JwtException e) {
            // JwtException 是所有 JWT 异常的父类，作为兜底
            System.out.println("Token 解析失败");
            return false;
        }
    }
    /**
     * 从 token 中解析出 Claims (载荷信息)
     * 注意：如果 token 签名无效、过期或格式错误，这里会抛出异常
     *
     * @param token JWT 字符串
     * @return Claims 对象
     */
    public static Claims getClaimsFromToken(String key,String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(key)) // 使用生成 token 时相同的密钥进行解密/校验
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 直接从 token 获取用户名 (Subject)
     */
    public static String getUsernameFromToken(String key,String token) {
        return getClaimsFromToken(key,token).getSubject();
    }


    /**
     * 从 Token 中获取权限列表，并转换为 Spring Security 需要的格式
     */
    public static List<SimpleGrantedAuthority> getAuthoritiesFromToken(String key,String token) {
        Claims claims = getClaimsFromToken(key,token);

        // 1. 获取原始的 List<String>
        // 注意：这里必须和 generateToken 中存入的 key ("permissions") 保持一致
        List<String> permissions = claims.get("permissions", List.class);

        if (permissions == null || permissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 将 List<String> 转换为 List<SimpleGrantedAuthority>
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public static SecretKey  getSecretKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

}
