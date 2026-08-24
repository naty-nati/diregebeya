package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.user.ChangePasswordRequest;
import com.diregebeya.backend.dto.user.UpdateProfileRequest;
import com.diregebeya.backend.dto.user.UserResponse;
import com.diregebeya.backend.dto.user.UserStatusUpdateRequest;
import com.diregebeya.backend.entity.ERole;
import com.diregebeya.backend.entity.Role;
import com.diregebeya.backend.entity.User;
import com.diregebeya.backend.exception.InvalidPasswordException;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.UserMapper;
import com.diregebeya.backend.repository.RoleRepository;
import com.diregebeya.backend.repository.UserRepository;
import com.diregebeya.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.of("User", "email", email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findByIdOrThrow(userId);
        user.setFullName(request.getFullName());

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findByIdOrThrow(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return userMapper.toResponse(findByIdOrThrow(userId));
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request) {
        User user = findByIdOrThrow(userId);
        user.setEnabled(request.getEnabled());

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateStaffRole(Long userId, boolean staff) {
        User user = findByIdOrThrow(userId);
        Role staffRole = roleRepository.findByName(ERole.ROLE_STAFF)
                .orElseThrow(() -> ResourceNotFoundException.of("Role", "name", ERole.ROLE_STAFF));

        if (staff) {
            user.getRoles().add(staffRole);
        } else {
            user.getRoles().remove(staffRole);
        }

        return userMapper.toResponse(user);
    }

    private User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", "id", userId));
    }
}
