package com.ginwind.springrbac.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    /**
     * 管理端密钥
     */
    private String adminSecretKey;

    /**
     * 管理端Token有效期
     */
    private long adminTtl;

    /**
     * 管理端Token名称
     */
    private String adminTokenName;

    /**
     * Token Redis Key
     */
    private String tokenRedisKey;

    /**
     * 用户端密钥
     */
    private String userSecretKey;

    /**
     * 用户端Token有效期
     */
    private long userTtl;

    /**
     * 用户端Token名称
     */
    private String userTokenName;
}
