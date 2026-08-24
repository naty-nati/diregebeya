package com.diregebeya.backend.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Admin-only: enables or disables an account (e.g. to suspend abuse) without deleting it. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateRequest {

    @NotNull(message = "Enabled is required")
    private Boolean enabled;
}
