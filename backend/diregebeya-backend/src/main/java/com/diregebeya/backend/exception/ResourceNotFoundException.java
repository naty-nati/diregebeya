package com.diregebeya.backend.exception;

/**
 * Thrown when a lookup by id/unique key finds nothing.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, String field, Object value) {
        return new ResourceNotFoundException(
                "%s not found with %s = '%s'".formatted(resource, field, value));
    }
}
