package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.auth.AuthResponse;
import com.diregebeya.backend.dto.auth.LoginRequest;
import com.diregebeya.backend.dto.auth.RegisterRequest;
import com.diregebeya.backend.entity.ERole;
import com.diregebeya.backend.entity.Role;
import com.diregebeya.backend.entity.User;
import com.diregebeya.backend.exception.DuplicateResourceException;
import com.diregebeya.backend.mapper.UserMapper;
import com.diregebeya.backend.repository.RoleRepository;
import com.diregebeya.backend.repository.UserRepository;
import com.diregebeya.backend.security.JwtProperties;
import com.diregebeya.backend.security.JwtService;
import com.diregebeya.backend.security.UserPrincipal;
import com.diregebeya.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Why an interface + impl split here (rather than one concrete class,
 * as the rest of the stack will use elsewhere)? Because Phase 2 is explicitly
 * the module the spec calls out for it - it keeps AuthController depending
 * on a contract, not an implementation, so a future alternate flow
 * (e.g. OAuth2 login) could implement the same interface without touching
 * the controller.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "An account with email '%s' already exists".formatted(request.getEmail()));
        }

        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_CUSTOMER is missing from the database - check the role seeder ran"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(customerRole))
                .build();

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(new UserPrincipal(saved));
        return AuthResponse.builder()
                .accessToken(token)
                .expiresInMs(jwtProperties.expirationMs())
                .user(userMapper.toResponse(saved))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Delegates credential checking to Spring Security's own
        // ProviderManager, which uses CustomUserDetailsService +
        // PasswordEncoder under the hood. Throws BadCredentialsException on
        // failure - already mapped to a 401 by GlobalExceptionHandler.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException(
                        "Authentication succeeded but user disappeared - this should be unreachable"));

        String token = jwtService.generateToken(new UserPrincipal(user));
        return AuthResponse.builder()
                .accessToken(token)
                .expiresInMs(jwtProperties.expirationMs())
                .user(userMapper.toResponse(user))
                .build();
    }
}
