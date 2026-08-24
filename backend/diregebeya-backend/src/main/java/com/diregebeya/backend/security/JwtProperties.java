package com.diregebeya.backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the {@code app.jwt.*} keys from application.yml. A typed properties
 * class beats scattering {@code @Value("${app.jwt.secret}")} across
 * multiple classes - one place to see every JWT-related setting, and a
 * missing/misnamed property fails fast at startup instead of injecting null
 * into some unrelated field.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationMs, long refreshExpirationMs) {
}
