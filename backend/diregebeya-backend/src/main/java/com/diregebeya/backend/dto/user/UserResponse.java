package com.diregebeya.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

/**
 * What we expose to clients for a user - notably, no password field. Mapping
 * from {@link com.diregebeya.backend.entity.User} to this DTO (rather than
 * serializing the entity directly) is what guarantees the hash can never
 * leak into a JSON response, even if someone adds a field to User later and
 * forgets about serialization.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Set<String> roles;
    private boolean enabled;
    private Instant createdAt;
}
