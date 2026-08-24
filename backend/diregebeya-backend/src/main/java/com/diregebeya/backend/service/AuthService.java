package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.auth.AuthResponse;
import com.diregebeya.backend.dto.auth.LoginRequest;
import com.diregebeya.backend.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
