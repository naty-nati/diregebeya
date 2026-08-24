package com.diregebeya.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Minimal endpoint to confirm the application context starts, the web layer
 * responds, and Swagger picks up a real controller. Every future phase
 * builds on top of this - it's the "hello world" smoke test.
 */
@RestController
@Tag(name = "Health", description = "Service health check")
public class HealthController {

    @Operation(summary = "Health check", description = "Returns service status and current server time")
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "diregebeya-backend",
                "timestamp", Instant.now()
        ));
    }
}
