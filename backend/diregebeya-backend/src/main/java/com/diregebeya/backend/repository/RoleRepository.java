package com.diregebeya.backend.repository;

import com.diregebeya.backend.entity.ERole;
import com.diregebeya.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
