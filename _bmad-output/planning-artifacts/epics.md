---
stepsCompleted: ['step-01-validate-prerequisites', 'step-02-design-epics', 'step-03-create-stories', 'step-04-final-validation']
inputDocuments:
  - '_bmad-output/planning-artifacts/prd.md'
  - '_bmad-output/planning-artifacts/architecture.md'
---

# EcoTrack Office - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for EcoTrack Office, decomposing the requirements from the PRD and Architecture into implementable stories organized around user value.

## Requirements Inventory

### Functional Requirements

FR1: A visitor can register an account with name, email, password, and a valid company invitation code
FR2: A registered user can log in and receive a session token valid for 8 hours
FR3: An authenticated user can log out and invalidate their session
FR4: An Organization Admin can create, update, deactivate, and delete user accounts
FR5: An Organization Admin can assign and change roles (Employee, Organization Admin, Technician) for any user
FR6: The system enforces role-based access at API level: Employees (self-service only), Organization Admins (full admin), Technicians (incident management only)
FR7: An Employee can view and update their own profile and saved search preferences
FR8: An Organization Admin can create, update, and deactivate floors within the building
FR9: An Organization Admin can create, update, and deactivate rooms (Desk area / Meeting room) within a floor, with energy-management flag and surface area in m²
FR9b: The system enforces 80%-capacity opening policy; a room/floor opens automatically when current active capacity reaches 80%; Technician can manually open/close
FR10: An Organization Admin can create, update, and deactivate individual desks with attributes (equipment list, floor plan position)
FR11: An Organization Admin can upload and replace the SVG floor plan for any floor
FR12: An Organization Admin can associate desks and rooms with their SVG anchor positions on the floor plan
FR13: An Employee can view the building as an interactive map with rectangular tiles representing floors and rooms; only available rooms are selectable
FR14: The map displays each resource with a real-time color-coded status (available, reserved, unavailable/incident)
FR15: When a room tile is clicked, a room-level view opens showing individual desks as available or reserved; a tooltip allows booking by shift
FR16: *(Nice-to-have — post-MVP)* An Employee can filter the map by criteria (proximity to restrooms, window-facing, equipment type)
FR17: An Employee can access a list/table view of available spaces as an alternative to the map
FR18: An Employee can view a detail card for any desk or room (equipment, capacity, zone, current status)
FR19: An Employee can reserve an available desk or meeting room for a specific date and shift (morning 08:00–14:00 / afternoon 14:00–20:00); maximum 7 days in advance
FR19b: The system prevents booking more than 7 days in advance (configurable)
FR20: The system prevents double-booking: a reserved resource cannot be booked by another user for the same shift
FR21: An Employee can view their upcoming and past reservations
FR22: An Employee can cancel their own reservation before the reservation start time
FR22b: A Technician can modify or cancel any reservation to resolve conflicts; no automatic user notification in demo
FR23: The system saves an Employee's last-used search criteria and pre-fills them on subsequent visits
FR23b: An Employee can mark a workday as remote work; the system records it for CO₂ savings estimation
FR24: An Employee can confirm check-in directly from within the app on their active reservation (no QR code)
FR25: *(Nice-to-have — post-MVP)* An Employee can confirm check-in via a unique email link, without requiring an active app session
FR26: The system automatically releases a reservation if check-in is not confirmed within 15 minutes of start time (timeout configurable by Organization Admin)
FR27: *(Nice-to-have — post-MVP)* The system sends a reminder to the Employee 10 minutes before the auto-release deadline
FR28: A released desk becomes immediately available for new bookings in real time
FR29: Any authenticated user can report an incident on a specific resource with a text description and optional photo
FR30: Upon incident submission, the system automatically marks the resource as unavailable and removes it from booking availability
FR31: The system sends an immediate push notification to all Technicians when a new incident is submitted, with location and description
FR32: A Technician can update incident status (open → in progress → resolved)
FR33: When a Technician marks an incident resolved, the resource is automatically re-enabled for booking
FR34: An Organization Admin can view all incidents with status, resolution time, and technician assignment
FR35: An Organization Admin can view total reservations per zone for the current day and current week
FR36: An Organization Admin can view occupancy rate (confirmed check-ins vs. total reservations) per zone per day
FR37: The system generates a zone consolidation suggestion when daily attendance falls below a configurable threshold, identifying zones to activate and zones to shut down
FR38: *(Nice-to-have — post-MVP)* An Organization Admin can send a targeted notification to Employees booked in a zone recommended for shutdown
FR39: An Organization Admin can export an occupancy summary report for a selected date range
FR40: The system displays a privacy notice and requests explicit consent during registration
FR41: An Employee can request the export of their personal data (reservations, profile)
FR42: An Organization Admin can configure the data retention period for reservation history (default: 12 months)
FR43: The system maintains an audit log of all reservation and incident state-change events, accessible to the Organization Admin

### NonFunctional Requirements

NFR1: Initial load (application shell + active floor map): ≤ 3 seconds on a 50 Mbps connection
NFR2: Core API responses (availability check, reservation create/cancel, check-in): ≤ 500ms at p95
NFR3: Floor plan assets and desk data loaded per active floor only (lazy loading); no full pre-load on startup
NFR4: Availability polling: every 30 seconds; stale data indicated clearly if refresh fails
NFR5: Incident photo uploads processed asynchronously; UI confirms submission immediately without blocking
NFR6: All data in transit encrypted via HTTPS/TLS 1.2+
NFR7: Passwords stored with bcrypt (minimum cost factor 12); never logged or returned in API responses
NFR8: JWT tokens expire after 8 hours; refresh token strategy required for continuous sessions
NFR9: JWT stored in HttpOnly, Secure, SameSite=Strict cookies
NFR10: All API endpoints require valid authentication except login, registration, and email check-in link
NFR11: Role enforcement applied at API layer (Spring Security), not only in the Angular frontend
NFR12: Incident photo uploads: server-side MIME-type validation, 5MB maximum
NFR13: All user-submitted text sanitized server-side to prevent injection attacks
NFR14: System supports up to 500 concurrent authenticated users without performance degradation
NFR15: Database schema supports multiple buildings from day one; no hardcoded limits on floors, zones, or desks
NFR16: Availability target: 99% during office hours (07:00–20:00 local time, Monday–Friday)
NFR17: Graceful degradation: if API is unreachable, SPA displays last-known floor map with "live data unavailable" banner; booking actions disabled
NFR18: Auto-release scheduler recovers pending jobs from DB state on server restart (no in-memory job state)
NFR19: OpenAPI/Swagger specification maintained in sync with Spring Boot controllers; serves as binding frontend–backend contract
NFR20: Each of the four functional blocks independently deployable with its own DB schema prefix
NFR21: All environment-specific values provided via environment variables — no hardcoded configuration

### Additional Requirements

- **Project scaffold (Epic 0, Story 1):** Angular CLI 21 workspace initialized with routing, SCSS, strict mode, and Angular Material; Spring Boot 3.5.x via Spring Initializr with web, data-jpa, mysql, security, validation, mail, lombok, devtools dependencies
- **Monorepo structure:** `ecotrack-office/frontend/` (Angular) + `ecotrack-office/backend/` (Spring Boot) in a single repository
- **Flyway DB migrations:** All schema changes managed via Flyway; migration version numbers coordinated globally (`V1__users.sql`, `V2__assets.sql`, etc.); Spring Boot auto-runs on startup
- **Table prefixes:** MySQL tables prefixed by block (`usr_`, `ast_`, `rsv_`, `anl_`) in a shared single MySQL database
- **Refresh token persistence:** Refresh tokens stored in DB table `usr_refresh_tokens` (revocable, auditable, survives server restarts)
- **SSE for real-time notifications:** Spring `SseEmitter` used for server-to-client incident push notifications (no WebSocket at MVP)
- **Docker Compose local dev:** `docker-compose.yml` with MySQL 8 + MailHog for reproducible dev environment across team
- **OpenAPI/Swagger spec:** SpringDoc OpenAPI v2.8.x with Swagger UI at `/swagger-ui.html`; spec defined Week 1 as parallel development contract
- **RFC 7807 Problem Details:** `GlobalExceptionHandler` (@ControllerAdvice) returns `ProblemDetail` for all API errors; never `Map<String, Object>`
- **Angular Material shared library:** Established in Week 1; `material.module.ts` re-exports used across all feature modules
- **URI versioning:** All API endpoints prefixed with `/api/v1/`
- **Filesystem photo storage:** Incident photos stored on local filesystem via `UPLOAD_DIR` environment variable
- **GDPR compliance:** Explicit consent at registration; right to access/erasure; data minimization; 12-month configurable retention; audit log minimum 90 days
- **Angular architecture:** Feature module per block (lazy-loaded); `core/` for singleton services/guards/interceptors; `shared/` for common components and Angular Material re-exports
- **Two HTTP interceptors:** `AuthInterceptor` (JWT cookie injection) + `ErrorInterceptor` (global RFC 7807 parsing)
- **App-based check-in:** Authenticated employee confirms check-in directly from the active reservation view via `PATCH /api/v1/reservations/{id}/check-in`; no QR code, no external token
- **Remote-work indicator:** `rsv_remote_work` table stores one row per user per date; used to calculate CO₂ savings in the analytics dashboard
- **Shift-based booking:** Reservations use `Shift` ENUM (MORNING / AFTERNOON) instead of arbitrary start/end times; unique constraint on (resource_id, date, shift)

### UX Design Requirements

_(No UX Design document found — UX requirements are derived from user journeys in the PRD)_

UX-DR1: Map-first booking interface — the building floor plan is the primary interaction surface for finding and reserving workspaces
UX-DR2: Color-coded resource status overlay on the floor map (green = available, orange = active zone / energy nudge, red = unavailable/incident)
UX-DR3: *(Nice-to-have — post-MVP)* Zone heat overlay nudging employees toward energy-efficient clustering without mandating it
UX-DR4: *(Nice-to-have — post-MVP)* Criteria-based filter panel on the map (proximity to restrooms, window-facing, equipment type)
UX-DR5: List/table fallback view for all available spaces (accessibility + low-bandwidth scenario)
UX-DR6: Desk/room detail card with equipment, capacity, zone, and current status
UX-DR7: Onboarding tooltip overlay on first login (map-first UX is unfamiliar to non-technical users)
UX-DR8: *(Nice-to-have — post-MVP)* Pre-release reminder email sent at T−10 min with email-link check-in (no session required)
UX-DR9: Incident report form with text description + optional photo attachment, completable in under 30 seconds
UX-DR10: Admin dashboard with daily/weekly occupancy summary, open incidents count, and zone consolidation suggestion card
UX-DR11: WCAG AA color contrast (4.5:1 minimum) enforced via Angular Material theme; all primary actions keyboard-navigable; `aria-label` on all icons and status indicators
UX-DR12: Responsive layout across desktop, tablet, and mobile (Chrome, Firefox, Safari, Edge — latest two stable versions)

### FR Coverage Map

FR1: Epic 1 — User registration
FR2: Epic 1 — Login / JWT session
FR3: Epic 1 — Logout / session invalidation
FR4: Epic 1 — Admin user CRUD
FR5: Epic 1 — Role assignment
FR6: Epic 1 — RBAC enforcement at API layer
FR7: Epic 1 — Employee profile & saved preferences
FR8: Epic 2 — Floor CRUD
FR9: Epic 2 — Room CRUD (Desk area / Meeting room) with energy flag and m²
FR9b: Epic 2 — 80%-capacity opening policy (auto + manual by Technician)
FR10: Epic 2 — Desk CRUD with equipment attributes
FR11: Epic 2 — SVG floor plan upload
FR12: Epic 2 — Desk ↔ SVG anchor association
FR13: Epic 2 — Interactive floor map viewer (building-level room tiles)
FR14: Epic 2 — Real-time color-coded status overlay
FR15: Epic 2 — Room drill-down with desk view and shift tooltip
FR16: *(post-MVP)* Epic 2 — Map criteria filters
FR17: Epic 2 — List/table fallback view
FR18: Epic 2 — Desk/room detail card
FR19: Epic 3 — Shift-based desk/room reservation (morning / afternoon)
FR19b: Epic 3 — 7-day advance booking limit
FR20: Epic 3 — Double-booking prevention (per shift)
FR21: Epic 3 — My reservations view
FR22: Epic 3 — Reservation cancellation
FR22b: Epic 3 — Technician modifies/cancels reservations (conflict resolution)
FR23: Epic 3 — Saved search preferences
FR23b: Epic 3 — Remote-work day indicator (CO₂ savings input)
FR24: Epic 3 — App-based check-in (from active reservation view)
FR25: *(post-MVP)* Epic 3 — Email link check-in
FR26: Epic 3 — Auto-release scheduler (15 min timeout)
FR27: *(post-MVP)* Epic 3 — Pre-release reminder email (T−10 min)
FR28: Epic 3 — Immediate desk availability after release
FR29: Epic 4 — Incident reporting with photo
FR30: Epic 4 — Automatic resource blocking on incident
FR31: Epic 4 — Real-time SSE push notification to technicians
FR32: Epic 4 — Incident status lifecycle (open → in progress → resolved)
FR33: Epic 4 — Resource re-enabled on incident resolution
FR34: Epic 4 — Organization Admin incident overview
FR35: Epic 4 — Occupancy dashboard (reservations per zone)
FR36: Epic 4 — Check-in rate per zone per day
FR37: Epic 4 — Zone consolidation suggestion engine
FR38: *(post-MVP)* Epic 4 — Targeted employee notification (zone shutdown)
FR39: Epic 4 — Occupancy report export
FR40b: Epic 4 — Energy savings estimate per closed room (m² formula)
FR40c: Epic 4 — CO₂ savings estimate (closed rooms + remote-work days)
FR40: Epic 1 — GDPR consent at registration
FR41: Epic 4 — Personal data export (GDPR)
FR42: Epic 4 — Data retention configuration
FR43: Epic 4 — Audit log

## Epic List

### Epic 0: Project Foundation & Infrastructure
Establish the full development environment and project scaffold so all four development blocks can proceed in parallel from Week 1.
**FRs covered:** _(none — technical foundation enabling all epics)_

### Epic 1: User Management & Authentication
Users can register, log in, manage their profiles, and access the platform according to their role; Organization Admins can administer accounts and assign roles; RBAC is enforced at the API layer.
**FRs covered:** FR1, FR2, FR3, FR4, FR5, FR6, FR7, FR40

### Epic 2: Physical Asset Management & Interactive Floor Map
Organization Admins can manage the complete building inventory (floors, rooms, desks) via dedicated admin views; rooms have a type (Desk area / Meeting room), surface area in m², and an 80%-capacity opening policy. Employees explore the building via an interactive map of rectangular room tiles with a desk drill-down view; real-time color-coded availability; list/table fallback.
**FRs covered:** FR8, FR9, FR9b, FR10, FR11, FR12, FR13, FR14, FR15, FR17, FR18 — FR16 post-MVP

### Epic 3: Reservations, Check-in & Auto-release
Employees reserve desks and rooms by shift (morning / afternoon) up to 7 days in advance, mark remote-work days, and confirm presence directly from within the app. Technicians can modify or cancel reservations to resolve conflicts. Abandoned reservations are released automatically.
**FRs covered:** FR19, FR19b, FR20, FR21, FR22, FR22b, FR23, FR23b, FR24, FR26, FR28 — FR25, FR27 post-MVP

### Epic 4: Incidents, Analytics & Data Governance
Any user can report a resource incident; technicians are notified in real time via SSE; the Organization Admin has a full occupancy dashboard, energy/CO₂ savings estimates per closed room, zone consolidation suggestions, and GDPR-compliant data governance tools.
**FRs covered:** FR29, FR30, FR31, FR32, FR33, FR34, FR35, FR36, FR37, FR39, FR40b, FR40c, FR41, FR42, FR43 — FR38 post-MVP

<!-- Repeat for each epic in epics_list (N = 1, 2, 3...) -->

## Epic 0: Project Foundation & Infrastructure

Establish the full development environment and project scaffold so all four development blocks can proceed in parallel from Week 1. This epic has no direct functional requirements but is the prerequisite for all subsequent epics.

### Story 0.1: Initialize Angular Frontend Workspace

As a developer,
I want a fully scaffolded Angular 21 workspace with routing, SCSS, strict TypeScript, Angular Material, and the core/shared module structure,
So that all four feature teams can start implementing their feature modules against a consistent foundation from day one.

**Acceptance Criteria:**

**Given** an empty `frontend/` directory
**When** the Angular workspace is initialized
**Then** `ng new ecotrack-office-frontend --routing --style scss --strict` executes successfully
**And** Angular Material is added via `ng add @angular/material` with a custom theme
**And** the `core/` module is created containing: `auth/` (AuthService, AuthGuard, RoleGuard, JwtService), `interceptors/` (AuthInterceptor, ErrorInterceptor), and `sse/` (SseNotificationService stubs)
**And** the `shared/` module is created with `material.module.ts` (Angular Material re-exports), and stub components: `loading-spinner/`, `confirm-dialog/`, `error-banner/`
**And** lazy-loaded stub routes exist for `users/`, `assets-mgmt/`, `reservations/`, and `analytics/` modules in `app.routes.ts`
**And** `environment.ts` and `environment.prod.ts` contain `apiUrl`, `sseUrl`, and `pollInterval` variables
**And** `ng lint` and `ng build` run without errors

---

### Story 0.2: Initialize Spring Boot Backend

As a developer,
I want a fully initialized Spring Boot 3.5.x Maven project with all required dependencies, block package structure, Flyway, OpenAPI/Swagger, and a GlobalExceptionHandler returning RFC 7807 Problem Details,
So that all four backend teams share a consistent API foundation and error format from the first commit.

**Acceptance Criteria:**

**Given** an empty `backend/` directory
**When** the Spring Boot project is initialized via Spring Initializr
**Then** the project includes dependencies: `web`, `data-jpa`, `mysql`, `security`, `validation`, `mail`, `lombok`, `devtools`
**And** manual dependencies are added: `springdoc-openapi-starter-webmvc-ui` v2.8.x, `jjwt-api/impl/jackson`, `flyway-core`
**And** package structure is created: `com.ecotrack.users`, `com.ecotrack.assets`, `com.ecotrack.reservations`, `com.ecotrack.analytics`, `com.ecotrack.common`
**And** `GlobalExceptionHandler` (`@ControllerAdvice`) returns `ProblemDetail` (Spring 6 native) for all unhandled exceptions — never `Map<String, Object>`
**And** `OpenApiConfig` configures Swagger UI at `/swagger-ui.html` with JWT bearer security scheme
**And** all config (DB URL, JWT secret, CORS origins, SMTP) is externalized via environment variables in `application.properties` — zero hardcoded values
**And** Flyway baseline migration `V1__init.sql` (empty baseline) runs successfully on application startup
**And** `mvn test` passes with zero failures

---

### Story 0.3: Local Development Environment Setup

As a developer,
I want a Docker Compose configuration that starts MySQL 8 and MailHog with a single command, plus `.env.example` files and a README with full setup instructions,
So that every team member has an identical, reproducible dev environment ready in under 2 minutes.

**Acceptance Criteria:**

**Given** the project root directory
**When** `docker-compose up -d` is executed
**Then** a MySQL 8 container starts on port 3306 with the `ecotrack` database initialized
**And** a MailHog container starts on port 1025 (SMTP) and 8025 (web UI)
**And** both containers use named volumes for data persistence across restarts
**And** `.env.example` files exist at project root, `frontend/`, and `backend/` with all required variable names and example values (no real secrets)
**And** `README.md` documents the complete setup sequence: prerequisites, clone, env file copy, `docker-compose up`, `ng serve`, `mvn spring-boot:run`
**And** the Angular dev server proxies API calls to Spring Boot without CORS errors using the env-configured `CORS_ALLOWED_ORIGINS`

---

## Epic 1: User Management & Authentication

Users can register, log in, manage their profiles, and access the platform according to their role; Organization Admins can administer accounts and assign roles; RBAC is enforced at the API layer.

### Story 1.1: User Registration with GDPR Consent

As a visitor,
I want to register an account with my name, email, password, and a valid company invitation code, and provide explicit GDPR consent,
So that I can access the platform tied to my organization and my personal data is processed lawfully.

**Acceptance Criteria:**

**Given** I am on the registration page
**When** I submit a valid name, email, password, company invitation code, and the consent checkbox ticked
**Then** my account is created with the EMPLOYEE role by default and is linked to the organization associated with the invitation code
**And** my password is stored hashed with bcrypt (cost factor ≥ 12) — never in plain text
**And** the `usr_users` table (Flyway `V2__users.sql`) stores: id, name, email, hashed_password, role, organization_id FK, gdpr_consent, consent_at, is_active, created_at, search_preferences (JSON)
**And** a 201 Created response returns the user profile (no password field)
**And** if the invitation code is invalid or does not exist, a 400 Bad Request Problem Detail is returned
**And** if the email already exists, a 409 Conflict Problem Detail is returned
**And** if any field fails validation (empty name, invalid email, password < 8 chars, missing code), a 400 Bad Request Problem Detail with field-level errors is returned
**And** the Angular registration form shows inline validation errors before submission
**And** the consent checkbox is required — form cannot be submitted without it

---

### Story 1.2: User Login and Secure Session Management

As a registered user,
I want to log in with my email and password and remain authenticated for up to 8 hours,
So that I can access the platform without re-authenticating on every request.

**Acceptance Criteria:**

**Given** I have a registered account and am on the login page
**When** I submit valid credentials
**Then** a JWT access token (8h expiry) is issued and set as an `HttpOnly; Secure; SameSite=Strict` cookie
**And** a refresh token is persisted in `usr_refresh_tokens` (token_hash, user_id, expires_at, revoked) and set as a separate `HttpOnly` cookie
**And** the Angular `AuthService` stores the user role from JWT claims and exposes it via `currentUser$` observable
**And** `AuthGuard` redirects unauthenticated requests to `/login`; `RoleGuard` returns 403 for insufficient roles
**And** `POST /api/v1/auth/refresh` accepts a valid refresh token cookie and issues a new access token
**And** `POST /api/v1/auth/logout` revokes the refresh token in DB and clears both cookies (204 No Content)
**And** invalid credentials return 401 Unauthorized Problem Detail — no indication of which field is wrong
**And** a deactivated account returns 403 Forbidden Problem Detail

---

### Story 1.3: Employee Profile & Saved Search Preferences

As an employee,
I want to view and update my profile information and save my workspace search preferences,
So that EcoTrack pre-fills my criteria on subsequent visits and I can keep my information up to date.

**Acceptance Criteria:**

**Given** I am authenticated as an employee
**When** I navigate to my profile page
**Then** `GET /api/v1/users/me` returns my name, email, role, and current saved search preferences (no password field)
**And** `PUT /api/v1/users/me` accepts updated name and preferences stored as JSON in `usr_users.search_preferences`
**And** saved preferences include: near_restrooms (boolean), window_facing (boolean), equipment_types (string array)
**And** when I open the floor map filter panel, it is pre-filled with my saved preferences
**And** if I update filter criteria on the map, I can save them with a single click and they persist for future visits
**And** email changes are rejected by this endpoint (admin-only operation); a 400 is returned if attempted
**And** all text input is sanitized server-side; invalid data returns 400 Bad Request

---

### Story 1.4: Organization Admin — User Administration

As an Organization Admin,
I want to create, update, deactivate, and delete user accounts, and assign roles,
So that I can manage who has access to the platform and what they are authorized to do.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin
**When** I navigate to the user management admin panel
**Then** `GET /api/v1/users` returns a paginated list of all users with name, email, role, and active status
**And** `POST /api/v1/users` creates a new account with a specified role (EMPLOYEE, ORGANIZATION_ADMIN, or TECHNICIAN)
**And** `PUT /api/v1/users/{id}` updates name, email, or role of any user
**And** `PATCH /api/v1/users/{id}/deactivate` sets `is_active = false`; the user receives 403 on their next login attempt
**And** `DELETE /api/v1/users/{id}` permanently removes the user and all associated personal data (GDPR erasure)
**And** every role change is recorded in the audit log (`anl_audit_log`) with timestamp and acting admin's user_id
**And** attempting any of these actions as EMPLOYEE or TECHNICIAN returns 403 Forbidden
**And** the Angular admin component displays a confirmation dialog before deactivation or deletion

---

## Epic 2: Physical Asset Management & Interactive Floor Map

Organization Admins can manage the complete building inventory (floors, rooms, desks) via dedicated admin views; rooms have a type (Desk area / Meeting room), surface area in m², and an 80%-capacity opening policy. Employees explore the building via an interactive map of rectangular room tiles with a desk drill-down view and list/table fallback.

### Story 2.1: Floor & Room Management

As an Organization Admin,
I want to create, update, and deactivate floors and rooms (Desk area or Meeting room) within the building, with the system automatically opening new rooms when capacity reaches 80%,
So that the building structure is accurately modelled, energy management is tracked, and bookings are consolidated by default.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin
**When** I use the asset admin panel to manage building structure
**Then** `GET/POST /api/v1/floors` and `PUT/PATCH /api/v1/floors/{id}` manage floors (Flyway `V3__assets.sql` creates `ast_floors`: id, name, level_number, is_active, created_at)
**And** `GET/POST /api/v1/rooms` and `PUT/PATCH /api/v1/rooms/{id}` manage rooms within a floor (`ast_rooms`: id, floor_id FK, name, type ENUM(DESK_AREA/MEETING_ROOM), surface_area_m2 DECIMAL, energy_managed BOOLEAN, is_open BOOLEAN, capacity INT, is_active, created_at)
**And** the system enforces the **80%-opening policy**: `GET /api/v1/rooms/open-next` returns the next room to open when active rooms reach ≥80% capacity; this is applied at booking-creation time automatically
**And** `PATCH /api/v1/rooms/{id}/open` and `PATCH /api/v1/rooms/{id}/close` allow a Technician to manually override the open/closed state of any room
**And** deactivating a floor automatically deactivates all its rooms and desks
**And** all responses return DTOs — never JPA entities directly
**And** all CRUD operations return correct HTTP codes: 201 Created, 200 OK, 204 No Content
**And** attempting these actions as EMPLOYEE returns 403 Forbidden

---

### Story 2.2: Desk Management

As an Organization Admin,
I want to create, update, and deactivate individual desks with their full attributes,
So that employees have accurate resource information when browsing and booking.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin
**When** I manage desks via the asset admin panel
**Then** `GET/POST /api/v1/desks` and `PUT/PATCH /api/v1/desks/{id}` manage desks (`ast_desks`: id, room_id FK, name, equipment JSON array, svg_anchor_id, status ENUM(AVAILABLE/UNAVAILABLE), is_active)
**And** the Angular desk form allows adding and removing equipment items (chairs, tables, TV, etc.) from a tag list
**And** deactivating a desk that has an active upcoming reservation returns 409 Conflict with a descriptive Problem Detail
**And** all responses are DTOs; entity classes are never serialized directly to the API

---

### Story 2.3: SVG Floor Plan Upload & Anchor Association

As an Organization Admin,
I want to upload an SVG floor plan for a floor and associate desks and rooms with their SVG anchor positions,
So that employees see an accurate visual representation of the physical layout.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin
**When** I upload an SVG file for a specific floor
**Then** `POST /api/v1/floor-plans/{floorId}` accepts multipart/form-data, validates MIME type (image/svg+xml), and stores the file at `{UPLOAD_DIR}/{floorId}.svg`
**And** an existing SVG for that floor is replaced atomically
**And** files exceeding 5MB are rejected with 400 Bad Request Problem Detail
**And** `GET /api/v1/floor-plans/{floorId}` serves the SVG with the correct Content-Type header
**And** `PUT /api/v1/desks/{id}/anchor` and `PUT /api/v1/rooms/{id}/anchor` accept an `svgAnchorId` string matching an element ID in the uploaded SVG
**And** the Angular upload component shows an SVG preview and allows clicking anchor elements to associate them with specific desks or rooms

---

### Story 2.4: Interactive Floor Map Viewer

As an employee,
I want to view the building as an interactive map of rectangular room tiles with real-time color-coded availability, and drill into a room to see individual desks and book by shift,
So that I can visually find and reserve a workspace in a few taps.

**Acceptance Criteria:**

**Given** I am authenticated as any user and navigate to the floor map
**When** the map loads
**Then** the `BuildingMapComponent` renders a grid of rectangular tiles, one per room on each floor; only open rooms are selectable
**And** each tile is color-coded: green = at least one desk AVAILABLE, orange = fully RESERVED, red = UNAVAILABLE/incident, grey = closed
**And** room availability is polled every 30 seconds via `interval(30000) + switchMap` in `FloorMapService`; polling cancels on component destroy
**And** clicking an available room tile opens the `RoomDrillDownComponent` showing individual desk markers (green/orange/red) overlaid on the room SVG
**And** clicking a desk marker displays a tooltip with desk name, equipment list, and two booking buttons: **Morning** / **Afternoon** (disabled if already reserved for that shift)
**And** if the API is unreachable during a poll, a "Live data unavailable" banner appears while the last-known state is preserved; booking actions are disabled
**And** the map (floor list + open rooms + desks for the clicked room) loads within ≤ 3 seconds on a 50 Mbps connection
**And** a dismissible tooltip overlay explains the map interface on first login and never reappears after dismissal
**And** all markers carry `aria-label` with name and status; primary interactions are keyboard-navigable

---

### Story 2.5: List View & Room Detail Card

> **Note — post-MVP:** Map criteria filters (FR16), zone heat overlay (UX-DR3), and filter panel (UX-DR4) are deferred to Growth. This story covers the MVP list/table fallback and the desk/room detail card.

As an employee,
I want to browse available spaces in a list/table view and view a detail card for any desk or room,
So that I can find and assess workspaces even without the map interface or on low-bandwidth connections.

**Acceptance Criteria:**

**Given** I am on the floor map page
**When** I switch to list/table view
**Then** `GET /api/v1/desks?floorId={id}&available=true` and `GET /api/v1/rooms?floorId={id}&available=true` return open, bookable resources
**And** the `ResourceListComponent` displays: name, room/floor, equipment, type, and current status in a sortable table
**And** the list view is the default when no SVG floor plan has been uploaded for a floor
**And** clicking any row opens a `ResourceDetailCard` showing: name, room, floor, equipment list, capacity (rooms), status, and booking buttons (Morning / Afternoon) for available resources
**And** both views are fully responsive across desktop, tablet, and mobile
**And** all status colors maintain WCAG AA contrast ratio (≥ 4.5:1)

---

## Epic 3: Reservations, Check-in & Auto-release

Employees reserve desks and rooms by shift (morning / afternoon) up to 7 days in advance, mark remote-work days, and confirm presence from within the app. Technicians can modify or cancel reservations to resolve conflicts. Abandoned reservations are released automatically.

### Story 3.1: Desk/Room Reservation & Conflict Prevention

As an employee,
I want to reserve an available desk or meeting room for a specific date and shift (morning or afternoon), up to 7 days in advance,
So that I am guaranteed a workspace when I arrive at the office.

**Acceptance Criteria:**

**Given** I am authenticated as an employee and have selected an available resource on the map or list
**When** I tap Morning or Afternoon on the desk tooltip / detail card
**Then** `POST /api/v1/reservations` creates the reservation (Flyway `V4__reservations.sql` creates `rsv_reservations`: id, user_id FK, resource_type ENUM(DESK/ROOM), resource_id, date, shift ENUM(MORNING/AFTERNOON), status ENUM(PENDING/CONFIRMED/CANCELLED/RELEASED), created_at)
**And** a database unique constraint on (resource_type, resource_id, date, shift) prevents double-booking; a concurrent duplicate returns 409 Conflict Problem Detail
**And** attempting to book more than 7 days in advance returns 400 Bad Request Problem Detail
**And** the resource status is immediately reflected as RESERVED in the next availability poll response
**And** the API responds within ≤ 500ms at p95
**And** an employee can only create reservations for themselves; booking on behalf of another user returns 403 Forbidden

---

### Story 3.2: My Reservations & Cancellation

As an employee,
I want to view my upcoming and past reservations and cancel any upcoming one before it starts,
So that I can manage my schedule and free up spaces I no longer need.

**Acceptance Criteria:**

**Given** I am authenticated as an employee
**When** I navigate to "My Reservations"
**Then** `GET /api/v1/reservations/me` returns my reservations paginated, grouped into upcoming and past sections
**And** each entry shows: resource name, floor, zone, date, time slot, and current status
**And** upcoming reservations display a "Cancel" action; past and released reservations do not
**And** `DELETE /api/v1/reservations/{id}` sets status = CANCELLED and immediately returns the resource to AVAILABLE
**And** attempting to cancel another user's reservation returns 403 Forbidden
**And** attempting to cancel a reservation whose start time has already passed returns 409 Conflict Problem Detail
**And** the Angular UI shows a confirmation dialog before submitting cancellation
**And** the floor map reflects the cancellation within the next 30-second availability poll cycle

---

### Story 3.3: App-based Check-in

As an employee,
I want to confirm my check-in directly from the app when I arrive at my reserved desk,
So that the system logs my presence and my reservation is not auto-released.

**Acceptance Criteria:**

**Given** I have an upcoming reservation with status PENDING and I am in the app
**When** I navigate to "My Reservations" and tap "Check in" on the active reservation
**Then** `PATCH /api/v1/reservations/{id}/check-in` sets the reservation status to CONFIRMED (204 No Content)
**And** only the reservation owner can check in; other users receive 403 Forbidden
**And** attempting to check in more than 15 minutes before the reservation start time returns 400 Bad Request Problem Detail
**And** attempting to check in on a CANCELLED or RELEASED reservation returns 409 Conflict Problem Detail
**And** on successful check-in, the desk status updates to CONFIRMED in the floor map within the next poll cycle
**And** the check-in event is written to the audit log with user_id and timestamp
**And** `PATCH /api/v1/reservations/{id}/check-in` validates the token, sets status = CONFIRMED, and deletes the used token (204 No Content)
**And** an expired token (> 15 min after reservation start) returns 410 Gone Problem Detail
**And** an already-used token returns 409 Conflict Problem Detail
**And** the `/check-in` Angular route is publicly accessible — no authenticated session required
**And** on successful check-in, the user sees a confirmation screen with their desk details
**And** the desk status updates to CONFIRMED in the floor map within the next poll cycle

---

### Story 3.4: *(Post-MVP — Nice-to-have)* Email Link Check-in & Pre-release Reminder

> This story is **deferred to Growth**. It depends on an email infrastructure (SMTP / MailHog) and token management (`rsv_checkin_tokens`) that are not required for the MVP.

As an employee,
I want to receive a reminder email before my reservation is auto-released and check in via a link without needing to be logged in,
So that I can confirm my presence frictionlessly when I cannot open the app.

**Acceptance Criteria:**

**Given** I have a reservation with status PENDING
**When** 10 minutes before the auto-release deadline, the reminder scheduler job runs
**Then** an email is sent to my registered address via JavaMailSender containing: resource name, floor, date, time slot, a one-time check-in link, and a note that the desk will be released in 10 minutes
**And** the check-in link follows the same `/check-in?token={token}` pattern as the QR code, using the same `PATCH /api/v1/reservations/{id}/check-in` endpoint
**And** the link works in any browser with no active app session required
**And** MailHog captures the email in the local dev environment for manual verification
**And** all SMTP configuration is provided via environment variables (`SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASS`, `MAIL_FROM`) — zero hardcoded values
**And** email send failures are logged at WARN level but do not affect reservation state or fail the scheduler job

---

### Story 3.5: Auto-release Scheduler

As the system,
I want to automatically release reservations where check-in was not confirmed within the configurable timeout,
So that ghost desks are eliminated and abandoned spaces become immediately available for other employees.

**Acceptance Criteria:**

**Given** a reservation has status PENDING and its start time + the auto-release timeout has elapsed
**When** the Spring `@Scheduled(fixedDelay = 60_000)` job runs
**Then** all eligible reservations are fetched: `SELECT WHERE status = PENDING AND start_time_of_shift < NOW() - auto_release_minutes`
**And** each eligible reservation status is set to RELEASED and its resource status is set back to AVAILABLE in the same transaction
**And** the auto-release timeout defaults to 15 minutes, configurable via environment variable `AUTO_RELEASE_MINUTES`
**And** `PUT /api/v1/config/auto-release-minutes` (ORGANIZATION_ADMIN role only) allows the Organization Admin to update the timeout value at runtime
**And** on server restart, the scheduler reads all PENDING reservations from DB and processes any that became eligible during downtime — no in-memory job state
**And** each auto-release event is written to the audit log (`anl_audit_log`: id, event_type, entity_type, entity_id, actor SYSTEM, timestamp)
**And** released resources propagate to the floor map availability within the next 30-second poll cycle

---

## Epic 4: Incidents, Analytics & Data Governance

Any user can report a resource incident; technicians are notified in real time via SSE; the Organization Admin has a full occupancy dashboard, zone consolidation suggestions, exportable reports, and GDPR-compliant data governance tools.

### Story 4.1: Incident Reporting & Automatic Resource Blocking

As any authenticated user,
I want to report an incident on a specific resource with a description and optional photo,
So that the problem is immediately known to the facilities team and the faulty resource stops accepting bookings.

**Acceptance Criteria:**

**Given** I am authenticated (any role) and viewing a resource detail card
**When** I submit the incident report form
**Then** `POST /api/v1/incidents` creates the incident (Flyway `V6__incidents.sql` creates `anl_incidents`: id, resource_type, resource_id, reported_by FK, description, photo_path, status ENUM(OPEN/IN_PROGRESS/RESOLVED), created_at, resolved_at, resolved_by FK)
**And** the resource status is immediately set to UNAVAILABLE in the same database transaction
**And** if a photo is attached, it is validated server-side: MIME type must be image/jpeg, image/png, or image/webp; size ≤ 5MB; invalid files return 400 Bad Request Problem Detail
**And** the photo upload is processed asynchronously (stored to `{UPLOAD_DIR}/incidents/{incidentId}`); the API returns 201 Created immediately without waiting for file write
**And** all submitted text is sanitized server-side before persistence
**And** the Angular `IncidentReportFormComponent` can be completed and submitted in under 30 seconds on mobile
**And** the incident creation is recorded in the audit log with reporter user_id and timestamp

---

### Story 4.2: Real-time Technician Notification & Incident Lifecycle

As a Technician,
I want to receive an instant push notification when a new incident is submitted and update its status through to resolution,
So that I can act immediately and the resource is re-enabled for booking once fixed.

**Acceptance Criteria:**

**Given** a Technician has an active session and a new incident is created
**When** the incident is persisted
**Then** the backend sends an SSE event via `SseEmitterRegistry` to all connected Technician clients: `event: INCIDENT_CREATED` with data: incidentId, resourceId, resourceType, location, description, timestamp
**And** the Angular `SseNotificationService` (native `EventSource`) subscribes to `GET /api/v1/sse/notifications` for TECHNICIAN-role users and displays an in-app toast notification
**And** `GET /api/v1/incidents` (TECHNICIAN and ORGANIZATION_ADMIN roles) returns a paginated list filterable by status
**And** `PATCH /api/v1/incidents/{id}/status` allows a Technician to transition: OPEN → IN_PROGRESS → RESOLVED
**And** when status becomes RESOLVED, the resource status is set back to AVAILABLE in the same transaction
**And** the resolution timestamp is recorded in `anl_incidents.resolved_at` and the technician in `resolved_by`
**And** each status transition is written to the audit log
**And** if the SSE connection drops, `EventSource` reconnects automatically via browser-native retry

---

### Story 4.3: Organization Admin — Incident Overview

As an Organization Admin,
I want to view all incidents with their status, resolution time, and technician information,
So that I can monitor operational health and identify recurring problems.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin
**When** I navigate to the incident management view
**Then** `GET /api/v1/incidents` returns a paginated list filterable by status, resource, and date range, with: resource name and location, reporter name, description, status, created_at, resolved_at, resolution duration, resolved_by name
**And** open incidents appear at the top sorted by created_at descending
**And** `GET /api/v1/incidents/{id}/photo` serves the incident photo with correct Content-Type
**And** the Angular `IncidentDetailComponent` shows all fields including photo preview and a timeline of status changes
**And** the admin dashboard overview card shows counts: open incidents, in-progress, resolved today
**And** attempting to access this view as EMPLOYEE returns 403 Forbidden

---

### Story 4.4: Occupancy Dashboard

As an Organization Admin,
I want to view occupancy statistics per zone for the current day and week including the check-in rate,
So that I have data-driven visibility into actual space utilization without manual data extraction.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin
**When** I open the occupancy dashboard
**Then** `GET /api/v1/analytics/occupancy?date={date}&granularity={day|week}` returns per-zone: total reservations, confirmed check-ins, check-in rate (%), and peak hour
**And** the Angular `OccupancyDashboardComponent` provides a daily view (today) and weekly view (current week) switchable via tabs
**And** each zone is displayed as a card showing its occupancy metrics and a simple bar visualization
**And** check-in rate is calculated from `rsv_reservations WHERE status = CONFIRMED` divided by total reservations for the period
**And** the dashboard refreshes occupancy data every 30 seconds using the same `interval(30000) + switchMap` pattern as the floor map
**And** attempting to access this view as EMPLOYEE or TECHNICIAN returns 403 Forbidden

---

### Story 4.5: Zone Consolidation Suggestions

> **Note — post-MVP:** The "Notify employees" button (FR38) is deferred to Growth. This story covers suggestion display and manual action only.

As an Organization Admin,
I want the system to suggest which zones to activate or shut down on low-attendance days,
So that I can concentrate occupancy into fewer zones and reduce energy waste.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin and daily attendance is below the configurable threshold
**When** I open the zone consolidation panel
**Then** `GET /api/v1/analytics/consolidation-suggestions?date={date}` returns: zones recommended to keep active, zones to shut down, and count of affected employees per zone
**And** the suggestion algorithm identifies zones with the fewest reservations and recommends consolidating employees into the most occupied zones
**And** the attendance threshold defaults to the value of `CONSOLIDATION_THRESHOLD_PERCENT` env var and is adjustable by the Organization Admin
**And** the Angular `ConsolidationSuggestionsComponent` shows: recommended active zones, zones to close, and a list of employees booked per zone (for manual outreach if needed)
**And** no automatic notification is sent to employees in demo scope (FR38 post-MVP)
**And** each consolidation suggestion view event is recorded in the audit log

---

### Story 4.6: Reporting, Data Export & GDPR Governance

As an Organization Admin and as an Employee,
I want to export occupancy reports and personal data, configure data retention, and access the full audit log,
So that EcoTrack complies with GDPR obligations and management has exportable operational data.

**Acceptance Criteria:**

**Given** I am authenticated as an Organization Admin
**When** I request an occupancy export
**Then** `GET /api/v1/analytics/export?from={date}&to={date}&format=csv` returns a CSV with: date, zone, total reservations, confirmed check-ins, check-in rate, open incidents — as a file download with `Content-Disposition: attachment; filename=occupancy-{from}-{to}.csv`

**Given** I am authenticated as an Employee
**When** I request my personal data export
**Then** `GET /api/v1/users/me/export` returns a JSON file containing my full profile, all reservations, and saved preferences (GDPR Article 20 data portability)

**Given** I am authenticated as an Organization Admin
**When** I configure data retention
**Then** `PUT /api/v1/config/data-retention-months` (ORGANIZATION_ADMIN only) sets the retention period (default: 12 months)
**And** a scheduled job anonymizes `rsv_reservations` records older than the retention period by replacing personal identifiers with pseudonymized tokens
**And** `GET /api/v1/audit-log` (ORGANIZATION_ADMIN only) returns a paginated audit log with: event_type, entity_type, entity_id, actor (user_id or SYSTEM), timestamp — retained for a minimum of 90 days regardless of the general retention setting
**And** purge job executions and all data deletion events are themselves written to the audit log
