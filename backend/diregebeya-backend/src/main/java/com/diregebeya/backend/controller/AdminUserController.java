package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.user.UserResponse;
import com.diregebeya.backend.dto.user.UserStaffRoleUpdateRequest;
import com.diregebeya.backend.dto.user.UserStatusUpdateRequest;
import com.diregebeya.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A separate controller/URL space from UserController rather than extra
 * methods bolted onto it - unlike Category/Product (where GET is public and
 * mutations are admin-only on the *same* resource), "list every user" has
 * no customer-facing equivalent at all. Overloading GET /api/users/me into
 * also meaning "list everyone" for admins would make the same URL behave
 * completely differently depending on caller identity - a URL should mean
 * one thing. Every method here is admin-only, so the class carries a single
 * {@code @PreAuthorize} instead of repeating it per method.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Users", description = "Admin management of customer accounts")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "List all users")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Get any user by id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Enable or disable a user account")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody UserStatusUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserStatus(id, request));
    }

    @Operation(summary = "Grant or revoke staff (admin panel) access")
    @PatchMapping("/{id}/staff-role")
    public ResponseEntity<UserResponse> updateStaffRole(@PathVariable Long id,
                                                          @Valid @RequestBody UserStaffRoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateStaffRole(id, request.getStaff()));
    }
}
