# Dire Gebeya Backend

E-commerce REST API (clothes, shoes, watches, perfumes) built with Java 21 + Spring Boot 3.5.

## Phase 1 — Project Setup ✅

What's included so far:
- Maven project, Java 21, Spring Boot 3.5.16
- Layered package structure (controller/service/repository/entity/dto/mapper/exception/security/config)
- PostgreSQL connection via environment-driven config (`dev` / `prod` profiles)
- Global exception handling (`GlobalExceptionHandler`) with a consistent JSON error shape
- Swagger / OpenAPI UI
- Temporary permissive Spring Security config (replaced with JWT in Phase 2)

## Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL 14+ running locally (or via Docker)

## Database setup

```bash
# Create the database (adjust user/password as needed)
createdb diregebeya
# or via psql:
psql -U postgres -c "CREATE DATABASE diregebeya;"
```

## Configuration

Config is externalized via environment variables (see `application.yml`,
`application-dev.yml`). Defaults assume a local Postgres on `localhost:5432`
with user/password `postgres`/`postgres`. Override with:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/diregebeya
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

## Running the app

```bash
mvn spring-boot:run
```

The `dev` profile is active by default. The app starts on `http://localhost:8080`.

## Verify it's working

- Health check: `GET http://localhost:8080/api/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Build

```bash
mvn clean package
```
