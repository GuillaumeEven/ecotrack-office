# Story 1.1: User Registration with GDPR Consent

Status: ready-for-dev

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a visitor,
I want to register an account with my name, email, and password, and provide explicit GDPR consent,
So that I can access the platform and my personal data is processed lawfully.

## Acceptance Criteria

1. **Given** I am on the registration page, **When** I submit a valid name, email, and password with the consent checkbox ticked, **Then** my account is created with the EMPLOYEE role by default and a 201 Created response returns the user profile (no password field).
2. **Given** a successful registration, **Then** my password is stored hashed with bcrypt (cost factor ≥ 12) — never in plain text.
3. **Given** a successful registration, **Then** the `usr_users` table (Flyway `V2__users.sql`) stores: `id`, `name`, `email`, `hashed_password`, `role` (ENUM), `gdpr_consent` (BOOLEAN), `consent_at` (TIMESTAMP), `is_active` (BOOLEAN, default TRUE), `created_at` (TIMESTAMP), `search_preferences` (JSON, nullable).
4. **Given** I submit an email that already exists, **Then** a 409 Conflict Problem Detail (RFC 7807) is returned.
5. **Given** any field fails validation (empty name, invalid email format, password < 8 chars, or consent checkbox unticked), **Then** a 400 Bad Request Problem Detail with field-level errors is returned from the API.
6. **Given** I am on the Angular registration form, **Then** inline validation errors are shown before submission (client-side Reactive Forms validators mirror server-side rules).
7. **Given** I have not ticked the consent checkbox, **Then** the form submit button remains disabled / form submission is blocked.

## Tasks / Subtasks

### Backend

- [ ] **Flyway migration** — Create `V2__users.sql` (AC: #3)
  - [ ] Table `usr_users`: `id` BIGINT PK AUTO_INCREMENT, `name` VARCHAR(100) NOT NULL, `email` VARCHAR(255) NOT NULL UNIQUE, `hashed_password` VARCHAR(255) NOT NULL, `role` ENUM('EMPLOYEE','MANAGER','TECHNICIAN') DEFAULT 'EMPLOYEE', `gdpr_consent` BOOLEAN NOT NULL, `consent_at` TIMESTAMP, `is_active` BOOLEAN DEFAULT TRUE, `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `search_preferences` JSON
  - [ ] Index: `idx_usr_users_email` on `email`

- [ ] **`UserEntity.java`** — JPA entity mapping `usr_users` (AC: #3)
  - [ ] `@Table(name = "usr_users")`, `@Column` for all fields, `@Enumerated(EnumType.STRING)` for role

- [ ] **`RegisterRequest.java` DTO** — Jakarta Bean Validation (AC: #5)
  - [ ] `@NotBlank name`, `@NotBlank @Email email`, `@NotBlank @Size(min=8) password`, `@AssertTrue gdprConsent`

- [ ] **`UserResponse.java` DTO** — No password field (AC: #1)
  - [ ] Fields: `id`, `name`, `email`, `role`, `gdprConsent`, `consentAt`, `isActive`, `createdAt`

- [ ] **`UserRepository.java`** — Spring Data JPA (AC: #4)
  - [ ] `boolean existsByEmail(String email)` for duplicate check
  - [ ] `Optional<UserEntity> findByEmail(String email)` (reused by Story 1.2)

- [ ] **`AuthService.java`** — Registration logic (AC: #1, #2, #4)
  - [ ] Check email uniqueness → throw `EmailAlreadyExistsException` (→ 409) if duplicate
  - [ ] Encode password via `PasswordEncoder` (bcrypt, cost 12)
  - [ ] Set role = EMPLOYEE, isActive = true, consentAt = now()
  - [ ] Save entity, return `UserResponse`

- [ ] **`AuthController.java`** — REST endpoint (AC: #1, #4, #5)
  - [ ] `POST /api/v1/auth/register` — publicly accessible (no `@PreAuthorize`)
  - [ ] `@Valid @RequestBody RegisterRequest` triggers bean validation → 400 on failure
  - [ ] Returns `ResponseEntity<UserResponse>` with HTTP 201

- [ ] **`GlobalExceptionHandler.java`** — Error mapping (AC: #4, #5)
  - [ ] `EmailAlreadyExistsException` → `ProblemDetail` HTTP 409
  - [ ] `MethodArgumentNotValidException` → `ProblemDetail` HTTP 400 with per-field error detail
  - [ ] All `ProblemDetail` — never `Map<String, Object>`

- [ ] **`SecurityConfig.java`** — Permit `/api/v1/auth/**` without authentication (AC: #1)
  - [ ] Ensure `POST /api/v1/auth/register` is in the `permitAll()` list

- [ ] **Unit tests** — `AuthServiceTest.java`
  - [ ] Happy path: valid registration returns UserResponse, password encoded
  - [ ] Duplicate email throws exception → mapped to 409
  - [ ] Invalid DTO (e.g., blank name) returns 400 with field errors

### Frontend

- [ ] **`register.component.ts`** — Angular Reactive Form (AC: #6, #7)
  - [ ] Located at `src/app/users/register/register.component.ts`
  - [ ] `Validators.required`, `Validators.email` on email; `Validators.minLength(8)` on password; `Validators.requiredTrue` on consent
  - [ ] Submit button `[disabled]` when `form.invalid`
  - [ ] Inline error messages using `*ngIf="control.invalid && control.touched"`

- [ ] **`register.component.html`** — Angular Material UI (AC: #6, #7)
  - [ ] `<mat-form-field>` for name, email, password inputs
  - [ ] `<mat-checkbox>` for GDPR consent (required)
  - [ ] Privacy text next to checkbox (UX-DR requirement)
  - [ ] Loading spinner via `isLoading$` during API call

- [ ] **`auth.service.ts`** — HTTP call (AC: #1)
  - [ ] `register(payload: RegisterRequest): Observable<UserResponse>` → `POST /api/v1/auth/register`
  - [ ] Uses `HttpClient`; `ErrorInterceptor` handles 400/409 Problem Details globally
  - [ ] On 400, field-level errors extracted and patched to form controls via `setErrors()`

- [ ] **Route** — Add `/register` to `users.routes.ts` (lazy-loaded)
  - [ ] No `AuthGuard` on this route (publicly accessible)
  - [ ] Redirect to `/login` after successful registration

- [ ] **`register.component.spec.ts`** — Unit tests
  - [ ] Form disabled when consent unchecked
  - [ ] Shows email error on invalid email format
  - [ ] Shows password error if < 8 chars
  - [ ] Calls `authService.register()` on valid submit

## Dev Notes

### Architecture Compliance

- **Package**: `com.ecotrack.users` — all backend files live here (controller / service / repository / model / dto sub-packages)
- **Table prefix**: `usr_` — table must be named `usr_users` exactly
- **API prefix**: `/api/v1/auth/register` — always prefixed, never bare `/register`
- **Error format**: ALWAYS use `ProblemDetail` (Spring 6 native) — NEVER `ResponseEntity<Map<String,Object>>`
- **DTO rule**: Controller returns `UserResponse` DTO — NEVER expose `UserEntity` directly to the API
- **Security**: Endpoint must be in `permitAll()` in `SecurityConfig` — otherwise Spring Security will block unauthenticated registrations with 403
- **Password**: Use the `PasswordEncoder` bean (BCryptPasswordEncoder, cost 12) from `SecurityConfig` — do NOT instantiate it locally

### Key Technical Constraints from Architecture

**Backend — Spring Boot 3.5.x, Java 21**

```java
// SecurityConfig — permit public endpoints
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
    // ... other rules
);

// BCrypt password encoder — declared as @Bean in SecurityConfig
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // cost factor 12 per NFR7
}

// RegisterRequest DTO — Bean Validation
public record RegisterRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @AssertTrue(message = "GDPR consent is required") boolean gdprConsent
) {}

// UserResponse DTO — NO password field
public record UserResponse(
    Long id,
    String name,
    String email,
    String role,
    boolean gdprConsent,
    Instant consentAt,
    boolean isActive,
    Instant createdAt
) {}

// GlobalExceptionHandler — RFC 7807
@ExceptionHandler(MethodArgumentNotValidException.class)
public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Validation Failed");
    pd.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
    return pd;
}
```

**Flyway migration** — exact filename: `V2__users.sql` (V1 is the empty baseline created in Story 0.2)

```sql
-- V2__users.sql
CREATE TABLE usr_users (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(100)  NOT NULL,
    email              VARCHAR(255)  NOT NULL,
    hashed_password    VARCHAR(255)  NOT NULL,
    role               ENUM('EMPLOYEE', 'MANAGER', 'TECHNICIAN') NOT NULL DEFAULT 'EMPLOYEE',
    gdpr_consent       BOOLEAN       NOT NULL DEFAULT FALSE,
    consent_at         TIMESTAMP,
    is_active          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    search_preferences JSON,
    UNIQUE KEY uk_usr_users_email (email)
);

CREATE INDEX idx_usr_users_email ON usr_users(email);
```

**Frontend — Angular 21, strict mode, Angular Material**

```typescript
// register.component.ts — Reactive Form
@Component({ ... })
export class RegisterComponent {
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable(); // NEVER expose BehaviorSubject directly

  form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    gdprConsent: [false, Validators.requiredTrue]
  });

  submit() {
    if (this.form.invalid) return;
    this.isLoadingSubject.next(true);
    this.authService.register(this.form.value).pipe(
      finalize(() => this.isLoadingSubject.next(false))
    ).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => { /* 400 field errors patched by ErrorInterceptor */ }
    });
  }
}
```

### Project Structure Notes

**Files to create:**

| File | Path |
|------|------|
| `V2__users.sql` | `backend/src/main/resources/db/migration/V2__users.sql` |
| `UserEntity.java` | `backend/src/main/java/com/ecotrack/users/model/UserEntity.java` |
| `RegisterRequest.java` | `backend/src/main/java/com/ecotrack/users/dto/RegisterRequest.java` |
| `UserResponse.java` | `backend/src/main/java/com/ecotrack/users/dto/UserResponse.java` |
| `UserRepository.java` | `backend/src/main/java/com/ecotrack/users/repository/UserRepository.java` |
| `AuthService.java` | `backend/src/main/java/com/ecotrack/users/service/AuthService.java` |
| `AuthController.java` | `backend/src/main/java/com/ecotrack/users/controller/AuthController.java` |
| `AuthServiceTest.java` | `backend/src/test/java/com/ecotrack/users/AuthServiceTest.java` |
| `register.component.ts` | `frontend/src/app/users/register/register.component.ts` |
| `register.component.html` | `frontend/src/app/users/register/register.component.html` |
| `register.component.spec.ts` | `frontend/src/app/users/register/register.component.spec.ts` |

**Files to modify:**

| File | Change |
|------|--------|
| `SecurityConfig.java` | Add `/api/v1/auth/register` to `permitAll()` matcher |
| `GlobalExceptionHandler.java` | Add handlers for `EmailAlreadyExistsException` and `MethodArgumentNotValidException` |
| `AuthService.java` | Only Story 1.1 creates this file — Stories 1.2+ extend it |
| `users.routes.ts` | Add `{ path: 'register', component: RegisterComponent }` |
| `auth.service.ts` | Add `register()` method (file already scaffolded in Story 0.1 as stub) |

**Do NOT modify:**

- `app.routes.ts` — users module already lazy-loaded with stub routes from Story 0.1
- Any `ast_`, `rsv_`, or `anl_` table or service (different blocks)
- `JwtService.java` — not needed yet (Story 1.2 owns JWT)

### Cross-Story Dependencies

- **Depends on Story 0.1**: Angular workspace initialized with `core/` module, `AuthService` stub, `AuthInterceptor`, `ErrorInterceptor`, and `shared/material.module.ts` already present.
- **Depends on Story 0.2**: Spring Boot project initialized with `GlobalExceptionHandler`, `SecurityConfig`, `OpenApiConfig`, and Flyway baseline `V1__init.sql` already applied.
- **Depends on Story 0.3**: Docker Compose running (MySQL 8 on port 3306, `ecotrack` DB created). Flyway `V2__users.sql` will apply on next Spring Boot startup.
- **Story 1.2 will extend**: `AuthService.java`, `UserRepository.java` (adds login, JWT, refresh token logic). Design `UserEntity` cleanly — Story 1.2 will add no new columns to this table, it creates the separate `usr_refresh_tokens` table.
- **Stories 1.3 and 1.4 extend**: `UserController.java` (not `AuthController.java`). Keep auth and user management controllers separate.

### Security Requirements (NFR6–NFR13)

- Password: bcrypt, cost ≥ 12 (NFR7). Never logged, never returned in any response.
- GDPR: Explicit consent checkbox required (FR40). `gdpr_consent = true` and `consent_at = NOW()` must be set atomically with user creation.
- Input sanitization: All user-submitted text sanitized server-side before persistence (NFR13). Use `@NotBlank`, `@Email`, `@Size` constraints; name field should also strip leading/trailing whitespace via `String.trim()`.
- Email uniqueness check must use a database-level UNIQUE constraint (not just application-level check) to handle concurrent registration race conditions.

### Testing Standards

**Backend:**
- `AuthServiceTest.java` — JUnit 5 + Mockito. Mock `UserRepository` and `PasswordEncoder`. Test:
  - `register()` happy path → asserts password encoded, role = EMPLOYEE, gdprConsent = true
  - Duplicate email → throws `EmailAlreadyExistsException`
- `AuthControllerTest.java` (optional, @WebMvcTest) — Test HTTP 201 on valid body, 400 on missing fields, 409 on duplicate email.

**Frontend:**
- `register.component.spec.ts` — Jasmine + Angular Testing Utilities:
  - Form defaults to invalid (consent = false)
  - Email validator shows error on "not-an-email"
  - Password validator shows error on "short"
  - `authService.register` called with correct payload on valid submit

### References

- [Source: epics.md#Story-1.1] — Full acceptance criteria, Flyway table spec
- [Source: architecture.md#Authentication-&-Security] — JWT decisions, bcrypt cost, HttpOnly cookies
- [Source: architecture.md#Naming-Patterns] — `usr_` prefix, `com.ecotrack.users` package, URL pattern `/api/v1/auth/register`
- [Source: architecture.md#Project-Structure] — `UserEntity.java`, `AuthController.java`, `AuthService.java`, `UserRepository.java` exact file paths
- [Source: architecture.md#API-error-responses] — RFC 7807 ProblemDetail format
- [Source: architecture.md#HTTP-status-codes] — 201 for creation, 409 for conflict, 400 for validation
- [Source: architecture.md#Enforcement-Guidelines] — Never expose JPA entity, never use Map for errors
- [Source: prd.md#NFR7] — bcrypt minimum cost factor 12
- [Source: prd.md#NFR10] — `/api/v1/auth/register` is one of the public endpoints (no auth required)
- [Source: prd.md#NFR13] — Server-side text sanitization mandatory

## Dev Agent Record

### Agent Model Used

claude-sonnet-4-5 / GitHub Copilot

### Debug Log References

### Completion Notes List

### File List
