package com.diregebeya.backend.exception;

/**
 * Thrown when a requested cart quantity exceeds available stock.
 * Mapped to HTTP 400 (Bad Request) by {@link GlobalExceptionHandler} - it's
 * a client asking for more than exists, not a server-side problem.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
