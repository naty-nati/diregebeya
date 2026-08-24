package com.diregebeya.backend.config;

import com.diregebeya.backend.entity.ERole;
import com.diregebeya.backend.entity.Role;
import com.diregebeya.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Roles are reference data, not user-generated content - they need to exist
 * before the first registration ever runs (AuthServiceImpl looks up
 * ROLE_CUSTOMER by name). Seeding on startup instead of a manual SQL insert
 * means a fresh dev database or a new environment "just works" the first
 * time `mvn spring-boot:run` executes. Once Flyway/Liquibase is introduced
 * (see application-prod.yml), this moves into a versioned migration instead.
 */
@Component
@RequiredArgsConstructor
@Order(1)
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        for (ERole role : ERole.values()) {
            if (roleRepository.findByName(role).isEmpty()) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            }
        }
    }
}
