# Story 0.2: Initialize Spring Boot Backend

Status: ready-for-dev

## Story

As a developer,
I want a fully initialized Spring Boot 3.5.x Maven project with all required dependencies, block package structure, Flyway, OpenAPI/Swagger, and a GlobalExceptionHandler returning RFC 7807 Problem Details,
so that all four backend teams share a consistent API foundation and error format from the first commit.

## Acceptance Criteria

1. Project initialized via Spring Initializr with starter dependencies: `web`, `data-jpa`, `mysql`, `security`, `validation`, `mail`, `lombok`, `devtools`
2. Manual dependencies added to `pom.xml`: `springdoc-openapi-starter-webmvc-ui` v2.8.x, `jjwt-api`/`jjwt-impl`/`jjwt-jackson` (io.jsonwebtoken), `flyway-core`
3. Package structure created: `com.ecotrack.users`, `com.ecotrack.assets`, `com.ecotrack.reservations`, `com.ecotrack.analytics`, `com.ecotrack.common`
4. `GlobalExceptionHandler` (`@ControllerAdvice`) returns `ProblemDetail` (Spring 6 native) for all unhandled exceptions — never `Map<String, Object>`
5. `OpenApiConfig` configures Swagger UI at `/swagger-ui.html` with JWT bearer security scheme
6. All config (DB URL, JWT secret, CORS origins, SMTP host/port) externalized via environment variables in `application.properties` — zero hardcoded values
7. Flyway baseline migration `V1__init.sql` (empty baseline) runs successfully on application startup
8. `mvn test` passes with zero failures

## Tasks / Subtasks

- [ ] Task 1 — Bootstrap via Spring Initializr (AC: #1)
  - [ ] 1.1 Run the `curl` command below (or use https://start.spring.io UI) to generate `ecotrack-office-api.zip`
  - [ ] 1.2 Unzip into `backend/` — verify `pom.xml` contains the 8 starter dependencies

- [ ] Task 2 — Add manual dependencies to `pom.xml` (AC: #2)
  - [ ] 2.1 Add `springdoc-openapi-starter-webmvc-ui` v2.8.x
  - [ ] 2.2 Add `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (io.jsonwebtoken, version 0.12.x)
  - [ ] 2.3 Add `flyway-core` (Spring Boot manages the version via BOM)
  - [ ] 2.4 Run `mvn dependency:resolve` — verify no conflicts

- [ ] Task 3 — Create package structure (AC: #3)
  - [ ] 3.1 Create `com/ecotrack/users/` with empty `package-info.java`
  - [ ] 3.2 Create `com/ecotrack/assets/` with empty `package-info.java`
  - [ ] 3.3 Create `com/ecotrack/reservations/` with empty `package-info.java`
  - [ ] 3.4 Create `com/ecotrack/analytics/` with empty `package-info.java`
  - [ ] 3.5 Create `com/ecotrack/common/exception/` and `com/ecotrack/common/security/` and `com/ecotrack/common/sse/`

- [ ] Task 4 — Implement `GlobalExceptionHandler` (AC: #4)
  - [ ] 4.1 Create `GlobalExceptionHandler.java` in `com.ecotrack.common.exception`
  - [ ] 4.2 Annotate with `@RestControllerAdvice`
  - [ ] 4.3 Add handler for generic `Exception` → returns `ProblemDetail` with status 500
  - [ ] 4.4 Add handler for Spring's `MethodArgumentNotValidException` → status 400 with field errors in `detail`
  - [ ] 4.5 Add handler for `NoResourceFoundException` → status 404
  - [ ] 4.6 Write a `@WebMvcTest` unit test verifying a 500 returns `ProblemDetail` JSON (not a Map)

- [ ] Task 5 — Implement `OpenApiConfig` (AC: #5)
  - [ ] 5.1 Create `OpenApiConfig.java` in `com.ecotrack.config`
  - [ ] 5.2 Annotate with `@Configuration`
  - [ ] 5.3 Define `@Bean OpenAPI` with `Info` (title, version) and `SecurityScheme` (type: HTTP, scheme: bearer, bearerFormat: JWT)
  - [ ] 5.4 Start the app locally and verify Swagger UI loads at `http://localhost:8080/swagger-ui.html`

- [ ] Task 6 — Externalize all config via environment variables (AC: #6)
  - [ ] 6.1 Write `application.properties` with zero hardcoded values (see Dev Notes for the template)
  - [ ] 6.2 Write `application-dev.properties` with reasonable local defaults pointing to Docker Compose containers
  - [ ] 6.3 Write `application-prod.properties` (empty — values come from deployment env vars)

- [ ] Task 7 — Flyway baseline migration (AC: #7)
  - [ ] 7.1 Create `src/main/resources/db/migration/V1__init.sql` — empty file with a comment (Flyway requires a non-null file)
  - [ ] 7.2 Start the app with a running MySQL (from Story 0.3 Docker Compose, or manually) — verify Flyway runs without error
  - [ ] 7.3 Verify `flyway_schema_history` table is created in the `ecotrack` database

- [ ] Task 8 — Verify build and tests (AC: #8)
  - [ ] 8.1 Run `mvn test` — the default `EcotrackApplicationTests` context load test must pass
  - [ ] 8.2 Verify the app starts with `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

## Dev Notes

### Spring Initializr command

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.5.0 \
  -d groupId=com.ecotrack \
  -d artifactId=ecotrack-office-api \
  -d name=ecotrack-office-api \
  -d packageName=com.ecotrack.ecotrack \
  -d javaVersion=21 \
  -d dependencies=web,data-jpa,mysql,security,validation,mail,lombok,devtools \
  -o ecotrack-office-api.zip

unzip ecotrack-office-api.zip -d backend/
```

> If the `curl` command fails due to network restrictions, use https://start.spring.io in the browser with the same parameters.

### Manual `pom.xml` dependencies to add

```xml
<!-- OpenAPI / Swagger UI -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.8.4</version>
</dependency>

<!-- JWT -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.12.6</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.12.6</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.12.6</version>
  <scope>runtime</scope>
</dependency>

<!-- Flyway (version managed by Spring Boot BOM) -->
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-mysql</artifactId>
</dependency>
```

> **`flyway-mysql`** is required separately from `flyway-core` for MySQL 8 compatibility (Flyway 9+).

### `GlobalExceptionHandler` implementation

```java
package com.ecotrack.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ❌ NEVER return Map<String, Object> or ResponseEntity<Map<...>>
    // ✅ ALWAYS return ProblemDetail

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setTitle("Validation Failed");
        return pd;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNotFound(NoResourceFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred");
        // Never expose ex.getMessage() in production — it may leak internal details
    }
}
```

### `OpenApiConfig` implementation

```java
package com.ecotrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecotrackOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("EcoTrack Office API")
                .version("v1")
                .description("Smart workspace management API"))
            .schemaRequirement("bearerAuth",
                new SecurityScheme()
                    .type(Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .name("bearerAuth"));
    }
}
```

### `application.properties` template (zero hardcoded values)

```properties
# ── Datasource ──────────────────────────────────────────────────────────────
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/ecotrack?useSSL=false&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:ecotrack}
spring.datasource.password=${DB_PASSWORD:ecotrack}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ── JPA ─────────────────────────────────────────────────────────────────────
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# ── Flyway ──────────────────────────────────────────────────────────────────
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# ── Security / JWT ───────────────────────────────────────────────────────────
jwt.secret=${JWT_SECRET}
jwt.expiration-hours=${JWT_EXPIRATION_HOURS:8}

# ── CORS ─────────────────────────────────────────────────────────────────────
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:4200}

# ── Mail ─────────────────────────────────────────────────────────────────────
spring.mail.host=${SMTP_HOST:localhost}
spring.mail.port=${SMTP_PORT:1025}
spring.mail.username=${SMTP_USERNAME:}
spring.mail.password=${SMTP_PASSWORD:}

# ── SpringDoc / Swagger ──────────────────────────────────────────────────────
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs

# ── Logging ──────────────────────────────────────────────────────────────────
logging.level.com.ecotrack=${LOG_LEVEL:INFO}
```

> `${VAR:default}` syntax — Spring reads the env var; falls back to the default only if the var is unset.  
> `JWT_SECRET` has no default intentionally — the app must fail fast if it's missing in production.

### Flyway V1__init.sql

```sql
-- Flyway baseline migration
-- Tables will be created by subsequent migrations (V2, V3, V4...)
-- This file intentionally left empty.
```

> Migration version numbers must be coordinated across all 4 students:
> - `V1__init.sql` — this story (baseline)
> - `V2__create_users_tables.sql` — Story 1.1
> - `V3__create_assets_tables.sql` — Story 2.1
> - `V4__create_reservations_tables.sql` — Story 3.1
> - `V5__create_analytics_tables.sql` — Story 4.1

### Project Structure Notes

This story creates **only** the `backend/` subtree:

```
backend/
├── pom.xml
├── .env.example                        ← created in Story 0.3
│
└── src/
    ├── main/
    │   ├── java/com/ecotrack/
    │   │   ├── EcotrackApplication.java       ← generated by Initializr
    │   │   ├── config/
    │   │   │   └── OpenApiConfig.java
    │   │   └── common/
    │   │       └── exception/
    │   │           └── GlobalExceptionHandler.java
    │   │
    │   └── resources/
    │       ├── application.properties
    │       ├── application-dev.properties
    │       ├── application-prod.properties
    │       └── db/migration/
    │           └── V1__init.sql
    │
    └── test/
        └── java/com/ecotrack/
            └── EcotrackApplicationTests.java   ← generated by Initializr
```

> Package stubs (`com.ecotrack.users`, `com.ecotrack.assets`, etc.) are empty directories — they just need a `package-info.java` or will be created by Story 1.x teams when they add their first class.

### Architecture Constraints (MUST follow)

- **`ProblemDetail` only** — never `ResponseEntity<Map<String,Object>>` or `ResponseEntity<String>` for errors
- **DTOs only from controllers** — never return JPA entities directly (even in stub endpoints)
- **Table prefixes** — all MySQL tables must be prefixed: `usr_`, `ast_`, `rsv_`, `anl_`
- **Flyway migration numbers** — coordinate globally; never two migrations with the same version number
- **No hardcoded secrets** — `JWT_SECRET` must come from env var; app must fail to start if missing
- **`@PreAuthorize` at service layer** — not just at controller level
- **`spring.jpa.hibernate.ddl-auto=validate`** — Hibernate must NOT create or modify tables; Flyway owns schema

### Anti-patterns banned

```java
// ❌ NEVER
return ResponseEntity.ok(Map.of("error", "User not found"));

// ✅ ALWAYS
ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "User 42 not found");
pd.setType(URI.create("https://ecotrack.com/errors/user-not-found"));
return ResponseEntity.status(404).body(pd);
```

```java
// ❌ NEVER — exposes entity internals, breaks encapsulation
@GetMapping("/users/{id}")
public UserEntity getUser(@PathVariable Long id) { ... }

// ✅ ALWAYS — return DTO
@GetMapping("/users/{id}")
public UserResponse getUser(@PathVariable Long id) { ... }
```

### References

- Initialization command: [Source: architecture.md#Backend-Starter-Spring-Initializr]
- Dependencies list: [Source: architecture.md#Dependencies-Included]
- RFC 7807 pattern: [Source: architecture.md#Format-Patterns]
- Anti-patterns: [Source: architecture.md#Enforcement-Guidelines]
- Package structure: [Source: architecture.md#Complete-Project-Directory-Structure]
- Naming conventions: [Source: architecture.md#Naming-Patterns]
- Story acceptance criteria: [Source: epics.md#Story-0.2]

## Dev Agent Record

### Agent Model Used

Claude Sonnet 4.6 (bmad-create-story workflow)

### Debug Log References

_None_

### Completion Notes List

_To be filled by the developer after implementation._

### File List

_To be filled by the developer: list every file created or modified._
