package com.diregebeya.backend.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds (and repairs) a known-good admin login on every startup.
 *
 * Deliberately uses plain JDBC instead of the JPA repositories: {@code
 * User.roles} is eager-loaded and {@code Role.name} is a strict enum column,
 * so if this account's role row was ever hand-inserted with a name that
 * doesn't match {@link com.diregebeya.backend.entity.ERole} exactly (e.g.
 * "Admin" instead of "ROLE_ADMIN"), even reading the user through Hibernate
 * throws and takes login down with it. Raw SQL here can inspect/fix that row
 * without tripping the same mapping, guaranteeing a working admin login
 * regardless of whatever bad state is already in the database.
 */
@Component
@RequiredArgsConstructor
@Order(2)
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.email:admin@demo.com}")
    private String adminEmail;

    @Value("${app.seed.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${app.seed.admin.full-name:Demo Admin}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        List<Long> roleIds = jdbcTemplate.queryForList(
                "SELECT id FROM roles WHERE name = 'ROLE_ADMIN'", Long.class);
        if (roleIds.isEmpty()) {
            log.warn("ROLE_ADMIN not found in roles table - skipping admin seed (did RoleSeeder run?)");
            return;
        }
        Long roleId = roleIds.get(0);

        String encodedPassword = passwordEncoder.encode(adminPassword);
        List<Long> existing = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE email = ?", Long.class, adminEmail);

        Long userId;
        if (existing.isEmpty()) {
            userId = jdbcTemplate.queryForObject(
                    "INSERT INTO users (full_name, email, password, enabled, created_at, updated_at) "
                            + "VALUES (?, ?, ?, true, now(), now()) RETURNING id",
                    Long.class, adminFullName, adminEmail, encodedPassword);
            log.info("Seeded demo admin account: {}", adminEmail);
        } else {
            userId = existing.get(0);
            jdbcTemplate.update(
                    "UPDATE users SET password = ?, enabled = true, updated_at = now() WHERE id = ?",
                    encodedPassword, userId);
            log.info("Demo admin account already existed - refreshed credentials and role: {}", adminEmail);
        }

        // Force the role link to the correct row, regardless of whatever
        // (possibly mis-seeded) mapping existed before.
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
        jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", userId, roleId);
    }
}
