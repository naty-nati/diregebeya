package com.diregebeya.backend.exception;

/**
 * Thrown when an order operation is attempted from a status that doesn't
 * allow it - e.g. cancelling an order that has already shipped.
 * Mapped to HTTP 400 (Bad Request) by {@link GlobalExceptionHandler}.
 */
public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
