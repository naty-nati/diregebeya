package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.auth.AuthResponse;
import com.diregebeya.backend.dto.auth.LoginRequest;
import com.diregebeya.backend.dto.auth.RegisterRequest;
import com.diregebeya.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public entry points - no token required, which is why both endpoints are
 * listed in SecurityConfig's permitAll list and additionally annotated with
 * {@code @SecurityRequirements} (empty) so Swagger UI doesn't show a padlock
 * on them despite the global bearerAuth requirement in OpenApiConfig.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration and login")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new customer account")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Log in and receive a JWT access token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
