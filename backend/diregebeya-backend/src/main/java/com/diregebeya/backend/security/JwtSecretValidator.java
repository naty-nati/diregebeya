package com.diregebeya.backend.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Fails startup outside the dev profile if JWT_SECRET was never overridden
 * from its placeholder default in application.yml - silently signing every
 * token with a secret anyone can read out of source control would let
 * anyone forge a valid admin token. Dev is exempt so a fresh checkout still
 * runs with zero configuration.
 */
@Component
@RequiredArgsConstructor
public class JwtSecretValidator {

    private static final String PLACEHOLDER_SECRET = "change-this-in-prod-to-a-long-random-base64-secret";

    private final JwtProperties jwtProperties;
    private final Environment environment;

    @PostConstruct
    void validate() {
        boolean isDev = environment.matchesProfiles("dev");
        if (!isDev && PLACEHOLDER_SECRET.equals(jwtProperties.secret())) {
            throw new IllegalStateException(
                    "JWT_SECRET is still the placeholder default outside the dev profile. Set a real, "
                            + "random JWT_SECRET environment variable before starting this application in any "
                            + "non-dev environment - refusing to start with a publicly-known signing key.");
        }
    }
}
