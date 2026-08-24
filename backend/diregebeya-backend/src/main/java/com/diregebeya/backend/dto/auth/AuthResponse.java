package com.diregebeya.backend.dto.auth;

import com.diregebeya.backend.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Returned by both register and login - the client gets the token it needs
 * to authenticate subsequent requests plus the current user's profile in a
 * single round trip, instead of forcing an immediate follow-up call to
 * GET /api/users/me.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresInMs;
    private UserResponse user;
}
