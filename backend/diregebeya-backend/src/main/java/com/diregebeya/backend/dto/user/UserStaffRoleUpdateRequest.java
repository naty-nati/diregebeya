package com.diregebeya.backend.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Admin-only: grants or revokes ROLE_STAFF without touching any other role the user holds. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStaffRoleUpdateRequest {

    @NotNull(message = "Staff is required")
    private Boolean staff;
}
