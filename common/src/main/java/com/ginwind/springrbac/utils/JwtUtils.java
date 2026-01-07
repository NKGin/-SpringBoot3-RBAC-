package com.ginwind.springrbac.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class JwtUtils {

    public static final long JWT_TTL = 3600*1000L;
    private static final String SECRET =
            "ginwind_jwt_secret_key_1234567890123456";
    private static final String PERMISSIONS_CLAIM = "permissions";
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // token 有效期（毫秒），这里设置 1 小时
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 根据用户名生成 JWT token
     */
    public static String generateToken(String username,Collection<? extends GrantedAuthority> permissions) {
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
                        KEY,
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public boolean validateToken(String token) {
        String redisStr = stringRedisTemplate.opsForValue().get("token_"+token);
        if (ObjectUtils.isEmpty(redisStr)) {

            System.out.println("redis中没有，token非法");
            return false;
        }
        boolean expire = token != null;
        if (expire) {
            System.out.println("token合法");
        }else {
            System.out.println("token非法");
        }
        return expire;
    }
}
