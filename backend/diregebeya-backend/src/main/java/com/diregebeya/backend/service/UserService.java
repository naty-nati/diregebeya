package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.user.ChangePasswordRequest;
import com.diregebeya.backend.dto.user.UpdateProfileRequest;
import com.diregebeya.backend.dto.user.UserResponse;
import com.diregebeya.backend.dto.user.UserStaffRoleUpdateRequest;
import com.diregebeya.backend.dto.user.UserStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getByEmail(String email);

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    /** Admin only. */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /** Admin only - unlike getByEmail, this is not restricted to "your own" profile. */
    UserResponse getUserById(Long userId);

    /** Admin only. */
    UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request);

    /** Admin only - grants or revokes ROLE_STAFF without touching any other role the user holds. */
    UserResponse updateStaffRole(Long userId, boolean staff);
}
