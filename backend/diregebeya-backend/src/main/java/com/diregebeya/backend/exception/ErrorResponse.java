package com.diregebeya.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Uniform error payload returned by every endpoint. Clients (frontend/mobile)
 * can rely on this exact shape regardless of which exception was thrown.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    /** Populated only for @Valid validation failures: field name -> message. */
    private final Map<String, String> validationErrors;
}
