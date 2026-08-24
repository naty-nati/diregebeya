package com.diregebeya.backend.config;

import com.diregebeya.backend.security.JwtAuthenticationFilter;
import com.diregebeya.backend.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * PHASE 2.
 *
 * Replaces the Phase 1 permit-all placeholder with real JWT authentication:
 *  - /api/auth/**, Swagger and the health check stay public
 *  - unauthenticated GET is also allowed on /api/products/** and
 *    /api/categories/** (and their nested image/review sub-paths) so the
 *    catalog is browsable without logging in
 *  - everything else requires a valid Bearer token
 *  - STATELESS session policy + a Bearer-token JWT filter means the server
 *    never holds session state, so CSRF (which protects cookie-based
 *    session auth) is correctly disabled rather than a shortcut
 *
 * Role-based rules (ADMIN vs CUSTOMER on specific endpoints) are added with
 * method-level {@code @PreAuthorize} on individual controller methods
 * (see CategoryController) rather than path matchers here - enabled via
 * {@code @EnableMethodSecurity}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/api/health",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    /** Comma-separated list, e.g. "http://localhost:5173,http://localhost:3000" - see application.yml. */
    @Value("#{'${app.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Backs the {@code .cors(...)} call above so the frontend (served from a
     * different origin in dev) can call this API with credentials. Origins
     * are externalized via {@code app.cors.allowed-origins} rather than
     * hardcoded so prod can point at its real frontend domain without a
     * code change.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Exposed so AuthServiceImpl can call authenticate(...) during login
     * without manually wiring a DaoAuthenticationProvider itself - Spring
     * Security assembles the default ProviderManager (which already knows
     * to use our CustomUserDetailsService + PasswordEncoder beans) and this
     * just surfaces it as an injectable bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt is the industry-standard password hashing algorithm: it's
     * adaptive (you can increase its work factor as hardware gets faster)
     * and includes a per-password salt automatically, so two users with the
     * same password get different hashes. Used by AuthServiceImpl to hash
     * passwords on register and, indirectly, by DaoAuthenticationProvider
     * to verify them on login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
