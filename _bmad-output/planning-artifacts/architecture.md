---
stepsCompleted: [1, 2, 3, 4, 5, 6, 7, 8]
inputDocuments:
  - "_bmad-output/planning-artifacts/prd.md"
  - "_bmad-output/planning-artifacts/implementation-readiness-report-2026-04-17.md"
workflowType: 'architecture'
lastStep: 8
status: 'complete'
completedAt: '2026-04-17'
project_name: 'ecotrack_office'
user_name: 'Sensei'
date: '2026-04-17'
---

# Architecture Decision Document

_This document builds collaboratively through step-by-step discovery. Sections are appended as we work through each architectural decision together._

## Project Context Analysis

### Requirements Overview

**Functional Requirements:**
43 FRs across 7 domains: User Management & Auth, Physical Asset Management,
Interactive Floor Map, Reservation & Booking, Check-in & Auto-release,
Incident Management, and Analytics & Occupancy Dashboard.
The most architecturally complex domains are the Interactive Floor Map
(SVG-as-Angular-components, real-time availability overlay) and Reservation Logic
(conflict prevention, auto-release scheduler with DB persistence).

**Non-Functional Requirements:**
- Performance: ≤ 3s initial load, ≤ 500ms API at p95; availability polling every 30s
- Security: JWT in HttpOnly/Secure/SameSite=Strict cookies; bcrypt (cost 12);
  RBAC enforced at API layer (Spring Security); TLS 1.2+; server-side MIME validation
- Scalability: 500 concurrent users; multi-building DB schema from day 1; no hardcoded limits
- Reliability: 99% office-hours uptime; graceful degradation with last-known map on API failure;
  auto-release scheduler recovers from DB state on server restart
- GDPR: Explicit consent at registration, right to access/erasure, data minimization,
  12-month retention with configurable purge, audit log (min 90 days)

**Scale & Complexity:**

- Primary domain: Full-stack web (Angular SPA + Spring Boot REST API + MySQL)
- Complexity level: Medium
- Estimated architectural components: 4 independent feature modules (Blocks 1–4)
- Target concurrent users: 500
- Academic constraint: 4 students × 8 weeks, one complete CRUD vertical per student

### Technical Constraints & Dependencies

- Stack locked in PRD: Angular (latest), Spring Boot 3.x, MySQL
- Auth: JWT stateless, stored in HttpOnly cookies; 8h expiry + refresh token
- Frontend: Angular Material shared library (no NgRx at MVP), RxJS observables
- Map: SVG floor plan admin-uploaded; Angular components overlaid on SVG anchors;
  no third-party mapping library
- API contract: OpenAPI/Swagger spec defined in Week 1, binds frontend–backend
- Email: SMTP, provider via environment variable
- No external integrations at MVP; REST API designed for future BAS/calendar extension
- All config via environment variables; no hardcoded values

### Cross-Cutting Concerns Identified

1. **Authentication & RBAC** — JWT token flow + Spring Security role enforcement across all 4 blocks
  Note: For SaaS Demo Mode the tenant-level admin role is `Organization Admin` (combines facilities management and tenant administration responsibilities: maps/assets, user accounts, tenant settings and demo plan).
2. **GDPR Compliance** — consent, erasure, export, and pseudonymization touch all data-producing modules
3. **Audit Trail** — all reservation and incident state-change events logged (Blocks 1, 3, 4)
4. **Async Event Processing** — auto-release scheduler, incident push notifications, photo upload (Blocks 3, 4)
5. **Real-Time Data** — availability polling every 30s, immediate resource status change on check-in/incident (Blocks 2, 3, 4)
6. **Error Handling & Graceful Degradation** — API offline → SPA displays last-known state; booking disabled
7. **Parallel Development Contract** — OpenAPI mocks enable Blocks 2–4 to develop independently from Week 2

## Starter Template Evaluation

### Primary Technology Domain

Full-stack web application: Angular 21 SPA (frontend) + Spring Boot 3.5.x REST API (backend) + MySQL 8.x (database).
Stack is locked per PRD. This step documents verified initialization commands and architectural decisions established at project scaffold.

### Project Structure (Monorepo)

```
ecotrack-office/
├── frontend/          # Angular 21 workspace
└── backend/           # Spring Boot 3.5.x Maven project
```

### Frontend Starter: Angular CLI 21

**Initialization Commands:**

```bash
# Install Angular CLI globally
npm install -g @angular/cli@21

# Create the Angular workspace
ng new ecotrack-office-frontend \
  --routing \
  --style scss \
  --strict

cd ecotrack-office-frontend

# Add Angular Material (shared UI library)
ng add @angular/material
```

**Architectural Decisions Provided by Starter:**

**Language & Runtime:**
- TypeScript (strict mode enabled)
- Node.js ≥ 20.19.0 or ≥ 22.12.0

**Build Tooling:**
- Angular CLI with Vite/esbuild (default from Angular 17+) — fast dev builds
- Lazy loading per feature module (configured via Angular Router)

**Styling Solution:**
- SCSS per component
- Angular Material theming system (shared design tokens)

**Testing Framework:**
- Jasmine + Karma (unit, default Angular setup)
- Angular component testing harness via `@angular/cdk/testing`

**Code Organization:**
- Feature module per Block (Block1–Block4), each lazy-loaded
- Shared module for Angular Material components, interceptors, guards
- `environment.ts` / `environment.prod.ts` for env config

**Development Experience:**
- `ng serve` with HMR
- Angular DevTools browser extension support
- ESLint via `ng lint`

---

### Backend Starter: Spring Initializr (Spring Boot 3.5.x)

**Initialization Command:**

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

Or via Spring Initializr UI: https://start.spring.io

**Architectural Decisions Provided by Starter:**

**Language & Runtime:**
- Java 21 LTS
- Maven build system

**Dependencies Included:**
- `spring-boot-starter-web` — REST controllers, embedded Tomcat
- `spring-boot-starter-data-jpa` — Hibernate ORM, repository pattern
- `mysql-connector-j` — MySQL 8.x JDBC driver
- `spring-boot-starter-security` — Spring Security (RBAC, JWT integration)
- `spring-boot-starter-validation` — Bean Validation (Jakarta)
- `spring-boot-starter-mail` — JavaMailSender for SMTP
- `lombok` — boilerplate reduction (@Data, @Builder, @Slf4j)
- `spring-boot-devtools` — hot reload in development

**Additional Dependencies (manual, post-init):**
- `springdoc-openapi-starter-webmvc-ui` v2.8.x — OpenAPI/Swagger UI
- `jjwt-api` / `jjwt-impl` / `jjwt-jackson` (io.jsonwebtoken) — JWT generation & validation

**Code Organization:**
- Package per Block: `com.ecotrack.users`, `com.ecotrack.assets`, `com.ecotrack.reservations`, `com.ecotrack.analytics`
- Layered architecture per block: `controller` / `service` / `repository` / `model` / `dto`
- `application.properties` externalized via environment variables

**Testing Framework:**
- JUnit 5 + Spring Boot Test (included by default)
- Mockito for service layer unit tests
- `@DataJpaTest` slice tests for repositories

**Note:** Project initialization (both frontend and backend) should be the first implementation stories (Story 0 / Block 0).

## Core Architectural Decisions

### Decision Priority Analysis

**Critical Decisions (block implementation):**
- Flyway for DB migrations (parallel 4-student schema management)
- Refresh tokens persisted in DB (survives restarts, revocable)
- SSE for real-time incident notifications (no WebSocket overhead at MVP)
- RFC 7807 Problem Details for API error format (uniform Angular error handling)

**Important Decisions (shape architecture):**
- URI versioning `/api/v1/` (explicit contract for mocking)
- Reactive Forms everywhere (consistent with server-side validation)
- Docker Compose for local dev (reproducible environment across team)
- Filesystem photo storage via env var (no object storage in academic scope)

**Deferred Decisions (Post-MVP):**
- Redis caching (not needed at 500 users / simple queries)
- WebSocket upgrade (polling 30s sufficient at MVP; Growth tier candidate)
- Cloud deployment (self-hosted localhost for academic demo)

---

### Data Architecture

| Decision | Choice | Rationale |
|---|---|---|
| DB Migration | **Flyway** | SQL-native, zero learning curve, Spring Boot auto-run on startup |
| ORM | **Spring Data JPA / Hibernate** | Locked by starter; `@Entity` + `JpaRepository` per block |
| Caching | **None (MVP)** | 500 concurrent users on MySQL 8 requires no caching layer |
| DB per block | **Shared schema, prefixed tables** | Single MySQL DB; table prefix per block (`usr_`, `ast_`, `rsv_`, `anl_`) |

---

### Authentication & Security

| Decision | Choice | Rationale |
|---|---|---|
| JWT Library | **jjwt (io.jsonwebtoken)** | Lightweight, well-maintained, no extra Spring OAuth2 complexity |
| Token storage (frontend) | **HttpOnly + Secure + SameSite=Strict cookies** | Locked by PRD; prevents XSS token theft |
| Refresh token storage | **DB table `usr_refresh_tokens`** | Revocable, auditable, survives server restarts |
| CORS | **`WebMvcConfigurer` + env var `CORS_ALLOWED_ORIGINS`** | Single source of truth, no annotation scatter |
| Role enforcement | **Spring Security `@PreAuthorize` at service layer** | Consistent regardless of controller changes |
| Email check-in token | **One-time token in DB (`rsv_checkin_tokens`)**, 15-min expiry | Stateless link, no session required (FR25) |

---

### API & Communication Patterns

| Decision | Choice | Rationale |
|---|---|---|
| API versioning | **URI prefix `/api/v1/`** | Explicit, mockable, visible in OpenAPI spec |
| Error format | **RFC 7807 Problem Details** (`ProblemDetail`, Spring 6 native) | Standard HTTP; Angular `HttpClient` parses uniformly |
| Pagination | **Offset-based** (`?page=0&size=20`, Spring `Pageable`) | Simple, sufficient for MVP data volumes |
| Real-time notifications | **SSE via Spring `SseEmitter`** | Native HTTP, no dependency, unidirectional (server → client) |
| API documentation | **SpringDoc OpenAPI v2.8.x** (Swagger UI at `/swagger-ui.html`) | Week 1 contract for parallel development |
| JSON field naming | **camelCase** (Spring Boot Jackson default) | Consistent with Angular/TypeScript conventions |
| Date/time format | **ISO 8601 strings** (`2026-04-17T09:00:00Z`) | Unambiguous, timezone-explicit |

---

### Frontend Architecture

| Decision | Choice | Rationale |
|---|---|---|
| State management | **Services + RxJS BehaviorSubject** (no NgRx) | Locked by PRD; sufficient at MVP scale |
| Forms | **Reactive Forms** | Consistent programmatic validation; mirrors server-side validation |
| HTTP interceptors | **Two interceptors**: `AuthInterceptor` (token injection) + `ErrorInterceptor` (global RFC 7807 parsing) | Separation of concerns |
| QR Code | **`qrcode` npm lib** (frontend generation) | Encodes check-in URL with one-time token; no backend cost |
| Availability polling | **`interval(30000)` + `switchMap` RxJS** in floor map service | Simple, cancels on unsubscribe |
| SSE subscription | **`EventSource` API** in Angular service, reconnect on error | Native browser API, no lib needed |

---

### Infrastructure & Deployment

| Decision | Choice | Rationale |
|---|---|---|
| Local dev DB | **Docker Compose** (MySQL 8 + MailHog) | `docker-compose up` → ready in 30s for all students |
| File storage (photos) | **Local filesystem** (`UPLOAD_DIR` env var) | No object storage complexity; academic scope |
| Deployment | **Self-hosted / localhost** | Academic demo; 12-factor architecture ready for cloud extension |
| Logging | **SLF4J + Logback** (Spring Boot default) | `INFO` in prod profile, `DEBUG` in dev profile |
| Scheduler (auto-release) | **Spring `@Scheduled` + DB state** | Recovers pending jobs on restart; no in-memory job loss |

---

### Decision Impact Analysis

**Implementation Sequence:**
1. Block 1 (auth): JWT + Spring Security + `usr_*` tables + Flyway baseline migration
2. Block 2 (assets): `ast_*` tables + SVG upload + floor map REST endpoints
3. Block 3 (reservations): `rsv_*` tables + scheduler + SSE + email check-in token
4. Block 4 (analytics): `anl_*` tables + SSE consumer + dashboard aggregation queries

**Cross-Component Dependencies:**
- All blocks depend on Block 1's `AuthInterceptor` and `JwtFilter`
- SSE emitter (Block 3) consumed by Angular service shared across Blocks 3 and 4
- Flyway migration order must be coordinated (Block 1 runs first: `V1__users.sql`, `V2__assets.sql`, etc.)
- `UPLOAD_DIR` env var shared between Block 4 (incident photo) and Docker Compose volume

## Implementation Patterns & Consistency Rules

### Naming Patterns

**Database — snake_case, prefixed tables**

```
Tables  : usr_users, ast_desks, ast_zones, ast_floors, rsv_reservations, rsv_checkin_tokens, anl_incidents
Columns : user_id, created_at, is_active, zone_id, auto_release_minutes
FKs     : user_id, zone_id (never fk_user, never userId)
Indexes : idx_{table}_{column}  →  idx_usr_users_email, idx_rsv_reservations_desk_id
```

**API REST — plural resources, kebab-case compound words**

```
GET    /api/v1/users
GET    /api/v1/floor-plans/{floorId}/desks
POST   /api/v1/reservations
PATCH  /api/v1/reservations/{id}/check-in
POST   /api/v1/incidents/{id}/resolve
GET    /api/v1/zones/{id}/occupancy-report
```

Rule: **plural** for collections; **action verb** for operations (`/check-in`, `/release`, `/resolve`).

**Java — standard Spring conventions**

```java
Classes   : UserService, ReservationController, DeskRepository, CheckInTokenEntity
Methods   : findByEmail(), createReservation(), releaseExpiredReservations()
Variables : userId, checkInToken, autoReleaseMinutes
Constants : MAX_PHOTO_SIZE_MB, DEFAULT_CHECKIN_TIMEOUT_MINUTES
Packages  : com.ecotrack.users.controller / .service / .repository / .model / .dto
```

**TypeScript/Angular — camelCase vars, PascalCase classes, kebab-case files**

```typescript
Components : FloorMapComponent, ReservationFormComponent, IncidentReportComponent
Services   : ReservationService, AuthService, FloorMapService, SseNotificationService
Files      : floor-map.component.ts, reservation.service.ts, auth.interceptor.ts
Interfaces : Reservation, Desk, UserProfile  (no I-prefix)
Enums      : ReservationStatus.CONFIRMED, DeskStatus.AVAILABLE, IncidentStatus.OPEN
```

---

### Structure Patterns

**Angular — feature module per block (lazy-loaded)**

```
src/app/
├── core/              ← AuthInterceptor, ErrorInterceptor, JwtService, AuthGuard, RoleGuard
├── shared/            ← Angular Material re-exports, shared pipes, shared components
├── users/             ← Block 1 (lazy-loaded feature module)
├── assets-mgmt/       ← Block 2 (lazy-loaded feature module)
├── reservations/      ← Block 3 (lazy-loaded feature module)
└── analytics/         ← Block 4 (lazy-loaded feature module)
```

**Test co-location**

```
Backend  : src/test/java/com/ecotrack/{block}/…Test.java  (mirrors src/main structure)
Frontend : src/app/{module}/…component.spec.ts            (co-located with component)
```

---

### Format Patterns

**API success responses**

```json
// Collection (Spring Page)
{ "content": [...], "page": 0, "size": 20, "totalElements": 47 }

// Single entity — direct object, no wrapper
{ "id": 1, "email": "g@ecotrack.com", "role": "EMPLOYEE" }

// Action without data return
HTTP 204 No Content
```

**API error responses — RFC 7807 Problem Details**

```json
{
  "type": "https://ecotrack.com/errors/desk-unavailable",
  "title": "Desk Unavailable",
  "status": 409,
  "detail": "Desk 42 is already reserved for this time slot.",
  "instance": "/api/v1/reservations"
}
```

**HTTP status codes**

| Situation | Code |
|---|---|
| Resource created | 201 Created |
| Read / update | 200 OK |
| Action, no return data | 204 No Content |
| Validation failure | 400 Bad Request |
| Not authenticated | 401 Unauthorized |
| Wrong role | 403 Forbidden |
| Not found | 404 Not Found |
| Double-booking conflict | 409 Conflict |

**Data formats**
- JSON fields: **camelCase** (Spring Boot Jackson default)
- Dates/times: **ISO 8601** (`2026-04-17T09:00:00Z`)
- Booleans: `true` / `false` (never 1/0)

---

### Communication Patterns

**SSE event format**

```
event: DESK_STATUS_CHANGED
data: {"deskId": 42, "status": "AVAILABLE", "timestamp": "2026-04-17T09:15:00Z"}

event: INCIDENT_CREATED
data: {"incidentId": 7, "resourceId": 42, "location": "Floor 3 - Zone A"}
```

**Angular service pattern — BehaviorSubject**

```typescript
// Always expose public Observable, never the Subject directly
private desksSubject = new BehaviorSubject<Desk[]>([]);
desks$ = this.desksSubject.asObservable();

// isLoading$ mandatory on every async operation
isLoading$ = new BehaviorSubject<boolean>(false);
```

**Spring Scheduler — auto-release pattern**

```java
// Always read state from DB, never from memory
@Scheduled(fixedDelay = 60_000)
public void releaseExpiredReservations() {
    // SELECT from rsv_reservations WHERE status = PENDING AND start_time < now() - timeout
}
```

---

### Process Patterns

**Error handling — Angular**

- `ErrorInterceptor` parses RFC 7807 and dispatches user-facing messages globally
- Individual components do **not** handle HTTP errors directly
- Exception: 400 validation errors are displayed inline in the form

**Loading states — mandatory on every async operation**

```typescript
submit() {
  this.isLoading$.next(true);
  this.service.create(this.form.value).pipe(
    finalize(() => this.isLoading$.next(false))
  ).subscribe({ ... });
}
```

**Validation — double mandatory**

```
✅ Jakarta Bean Validation server-side (source of truth)
✅ Angular Reactive Forms validators client-side (UX prevention)
❌ Never Angular-only validation without server-side check
```

---

### Enforcement Guidelines

**All agents/students MUST:**

- Prefix MySQL tables with block code (`usr_`, `ast_`, `rsv_`, `anl_`)
- Use `ProblemDetail` (Spring 6) for all API errors — never `ResponseEntity<Map<String,Object>>`
- Return **DTOs** from controllers, never JPA entities directly
- Coordinate Flyway migration version numbers globally (`V1__`, `V2__`, `V3__`…)
- Expose Angular Observables via `.asObservable()`, never the raw Subject

**Anti-patterns banned:**

```java
// ❌ NEVER
return ResponseEntity.ok(Map.of("error", "not found"));
// ✅ ALWAYS
return ResponseEntity.status(404)
    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Desk 42 not found"));
```

```typescript
// ❌ NEVER
public desksSubject = new BehaviorSubject<Desk[]>([]);
// ✅ ALWAYS
private desksSubject = new BehaviorSubject<Desk[]>([]);
desks$ = this.desksSubject.asObservable();
```

## Project Structure & Boundaries

### Complete Project Directory Structure

```
ecotrack-office/
├── docker-compose.yml              ← MySQL 8 + MailHog (dev env)
├── .env.example                    ← Template variables d'environnement
├── README.md
│
├── frontend/                       ← Angular 21 workspace
│   ├── angular.json
│   ├── package.json
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   ├── tsconfig.spec.json
│   ├── .eslintrc.json
│   ├── .env.example
│   │
│   └── src/
│       ├── main.ts
│       ├── index.html
│       ├── styles.scss             ← Angular Material theme global
│       │
│       ├── environments/
│       │   ├── environment.ts      ← apiUrl, sseUrl, pollInterval
│       │   └── environment.prod.ts
│       │
│       └── app/
│           ├── app.component.ts
│           ├── app.routes.ts       ← Lazy routes to the 4 feature modules
│           ├── app.config.ts       ← provideHttpClient, provideRouter
│           │
│           ├── core/               ← Singleton services, guards, interceptors
│           │   ├── auth/
│           │   │   ├── auth.service.ts
│           │   │   ├── auth.guard.ts
│           │   │   ├── role.guard.ts
│           │   │   └── jwt.service.ts
│           │   ├── interceptors/
│           │   │   ├── auth.interceptor.ts   ← Injects JWT cookie
│           │   │   └── error.interceptor.ts  ← Parses RFC 7807
│           │   └── sse/
│           │       └── sse-notification.service.ts  ← EventSource wrapper
│           │
│           ├── shared/             ← Components / pipes shared across blocks
│           │   ├── components/
│           │   │   ├── loading-spinner/
│           │   │   ├── confirm-dialog/
│           │   │   └── error-banner/
│           │   ├── pipes/
│           │   │   └── relative-time.pipe.ts
│           │   └── material.module.ts       ← Re-export Angular Material
│           │
│           ├── users/              ← Block 1 — Users & Auth (FR1–FR7)
│           │   ├── users.routes.ts
│           │   ├── login/
│           │   │   ├── login.component.ts
│           │   │   └── login.component.spec.ts
│           │   ├── register/
│           │   ├── profile/
│           │   └── admin/
│           │       └── user-management.component.ts
│           │
│           ├── assets-mgmt/        ← Block 2 — Physical Assets (FR8–FR18)
│           │   ├── assets.routes.ts
│           │   ├── floor-map/
│           │   │   ├── floor-map.component.ts      ← SVG overlay principal
│           │   │   ├── floor-map.component.spec.ts
│           │   │   ├── desk-marker/
│           │   │   │   └── desk-marker.component.ts  ← Component on SVG anchor
│           │   │   └── zone-overlay/
│           │   │       └── zone-overlay.component.ts ← Energy heat overlay
│           │   ├── floor-plan-upload/
│           │   └── asset-admin/
│           │       └── asset-form.component.ts
│           │
│           ├── reservations/       ← Block 3 — Reservations (FR19–FR28)
│           │   ├── reservations.routes.ts
│           │   ├── reservation-form/
│           │   │   ├── reservation-form.component.ts
│           │   │   └── reservation-form.component.spec.ts
│           │   ├── my-reservations/
│           │   ├── check-in/
│           │   │   └── check-in.component.ts     ← Public route /check-in?token=
│           │   └── qr-code/
│           │       └── qr-code.component.ts      ← Generation via qrcode lib
│           │
│           └── analytics/          ← Block 4 — Analytics & Incidents (FR29–FR43)
│               ├── analytics.routes.ts
│               ├── dashboard/
│               │   └── occupancy-dashboard.component.ts
│               ├── incidents/
│               │   ├── incident-list.component.ts
│               │   ├── incident-report-form.component.ts  ← Photo upload
│               │   └── incident-detail.component.ts
│               └── zone-consolidation/
│                   └── consolidation-suggestions.component.ts
│
└── backend/                        ← Spring Boot 3.5.x Maven project
    ├── pom.xml
    ├── .env.example
    │
    └── src/
        ├── main/
        │   ├── java/com/ecotrack/
        │   │   │
        │   │   ├── EcotrackApplication.java
        │   │   ├── config/
        │   │   │   ├── SecurityConfig.java       ← Spring Security + CORS
        │   │   │   ├── OpenApiConfig.java        ← SpringDoc Swagger config
        │   │   │   ├── SchedulerConfig.java      ← @EnableScheduling
        │   │   │   └── WebMvcConfig.java         ← CORS origins via env var
        │   │   │
        │   │   ├── common/
        │   │   │   ├── exception/
        │   │   │   │   └── GlobalExceptionHandler.java  ← @ControllerAdvice RFC 7807
        │   │   │   ├── security/
        │   │   │   │   ├── JwtService.java
        │   │   │   │   ├── JwtAuthFilter.java
        │   │   │   │   └── UserDetailsServiceImpl.java
        │   │   │   └── sse/
        │   │   │       └── SseEmitterRegistry.java  ← Technician SSE connections
        │   │   │
        │   │   ├── users/                        ← Block 1 (FR1–FR7)
        │   │   │   ├── controller/
        │   │   │   │   ├── AuthController.java        ← /api/v1/auth/login, /register
        │   │   │   │   └── UserController.java        ← /api/v1/users
        │   │   │   ├── service/
        │   │   │   │   ├── AuthService.java
        │   │   │   │   └── UserService.java
        │   │   │   ├── repository/
        │   │   │   │   ├── UserRepository.java
        │   │   │   │   └── RefreshTokenRepository.java
        │   │   │   ├── model/
        │   │   │   │   ├── UserEntity.java            ← table usr_users
        │   │   │   │   └── RefreshTokenEntity.java    ← table usr_refresh_tokens
        │   │   │   └── dto/
        │   │   │       ├── LoginRequest.java
        │   │   │       ├── RegisterRequest.java
        │   │   │       └── UserResponse.java
        │   │   │
        │   │   ├── assets/                       ← Block 2 (FR8–FR18)
        │   │   │   ├── controller/
        │   │   │   │   ├── FloorController.java       ← /api/v1/floors
        │   │   │   │   ├── ZoneController.java        ← /api/v1/zones
        │   │   │   │   ├── DeskController.java        ← /api/v1/desks
        │   │   │   │   └── FloorPlanController.java   ← /api/v1/floor-plans (SVG upload)
        │   │   │   ├── service/
        │   │   │   ├── repository/
        │   │   │   ├── model/
        │   │   │   │   ├── FloorEntity.java           ← table ast_floors
        │   │   │   │   ├── ZoneEntity.java            ← table ast_zones
        │   │   │   │   └── DeskEntity.java            ← table ast_desks
        │   │   │   └── dto/
        │   │   │
        │   │   ├── reservations/                 ← Block 3 (FR19–FR28)
        │   │   │   ├── controller/
        │   │   │   │   ├── ReservationController.java ← /api/v1/reservations
        │   │   │   │   └── CheckInController.java     ← /api/v1/reservations/{id}/check-in (public)
        │   │   │   ├── service/
        │   │   │   │   ├── ReservationService.java
        │   │   │   │   ├── CheckInService.java
        │   │   │   │   └── AutoReleaseScheduler.java  ← @Scheduled
        │   │   │   ├── repository/
        │   │   │   ├── model/
        │   │   │   │   ├── ReservationEntity.java     ← table rsv_reservations
        │   │   │   │   └── CheckInTokenEntity.java    ← table rsv_checkin_tokens
        │   │   │   └── dto/
        │   │   │
        │   │   └── analytics/                    ← Block 4 (FR29–FR43)
        │   │       ├── controller/
        │   │       │   ├── IncidentController.java    ← /api/v1/incidents
        │   │       │   ├── DashboardController.java   ← /api/v1/dashboard
        │   │       │   └── ReportController.java      ← /api/v1/reports (export)
        │   │       ├── service/
        │   │       │   ├── IncidentService.java
        │   │       │   ├── OccupancyService.java
        │   │       │   └── NotificationService.java   ← SSE dispatch + email
        │   │       ├── repository/
        │   │       ├── model/
        │   │       │   └── IncidentEntity.java        ← table anl_incidents
        │   │       └── dto/
        │   │
        │   └── resources/
        │       ├── application.properties         ← References env vars only
        │       ├── application-dev.properties
        │       ├── application-prod.properties
        │       └── db/migration/                  ← Flyway scripts
        │           ├── V1__create_users_tables.sql
        │           ├── V2__create_assets_tables.sql
        │           ├── V3__create_reservations_tables.sql
        │           └── V4__create_analytics_tables.sql
        │
        └── test/
            └── java/com/ecotrack/
                ├── users/
                │   ├── AuthServiceTest.java
                │   └── UserControllerTest.java       ← @WebMvcTest
                ├── assets/
                ├── reservations/
                │   └── AutoReleaseSchedulerTest.java
                └── analytics/
```

### Architectural Boundaries

**API access control**

| Boundary | Endpoint prefix | Auth | Roles |
|---|---|---|---|
| Auth | `/api/v1/auth/**` | No | Public |
| Check-in link | `/api/v1/reservations/*/check-in?token=` | No (one-time token) | Public |
| Employee self-service | `/api/v1/reservations/**`, `/api/v1/floor-plans/**` | Yes | EMPLOYEE+ |
| Incident reporting | `POST /api/v1/incidents` | Yes | Any authenticated |
| Technician actions | `/api/v1/incidents/*/resolve` | Yes | TECHNICIAN+ |
| Admin / Manager | `/api/v1/users/**`, `/api/v1/reports/**` | Yes | ORGANIZATION_ADMIN |
| SSE stream | `/api/v1/sse/incidents` | Yes | TECHNICIAN |

**Data flow (synchronous)**

```
Angular Component
  → Reactive Form (client validation)
  → Service (BehaviorSubject + HttpClient)
  → AuthInterceptor (attach cookie)
  → Spring Controller (DTO binding + @Valid)
  → Spring Security (@PreAuthorize)
  → Service layer (business logic)
  → Repository (JPA query)
  → MySQL
  ← DTO response / ProblemDetail (RFC 7807)
  ← ErrorInterceptor (global error parse)
  ← Component (update isLoading$, state$)
```

**Async flows**

```
Auto-release   : @Scheduled → DB query → UPDATE status → SseEmitterRegistry.broadcast()
Incident notif : POST /incidents → IncidentService → SseEmitterRegistry.broadcast() + JavaMailSender
Photo upload   : multipart POST → MIME check → save to UPLOAD_DIR → return path in response
Email check-in : POST /reservations → generate token → save rsv_checkin_tokens → JavaMailSender
```

### Requirements to Structure Mapping

| FR Domain | Backend package | Frontend module | DB prefix |
|---|---|---|---|
| FR1–FR7 User & Auth | `com.ecotrack.users` | `app/users/` | `usr_` |
| FR8–FR12 Physical Assets | `com.ecotrack.assets` | `app/assets-mgmt/` | `ast_` |
| FR13–FR18 Floor Map | `com.ecotrack.assets` | `app/assets-mgmt/floor-map/` | `ast_` |
| FR19–FR23 Reservations | `com.ecotrack.reservations` | `app/reservations/` | `rsv_` |
| FR24–FR28 Check-in | `com.ecotrack.reservations` | `app/reservations/check-in/` | `rsv_` |
| FR29–FR34 Incidents | `com.ecotrack.analytics` | `app/analytics/incidents/` | `anl_` |
| FR35–FR43 Dashboard & GDPR | `com.ecotrack.analytics` | `app/analytics/dashboard/` | `anl_` |

**Cross-cutting → `core/` (frontend) + `common/` (backend)**

| Concern | Backend | Frontend |
|---|---|---|
| JWT auth | `common/security/JwtService` | `core/auth/jwt.service.ts` |
| Error handling | `common/exception/GlobalExceptionHandler` | `core/interceptors/error.interceptor.ts` |
| SSE | `common/sse/SseEmitterRegistry` | `core/sse/sse-notification.service.ts` |
| RBAC | Spring Security `@PreAuthorize` | `core/auth/role.guard.ts` |

## Architecture Validation Results

### Coherence Validation ✅

**Decision Compatibility:**
All technology choices are mutually compatible: Angular 21 HttpClient with `withCredentials` works
seamlessly with Spring Boot 3.5.x JWT HttpOnly cookies; SSE `EventSource` pairs natively with Spring
`SseEmitter`; Flyway auto-runs on Spring Boot startup; SpringDoc OpenAPI 2.8.x is compatible with
Spring 6 / Boot 3.5.x; `ProblemDetail` is native to Spring 6 (no extra library).

**Pattern Consistency:**
RFC 7807 server errors map directly to `ErrorInterceptor` Angular parsing. The 3-layer alignment
(DB prefix `usr_/ast_/rsv_/anl_` ↔ Java package ↔ Angular module) ensures no naming drift across
the stack. Flyway global version ordering (V1–V4) respects block dependency sequence.

**Notable behaviour:** `SseEmitterRegistry` holds in-memory connections — clients auto-reconnect via
native `EventSource` retry on server restart. Acceptable at MVP scope.

### Requirements Coverage Validation ✅

**Functional Requirements (43 FRs):**

| FR Domain | Status |
|---|---|
| FR1–FR7 User & Auth | ✅ Block 1: AuthController, Spring Security, JWT, usr_users, usr_refresh_tokens |
| FR8–FR12 Physical Assets | ✅ Block 2: Floor/Zone/Desk/FloorPlan controllers, UPLOAD_DIR for SVG |
| FR13–FR18 Floor Map | ✅ assets-mgmt/floor-map, desk-marker, zone-overlay, interval(30000) polling |
| FR19–FR23 Reservations | ✅ ReservationService + DB UNIQUE constraint on (desk_id, date, slot) |
| FR24–FR25 Check-in | ✅ CheckInController (public), rsv_checkin_tokens, /check-in?token= route |
| FR26–FR28 Auto-release | ✅ AutoReleaseScheduler @Scheduled + DB state recovery + JavaMailSender |
| FR29–FR34 Incidents | ✅ IncidentController + NotificationService (SSE + mail) + desk auto-block |
| FR35–FR39 Dashboard | ✅ DashboardController, OccupancyService, ReportController |
| FR40–FR43 GDPR | ✅ Consent at registration, data export/delete via UserController, configurable retention |

**Non-Functional Requirements:**

| NFR | Coverage |
|---|---|
| Performance ≤3s / ≤500ms | Lazy loading per Angular module; no N+1 queries (JPA fetch strategy per block) |
| Security | HttpOnly cookies, Spring Security @PreAuthorize, bcrypt cost 12, TLS |
| Scalability 500 users | MySQL 8 + HikariCP connection pool (Spring Boot default) |
| Reliability (scheduler) | @Scheduled reads DB state on every tick; no in-memory job loss |
| GDPR compliance | FR40–FR43 fully mapped to Block 1 and Block 4 |
| Maintainability | OpenAPI spec Week 1; env vars only; independent block packages |

### Gap Analysis Results

**Important gaps (to address during implementation, not blocking):**

1. **Double-booking DB constraint** — Flyway migration must explicitly add `UNIQUE(desk_id, date, time_slot)` on `rsv_reservations`. Critical to implement in V3 migration.

2. **UPLOAD_DIR sub-folder split** — `UPLOAD_DIR/floor-plans/` for Block 2 SVG and `UPLOAD_DIR/incident-photos/` for Block 4 photos. Same env var, two sub-paths.

3. **Audit log table** — FR43 requires event logging. Recommended: table `usr_audit_log` in Block 1 schema, written via Spring `@EventListener` on reservation and incident state changes.

4. **GDPR anonymisation scheduler** — FR42 requires automated purge after retention period. A `@Scheduled` job in Block 1 (`DataRetentionScheduler`) should handle this.

**Minor gaps:**
- `Pageable` parameters should be documented in OpenAPI spec during Week 1
- Dev/prod log levels to be set explicitly in `application-dev.properties` / `application-prod.properties`

### Architecture Completeness Checklist

**✅ Requirements Analysis**
- [x] Project context thoroughly analyzed (43 FRs, 7 NFR categories)
- [x] Scale and complexity assessed (Medium, 4 blocks, 500 concurrent users)
- [x] Technical constraints identified (stack, auth, SVG, SMTP, no external integrations)
- [x] Cross-cutting concerns mapped (7 concerns across all blocks)

**✅ Architectural Decisions**
- [x] 14 critical/important decisions documented with rationale
- [x] All versions verified (Angular 21.2.7, Spring Boot 3.5.x, Java 21 LTS, MySQL 8)
- [x] Block implementation sequence defined (1→2→3→4)
- [x] Cross-block dependencies cartographied

**✅ Implementation Patterns**
- [x] Naming conventions (DB snake_case, API kebab-case plural, Java PascalCase, TS camelCase)
- [x] Structure patterns (lazy feature modules, co-located tests)
- [x] Format patterns (RFC 7807, ISO 8601, camelCase JSON, HTTP status codes)
- [x] Communication patterns (SSE events, BehaviorSubject, @Scheduled)
- [x] Process patterns (error handling, loading states, double validation)
- [x] Anti-patterns explicitly documented with examples

**✅ Project Structure**
- [x] Complete directory tree defined (frontend + backend, every file named)
- [x] Component boundaries established (core / shared / block1–4)
- [x] Integration points mapped (API boundaries table, sync + async data flows)
- [x] All 43 FRs mapped to specific packages, modules, and DB prefixes

### Architecture Readiness Assessment

**Overall Status: READY FOR IMPLEMENTATION** ✅

**Confidence Level: High**

**Key Strengths:**
- Stack fully pre-defined in PRD — zero technology ambiguity for agents
- 4-block structure perfectly matched to academic constraint (1 student = 1 full CRUD vertical)
- Cross-cutting concerns isolated in `core/` (frontend) and `common/` (backend) — blocks are decoupled
- Complete initialization commands ready — first sprint can start immediately
- Consistent 3-layer naming alignment prevents drift across DB / Java / Angular

**Areas for Future Enhancement (post-MVP):**
- WebSocket upgrade for real-time map updates (replaces 30s polling)
- Redis cache for desk availability (if user base scales beyond 500)
- `usr_audit_log` dedicated table (identified gap above)
- GDPR anonymisation `@Scheduled` job in Block 1
- Native mobile app via Angular + Capacitor (Vision tier)

### Implementation Handoff

**AI Agent Guidelines:**
- Follow all architectural decisions exactly as documented in this file
- Use implementation patterns consistently — refer to the anti-patterns section before coding
- Respect project structure and package/module boundaries
- This document is the single source of truth for all technical questions

**First Implementation Priority:**
```bash
# 1. Backend scaffold
curl https://start.spring.io/starter.zip \
  -d type=maven-project -d language=java -d bootVersion=3.5.0 \
  -d groupId=com.ecotrack -d artifactId=ecotrack-office-api \
  -d packageName=com.ecotrack.ecotrack -d javaVersion=21 \
  -d dependencies=web,data-jpa,mysql,security,validation,mail,lombok,devtools \
  -o ecotrack-office-api.zip

# 2. Frontend scaffold
ng new ecotrack-office-frontend --routing --style scss --strict
cd ecotrack-office-frontend && ng add @angular/material

# 3. Local dev environment
docker-compose up -d   # MySQL 8 + MailHog ready
```
