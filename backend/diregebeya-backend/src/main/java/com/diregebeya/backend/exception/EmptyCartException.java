package com.diregebeya.backend.exception;

/**
 * Thrown when checkout is attempted with no items in the cart.
 * Mapped to HTTP 400 (Bad Request) by {@link GlobalExceptionHandler}.
 */
public class EmptyCartException extends RuntimeException {

    public EmptyCartException(String message) {
        super(message);
    }
}
