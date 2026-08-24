package com.diregebeya.backend.exception;

/**
 * Thrown on attempts to create a resource that violates a uniqueness rule
 * (e.g. registering with an email that already exists).
 * Mapped to HTTP 409 (Conflict) by {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
