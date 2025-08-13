package com.dmt.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret = "dmtSecretKeyForJWTTokenGenerationAndValidation2024";
    private long expiration = 86400000; // 24시간 (밀리초)
    private String issuer = "dmt-app";
} 