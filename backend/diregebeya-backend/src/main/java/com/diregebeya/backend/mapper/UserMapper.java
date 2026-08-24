package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.user.UserResponse;
import com.diregebeya.backend.entity.Role;
import com.diregebeya.backend.entity.User;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct generates the implementation at compile time (see
 * target/generated-sources) - no reflection at runtime, and a typo in a
 * field name fails the build instead of silently producing a null field.
 * {@code componentModel = "spring"} makes the generated impl a Spring bean
 * so it can be constructor-injected into services.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
