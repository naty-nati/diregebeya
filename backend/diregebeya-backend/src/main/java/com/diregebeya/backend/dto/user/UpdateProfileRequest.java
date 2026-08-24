package com.diregebeya.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Deliberately only fullName - not email. Email is the login identifier;
 * letting it change here with no verification step would let an account
 * silently repoint its own credential to an unowned address. A real email
 * change belongs behind its own confirm-via-link flow, which is out of
 * scope for this phase.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;
}
