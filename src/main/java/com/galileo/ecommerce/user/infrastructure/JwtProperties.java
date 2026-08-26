package com.galileo.ecommerce.user.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app.security.jwt")
public record JwtProperties(String secret, Duration accessTtl, Duration refreshTtl) {
}
