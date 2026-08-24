package com.diregebeya.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI (Swagger) document.
 *
 * Why annotation-based config instead of a @Bean? springdoc-openapi picks up
 * these class-level annotations automatically at startup - no manual OpenAPI
 * bean wiring needed for the common case (title/description/security scheme).
 *
 * Swagger UI will be served at /swagger-ui.html, raw JSON at /v3/api-docs.
 * The "bearerAuth" scheme lets you click "Authorize" in the UI and paste a
 * JWT once it's issued (Phase 2) - every subsequent try-it-out call then
 * automatically includes the Authorization header.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Dire Gebeya E-Commerce API",
                version = "v1",
                description = """
                        REST API for the Dire Gebeya e-commerce platform \
                        (clothes, shoes, watches, perfumes).""",
                contact = @Contact(name = "Dire Gebeya Engineering")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
