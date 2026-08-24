package com.diregebeya.backend.exception;

/**
 * Thrown when a change-password request's currentPassword doesn't match
 * what's on file. Kept separate from Spring Security's BadCredentialsException
 * (used at login) so GlobalExceptionHandler can give each a wording suited to
 * its context instead of one generic "Invalid username or password" message
 * showing up on a screen that isn't even asking for a username.
 */
public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}
