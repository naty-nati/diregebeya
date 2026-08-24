package com.diregebeya.backend.exception;

/**
 * Thrown when a coupon code doesn't exist, is inactive, has expired, or has
 * hit its redemption limit. Mapped to HTTP 400 (Bad Request) by
 * {@link GlobalExceptionHandler} - deliberately not 404, since from the
 * caller's perspective "SAVE10 doesn't exist" and "SAVE10 expired
 * yesterday" are both just "this code isn't usable right now", not a
 * missing-resource lookup failure.
 */
public class InvalidCouponException extends RuntimeException {

    public InvalidCouponException(String message) {
        super(message);
    }
}
