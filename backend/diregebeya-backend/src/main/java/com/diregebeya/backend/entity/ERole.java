package com.diregebeya.backend.entity;

/**
 * The "ROLE_" prefix is a Spring Security convention: {@code hasRole("ADMIN")}
 * checks look for an authority literally named "ROLE_ADMIN". Storing the
 * prefix in the enum itself (instead of prepending it at authority-mapping
 * time) keeps that detail in one place.
 */
public enum ERole {
    ROLE_ADMIN,
    ROLE_STAFF,
    ROLE_CUSTOMER
}
