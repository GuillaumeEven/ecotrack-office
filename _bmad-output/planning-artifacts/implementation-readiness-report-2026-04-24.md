---
stepsCompleted: ['step-01-document-discovery', 'step-02-prd-analysis', 'step-03-epic-coverage-validation', 'step-04-ux-alignment', 'step-05-epic-quality-review', 'step-06-final-assessment']
workflowStatus: complete
project: ecotrack_office
date: '2026-04-24'
documentsAssessed:
  prd: '_bmad-output/planning-artifacts/prd.md'
  architecture: '_bmad-output/planning-artifacts/architecture.md'
  epics: '_bmad-output/planning-artifacts/epics.md'
  ux: '_bmad-output/planning-artifacts/ux-design-specification.md'
---

# Implementation Readiness Assessment Report

**Date:** 2026-04-24
**Project:** ecotrack_office

---

## PRD Analysis

### Functional Requirements

**User Management & Authentication**

- FR1: A visitor can register an account with name, email, and password
- FR2: A registered user can log in (MVP demo: HTTP Basic Auth); production will adopt token-based sessions with expiry and refresh strategy
- FR3: An authenticated user can log out and invalidate their session
- FR4: An Organization Admin can create, update, deactivate, and delete user accounts
- FR5: An Organization Admin can assign and change roles (Employee, Organization Admin, Technician) for any user
- FR6: The system enforces role-based access at API level: Employees (self-service only), Organization Admins (full admin), Technicians (incident management only)
- FR7: An Employee can view and update their own profile and saved search preferences

**Physical Asset Management**

- FR8: An Organization Admin can create, update, and deactivate floors within the building
- FR9: An Organization Admin can create, update, and deactivate zones within a floor, each with an energy-management flag
- FR10: An Organization Admin can create, update, and deactivate individual desks and meeting rooms with attributes (equipment list, capacity, floor plan position)
- FR11: An Organization Admin can manage floor maps via dynamic floor map generation (MVP) — manual SVG upload is deferred to Growth
- FR12: An Organization Admin can associate desks and rooms with generated map coordinates; manual SVG anchor association is deferred to Growth

**Interactive Floor Map**

- FR13: An Employee can view any floor as an interactive, dynamically generated floor map (no manual SVG upload required for MVP)
- FR14: The map displays each resource with a real-time color-coded status (available, reserved, unavailable/incident)
- FR15: The map displays a zone heat overlay showing zones with active reservations to nudge energy-efficient clustering
- FR16: An Employee can filter the map by criteria (proximity to restrooms, window-facing, equipment type)
- FR17: An Employee can access a list/table view of available spaces as an alternative to the map
- FR18: An Employee can view a detail card for any desk or room (equipment, capacity, zone, current status)

**Reservation & Booking**

- FR19: An Employee can reserve an available desk or meeting room for a specific date and time slot
- FR20: The system prevents double-booking: a reserved resource cannot be booked by another user for the same slot
- FR21: An Employee can view their upcoming and past reservations
- FR22: An Employee can cancel their own reservation before the reservation start time
- FR23: The system saves an Employee's last-used search criteria and pre-fills them on subsequent visits

**Check-in & Auto-release**

- FR24: An Employee can confirm check-in by scanning a QR code displayed at the desk
- FR25: An Employee can confirm check-in via a unique email link, without requiring an active app session
- FR26: The system automatically releases a reservation if check-in is not confirmed within 15 minutes of start time (timeout configurable by Organization Admin)
- FR27: The system sends a reminder to the Employee 10 minutes before the auto-release deadline
- FR28: A released desk becomes immediately available for new bookings in real time

**Incident Management**

- FR29: Any authenticated user can report an incident on a specific resource with a text description and optional photo
- FR30: Upon incident submission, the system automatically marks the resource as unavailable and removes it from booking availability
- FR31: The system sends an immediate push notification to all Technicians when a new incident is submitted, with location and description
- FR32: A Technician can update incident status (open → in progress → resolved)
- FR33: When a Technician marks an incident resolved, the resource is automatically re-enabled for booking
- FR34: An Organization Admin can view all incidents with status, resolution time, and technician assignment

**Analytics & Occupancy Dashboard**

- FR35: An Organization Admin can view total reservations per zone for the current day and current week
- FR36: An Organization Admin can view occupancy rate (confirmed check-ins vs. total reservations) per zone per day
- FR37: The system generates a zone consolidation suggestion when daily attendance falls below a configurable threshold, identifying zones to activate and zones to shut down
- FR38: An Organization Admin can send a targeted notification to Employees booked in a zone recommended for shutdown
- FR39: An Organization Admin can export an occupancy summary report for a selected date range

**Data & Privacy**

- FR40: The system displays a privacy notice and requests explicit consent during registration
- FR41: An Employee can request the export of their personal data (reservations, profile)
- FR42: An Organization Admin can configure the data retention period for reservation history (default: 12 months)
- FR43: The system maintains an audit log of all reservation and incident state-change events, accessible to the Organization Admin

**Total FRs: 43**

---

### Non-Functional Requirements

**Performance**

- NFR1: Initial load (application shell + active floor map): ≤ 3 seconds on a 50 Mbps connection
- NFR2: Core API responses (availability check, reservation create/cancel, check-in): ≤ 500ms at p95
- NFR3: Floor plan assets and desk data loaded per active floor only (lazy loading); no full pre-load on startup
- NFR4: Availability polling every 30 seconds; stale data indicated clearly if refresh fails
- NFR5: Incident photo uploads processed asynchronously; UI confirms submission immediately without blocking

**Security**

- NFR6: All data in transit encrypted via HTTPS/TLS 1.2+
- NFR7: Password hashing and secure persistence are required for production; for the MVP demo, password storage/hardening is simplified and full bcrypt-based persistence is deferred to Growth
- NFR8: MVP demo authentication uses HTTP Basic Auth (demo-only). Production will adopt token-based sessions (e.g., JWT) with appropriate expiry and refresh strategy
- NFR9: For production, tokens must be stored in HttpOnly, Secure, SameSite=Strict cookies; demo authentication may not follow the production token storage model
- NFR10: All API endpoints require valid authentication except login, registration, and email check-in link
- NFR11: Role enforcement applied at API layer (Spring Security), not only in the Angular frontend
- NFR12: Incident photo uploads: server-side MIME-type validation, 5MB maximum
- NFR13: All user-submitted text sanitized server-side to prevent injection attacks

**Scalability**

- NFR14: System supports up to 500 concurrent authenticated users without degradation
- NFR15: Database schema supports multiple buildings (multi-floor, multi-zone) from day one
- NFR16: No hardcoded limits on floors, zones, or desks per building

**Reliability**

- NFR17: Availability target: 99% during office hours (07:00–20:00 local time, Monday–Friday)
- NFR18: Graceful degradation: SPA displays last-known floor map with "live data unavailable" banner; booking actions disabled rather than silently failing
- NFR19: Auto-release scheduler recovers pending jobs from database state on server restart

**Maintainability**

- NFR20: OpenAPI/Swagger specification maintained in sync with Spring Boot controllers
- NFR21: Each of the four functional blocks deployable independently with its own database schema prefix
- NFR22: All environment-specific values provided via environment variables — no hardcoded configuration

**Accessibility (documented baseline)**

- NFR23: WCAG AA color contrast (4.5:1 minimum) enforced via Angular Material theme
- NFR24: All primary actions keyboard-navigable
- NFR25: List/table fallback view for map (also serves low-bandwidth scenarios)
- NFR26: aria-label on all icons and status indicators
- NFR27: Focus returned programmatically after modal dialogs and navigation events

**Total NFRs: 27**

---

### Additional Requirements

**SaaS Demo Mode Constraints**

- Demo banner displayed across all app screens and exported reports
- Demo signup flow creates demo tenant and Organization Admin account without payment collection
- Informational Pricing page: "Subscribe" triggers demo signup, not live billing
- Billing and invoicing: OUT OF SCOPE for this release
- Simplified tenant model: logical data segmentation (not hardened multi-tenant isolation)
- Usage telemetry may be recorded for evaluation but not for billing

-**Technical Architecture Constraints**

- Dynamic floor map generation rendered via Angular components; manual SVG upload/anchor association deferred for MVP (no third-party mapping library)
- Angular services + RxJS observables for state management; no NgRx required
- Angular HttpClient with interceptors to support demo Basic Auth headers and production token injection/global error handling
- OpenAPI/Swagger spec as the binding frontend–backend contract
- 4 students × 8 weeks; each owns one full CRUD vertical (Angular module + Spring Boot controller/service/repository + MySQL schema)
- Angular Material shared component library established in Week 1
- SMTP email delivery; provider configurable via environment variable

**Integration Requirements**

- No third-party system integrations at MVP scope
- REST API with JSON payloads designed for future calendar/HVAC integration

---

### PRD Completeness Assessment

The PRD was updated on 2026-05-19 with demo scope simplifications (see validation report). The update simplifies authentication for the MVP (HTTP Basic Auth for demo) and replaces manual SVG floor-plan upload/anchor association with a dynamic floor map generation approach; some persistence and hashing details (e.g., bcrypt) are deferred to the Growth phase.

These simplifications are intentional for the demo but introduce a warning for downstream artifacts: update epics, stories, architecture notes, and UX assumptions accordingly before implementation. The validation report rates the updated PRD as 4/5 with a "Warning" status due to the scope simplifications and recommends aligning affected artifacts.

Minor observation: FR38 (targeted notification to employees in shutdown zone) still overlaps with Growth-tier "notification system" — the mechanism (push/email) is not specified for MVP in the PRD itself; the architecture document should clarify the MVP delivery vehicle.

---

## Epic Coverage Validation

### Coverage Matrix

| FR     | PRD Requirement (short)                                         | Epic Coverage               | Status      |
|--------|-----------------------------------------------------------------|-----------------------------|-------------|
| FR1    | Visitor registration (name, email, password)                   | Epic 1 — Story 1.1          | ✓ Covered   |
| FR2    | Login + session (MVP Basic Auth; production token-based sessions planned) | Epic 1 — Story 1.2          | ✓ Covered   |
| FR3    | Logout / session invalidation                                   | Epic 1 — Story 1.2          | ✓ Covered   |
| FR4    | Admin CRUD on user accounts                                     | Epic 1 — Story 1.4          | ✓ Covered   |
| FR5    | Admin role assignment                                           | Epic 1 — Story 1.4          | ✓ Covered   |
| FR6    | RBAC enforcement at API layer                                   | Epic 1 — Story 1.2 / 1.4    | ✓ Covered   |
| FR7    | Employee profile & saved search preferences                     | Epic 1 — Story 1.3          | ✓ Covered   |
| FR8    | Admin floor CRUD                                                | Epic 2 — Story 2.1          | ✓ Covered   |
| FR9    | Admin zone CRUD with energy flag                                | Epic 2 — Story 2.1          | ✓ Covered   |
| FR10   | Admin desk & room CRUD with attributes                          | Epic 2 — Story 2.2          | ✓ Covered   |
| FR11   | Admin dynamic floor map generation (SVG upload deferred to Growth) | Epic 2 — Story 2.3          | ✓ Covered   |
| FR12   | Desk/room ↔ generated map coordinates (SVG anchor association deferred to Growth) | Epic 2 — Story 2.3          | ✓ Covered   |
| FR13   | Employee: view interactive, dynamically generated floor map      | Epic 2 — Story 2.4          | ✓ Covered   |
| FR14   | Real-time color-coded resource status on map                    | Epic 2 — Story 2.4          | ✓ Covered   |
| FR15   | Zone heat overlay (energy nudge)                                | Epic 2 — Story 2.5          | ✓ Covered   |
| FR16   | Map criteria filters                                            | Epic 2 — Story 2.5          | ✓ Covered   |
| FR17   | List/table fallback view                                        | Epic 2 — Story 2.5          | ✓ Covered   |
| FR18   | Desk/room detail card                                           | Epic 2 — Story 2.4          | ✓ Covered   |
| FR19   | Employee: reserve desk/room for date+slot                       | Epic 3 — Story 3.1          | ✓ Covered   |
| FR20   | Double-booking prevention                                       | Epic 3 — Story 3.1          | ✓ Covered   |
| FR21   | Employee: view upcoming & past reservations                     | Epic 3 — Story 3.2          | ✓ Covered   |
| FR22   | Employee: cancel own reservation before start                   | Epic 3 — Story 3.2          | ✓ Covered   |
| FR23   | Saved search criteria pre-fill on return                        | Epic 3 — Story 3.1 / 1.3    | ✓ Covered   |
| FR24   | QR code check-in                                                | Epic 3 — Story 3.3          | ✓ Covered   |
| FR25   | Email link check-in (no session required)                       | Epic 3 — Story 3.4          | ✓ Covered   |
| FR26   | Auto-release after 15-min timeout (configurable)               | Epic 3 — Story 3.5          | ✓ Covered   |
| FR27   | Pre-release reminder email T−10 min                             | Epic 3 — Story 3.4          | ✓ Covered   |
| FR28   | Released desk immediately available in real time                | Epic 3 — Story 3.5          | ✓ Covered   |
| FR29   | Incident reporting with text + optional photo                   | Epic 4 — Story 4.1          | ✓ Covered   |
| FR30   | Auto resource blocking on incident submission                   | Epic 4 — Story 4.1          | ✓ Covered   |
| FR31   | Push notification to all Technicians on new incident            | Epic 4 — Story 4.2          | ✓ Covered   |
| FR32   | Technician: incident status lifecycle (open→in progress→resolved) | Epic 4 — Story 4.2        | ✓ Covered   |
| FR33   | Resource re-enabled on incident resolved                        | Epic 4 — Story 4.2          | ✓ Covered   |
| FR34   | Admin: incident overview with status + resolution time          | Epic 4 — Story 4.3          | ✓ Covered   |
| FR35   | Admin: reservations per zone (day/week)                         | Epic 4 — Story 4.4          | ✓ Covered   |
| FR36   | Admin: check-in rate per zone per day                           | Epic 4 — Story 4.4          | ✓ Covered   |
| FR37   | Zone consolidation suggestion engine                            | Epic 4 — Story 4.5          | ✓ Covered   |
| FR38   | Admin: targeted notification to employees in shutdown zone      | Epic 4 — Story 4.5          | ✓ Covered   |
| FR39   | Occupancy report export (date range)                            | Epic 4 — Story 4.6          | ✓ Covered   |
| FR40   | GDPR consent at registration                                    | Epic 1 — Story 1.1          | ✓ Covered   |
| FR41   | Employee: personal data export (GDPR Art. 20)                   | Epic 4 — Story 4.6          | ✓ Covered   |
| FR42   | Admin: data retention configuration (default 12 months)        | Epic 4 — Story 4.6          | ✓ Covered   |
| FR43   | Audit log of reservation & incident events                      | Epic 4 — Story 4.6          | ✓ Covered   |

### Missing Requirements

None. All 43 functional requirements have traceable coverage in the epics and stories.

### Coverage Statistics

- Total PRD FRs: 43
- FRs covered in epics: 43
- Coverage percentage: **100%**
- Epics count: 5 (Epic 0 foundation + Epics 1–4)
- Stories count: 16 (3 foundation + 4 auth + 5 assets/map + 5 reservations/incidents/analytics)


---

## UX Alignment Assessment

### UX Document Status

**Found:** `_bmad-output/planning-artifacts/ux-design-specification.md` (53KB, 2026-04-20)

The UX document is comprehensive: executive summary, user personas, design system, color palette, typography, component strategy, 5 user journey flowcharts, and implementation approach. It was authored with reference to the PRD and architecture documents (confirmed in its frontmatter `inputDocuments`).

---

### UX ↔ PRD Alignment

| UX Requirement | PRD Requirement | Status |
|---|---|---|
| Map-first booking interface | FR13–FR18 (interactive, dynamically generated floor map; status overlay, filters, detail card) | ✅ Aligned |
| Color-coded status (green/amber/red/grey) | FR14 (real-time color-coded status) | ✅ Aligned — UX refines to 5 states (see note) |
| Zone heat overlay (energy nudge) | FR15 | ✅ Aligned |
| Criteria filter panel (restrooms, window, equipment) | FR16 | ✅ Aligned |
| List/table fallback view | FR17 | ✅ Aligned |
| Desk/room detail card (hover card) | FR18 | ✅ Aligned |
| Onboarding tooltip overlay on first login | Story 2.4 AC | ✅ Aligned |
| Pre-release reminder email + email-link check-in | FR25, FR27 | ✅ Aligned |
| Incident form completable in <30s on mobile | FR29 | ✅ Aligned |
| Admin dashboard — 4 KPI above fold | FR35, FR36, FR37 | ✅ Aligned |
| Technician SSE push notification + in-app toast | FR31 | ✅ Aligned |
| WCAG AA, keyboard nav, aria-labels | NFR23–NFR27 | ✅ Aligned |
| Role-based routing (EMPLOYEE → /floor-map, ADMIN → /dashboard) | FR6 | ✅ Aligned |
| Stateless check-in screen (QR + email token) | FR24, FR25 | ✅ Aligned |
| Optimistic UI + snackbar feedback | UX pattern, not FR | ✅ No conflict |
| **Mobile scope narrowed to check-in only** | **PRD: "fully responsive across desktop, tablet, and mobile"** | **⚠️ MISALIGNMENT** |

---

### UX ↔ Architecture Alignment

| UX Decision | Architecture Decision | Status |
|---|---|---|
| Angular Material (MDC/M3) as design system | Architecture: Angular Material shared library from Week 1 | ✅ Aligned |
| `FloorMapComponent` + `DeskMarkerComponent` overlaying generated map coordinates | Architecture: Dynamic floor map generation rendered via Angular components; manual SVG upload/anchor association deferred for MVP | ✅ Aligned |
| `ZoneHeatOverlayComponent` as SVG layer | Architecture: computed from zone occupancy data | ✅ Aligned |
| `SseNotificationService` via native `EventSource` | Architecture: Spring `SseEmitter` for incident push | ✅ Aligned |
| RxJS `interval(30000) + switchMap` for availability polling | Architecture: 30s polling, cancel on component destroy | ✅ Aligned |
| `AppShellComponent` with `mode: 'employee' | 'admin'` inputs | Architecture: role guards, lazy-loaded modules | ✅ Aligned |
| `MatBottomSheet` for booking drawer | Architecture: Angular Material components confirmed | ✅ Aligned |
| URL params for deep-link map state (`/floor-map?floor=3`) | Architecture: Angular Router with route guards | ✅ Aligned |
| 5 desk visual states (available/reserved/occupied/mine/blocked) | Architecture: 2 DB statuses (desk: AVAILABLE/UNAVAILABLE) + reservation status ENUM | ✅ Aligned — UX states are derived from combined desk + reservation status; fully supportable |
| High-contrast mode toggle on floor map | Architecture: not specified | ⚠️ Not architecturally blocked, but no story covers it |

---

### Alignment Issues

#### ⚠️ ISSUE 1 — Mobile Scope Discrepancy (Medium Risk)

**PRD says:** "Fully responsive across desktop, tablet, and mobile; Chrome, Firefox, Safari, Edge (latest two stable versions each)"

**UX Spec says:** "Interaction model: Mouse + keyboard primary; touch input supported only for the check-in confirmation screens" and "Secondary surface: Mobile browser for QR check-in and email-link check-in only."

**Impact:** The primary user journey for Guillermo explicitly shows him booking **from his phone on the bus** — filtering the map, finding a desk, and booking in 2 taps. The UX spec scopes mobile to check-in only, which would make Guillermo's core scenario non-functional on mobile.

**Recommendation:** The team must explicitly align scope before implementation begins. **Either:**
- (a) The UX is updated to define a mobile-friendly map and booking flow for Guillermo's scenario (adds scope), OR
- (b) The PRD is updated to narrow the mobile claim to check-in and read-only map view only, and Guillermo's journey is updated to reflect he books from desktop

This is the only material gap found. It must be resolved before Story 2.4 and 3.1 are implemented.

#### ⚠️ ISSUE 2 — High-Contrast Mode Toggle (Low Risk)

The UX spec mentions "the map includes a high-contrast mode toggle" (Accessibility section). No corresponding FR, NFR, or architectural story exists. This is a scope addition not tracked anywhere.

**Recommendation:** Either add a story to Epic 2 (Story 2.4 AC or a new story) to specify this, or explicitly mark it as out-of-scope for MVP to prevent silent scope creep.

---

### Warnings

None beyond the two issues above.

---

### UX Alignment Summary

- UX document: ✅ Present and comprehensive
- UX ↔ PRD alignment: ✅ 14/16 items aligned; 1 medium-risk scope discrepancy + 1 minor gap
- UX ↔ Architecture alignment: ✅ Fully aligned; no blocking conflicts
- Blocking issue: ⚠️ Mobile scope must be resolved before implementing Stories 2.4 and 3.1

---

## Epic Quality Review

### Epics & Stories Inventory Reviewed

- Epic 0: Project Foundation & Infrastructure (3 stories: 0.1, 0.2, 0.3)
- Epic 1: User Management & Authentication (4 stories: 1.1, 1.2, 1.3, 1.4)
- Epic 2: Physical Asset Management & Interactive Floor Map (5 stories: 2.1, 2.2, 2.3, 2.4, 2.5)
- Epic 3: Reservations, Check-in & Auto-release (5 stories: 3.1, 3.2, 3.3, 3.4, 3.5)
- Epic 4: Incidents, Analytics & Data Governance (6 stories: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6)

Total: 5 epics, 23 stories

---

### Best Practices Compliance Matrix

| Epic | User Value | Independent | Stories Sized OK | No Forward Deps | DB Tables Right | Clear ACs (BDD) | FR Traceability |
|------|-----------|-------------|-----------------|----------------|----------------|-----------|----------------|
| Epic 0 | ❌ intentional | ✅ | ✅ | ✅ | ✅ | ✅ | N/A |
| Epic 1 | ✅ | ✅ | ✅ | ✅ | ⚠️ audit_log gap | ✅ | ✅ |
| Epic 2 | ✅ | ✅ | ✅ | ✅ | ⚠️ UPLOAD_DIR gap | ✅ | ✅ |
| Epic 3 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Epic 4 | ✅ | ✅ | ⚠️ 4.6 broad | ✅ | ⚠️ audit_log gap | ✅ | ✅ |

---

### 🔴 Critical Violations

**None found.**

---

### 🟠 Major Issues

#### MAJOR-01 — `anl_audit_log` Flyway Migration Not Assigned

The `anl_audit_log` table has no explicit Flyway migration version number assigned in any story. It is referenced and written to across six stories: Story 1.4, Story 3.5, Story 4.1, Story 4.2, Story 4.5, and Story 4.6. The migration numbering in the epics document accounts for V1 through V6 (`init`, `users`, `assets`, `reservations`, `checkin_tokens`, `incidents`) but never assigns a version to `anl_audit_log` creation.

**Risk:** Without an explicit `V_audit_log.sql` Flyway migration, each developer will assume another student created it, leading to a runtime "table does not exist" failure that only surfaces at integration time (Week 6–7).

**Recommendation:** Add a dedicated Flyway migration (e.g., `V7__audit_log.sql`) creating the `anl_audit_log` table, assigned to a specific story. Most logically, this should be added to Story 1.4 AC (the first story that writes to it), or to a new Story 4.0 "Shared Audit Infrastructure" at the start of Epic 4. The table must exist before Epic 1 Story 1.4 runs in production.

---

#### MAJOR-02 — Story 4.6 Multi-Persona Breadth

Story 4.6 combines 4 distinct functional requirements (FR39, FR41, FR42, FR43) and 2 user personas (Organization Admin + Employee) in a single story, with 4 separate `Given/When/Then` blocks. The story covers: occupancy CSV export, personal data JSON export, data retention configuration, and audit log access. This creates a story that is unlikely to be completable by one student in a single sprint.

**Risk:** The story cannot pass sprint acceptance as a single atomic unit. Testing requires both admin and employee logins, two separate export flows, one scheduled job, and pagination logic on the audit log.

**Recommendation:** Split Story 4.6 into two stories:
- **Story 4.6a** (Admin): Occupancy CSV export + data retention configuration + audit log access (FR39, FR42, FR43)
- **Story 4.6b** (Employee): Personal data JSON export / GDPR portability (FR41)

Or keep as-is with explicit sprint planning note that this story spans multiple sessions and should be assigned to the most experienced student with priority time allocation.

---

### 🟡 Minor Concerns

#### MINOR-01 — Epic 0 is a Technical-Only Foundation Epic

Epic 0 delivers zero user value by design — this is explicitly stated in its description: "no direct functional requirements." Per strict best practices, epics should deliver user value. This is a well-established greenfield exception (the "Epic 0 convention") and is intentional here, but should be acknowledged.

**Recommendation:** No remediation needed. Annotate it clearly throughout sprint planning as a prerequisite technical block, not a user-value increment. Do not include it in sprint burndown charts as user-facing progress.

---

#### MINOR-02 — `UPLOAD_DIR` Env Variable Missing from Story 0.3

Story 0.3 (Docker Compose + env setup) creates `.env.example` files and documents all required environment variables. However, `UPLOAD_DIR` (used for floor plan assets or incident photo storage in Story 2.3 and Story 4.1) is not mentioned in Story 0.3's acceptance criteria. Backend students working on Stories 2.3 and 4.1 will discover this variable requirement when they implement it — risking inconsistent paths across dev environments.

**Recommendation:** Add `UPLOAD_DIR` to the Story 0.3 AC: "`.env.example` includes `UPLOAD_DIR=/var/ecotrack/uploads` as a required variable". No story rewrite needed — a single line addition to Story 0.3.

---

#### MINOR-03 — Story 4.5 Zone Consolidation Algorithm Underspecified

The acceptance criteria for Story 4.5 describe the algorithm as: "identifies zones with the fewest reservations and recommends consolidating employees into the most occupied zones." No formal criteria are defined: What constitutes "few" reservations? How are zones ranked? What is the minimum output (suggest at least N zones to shut down)?

Without specificity, two students would independently implement different algorithms, producing non-reproducible suggestions. This risks a scope dispute during demo if the algorithm behavior is unexpected.

**Recommendation:** Add one concrete AC to Story 4.5: "The algorithm ranks zones by ascending reservation count for the given day; the bottom X% of zones (where X = `100 - CONSOLIDATION_THRESHOLD_PERCENT`) are marked as consolidation candidates; zones with zero reservations are always identified as candidates." Alternatively, post a team decision in an ADR before Story 4.5 implementation begins.

---

#### MINOR-04 — Story 3.4 Has an Implicit Ordering Dependency on Story 3.3

Story 3.4 (Email Link Check-in) reuses the `rsv_checkin_tokens` table (Flyway V5) created in Story 3.3 (QR Code Check-in) and the same `PATCH /api/v1/reservations/{id}/check-in` endpoint. This is an intentional design choice (DRY), but the formal AC does not state that Story 3.3 must be completed before Story 3.4 can be implemented.

If a student picks up Story 3.4 without Story 3.3 being merged, they will have no token table and no existing endpoint to reuse.

**Recommendation:** Add a "Depends on: Story 3.3" note to Story 3.4, and ensure sprint planning sequences them in this order.

---

#### MINOR-05 — Story 4.2 Combines Two Concerns for Technician Persona

Story 4.2 addresses both (a) real-time SSE push notification delivery and (b) incident lifecycle status management. These are technically related (both belong to the Technician persona) but differ substantially in implementation: SSE infrastructure is backend-heavy with frontend EventSource subscription, while status management is a standard CRUD lifecycle. Combining them risks the story overflowing its sprint budget for one student.

**Recommendation:** This is acceptable as-is given the academic 4-student constraint (each student owns one block). But the student assigned Epic 4 should be the most technically confident. If sprint velocity is at risk, Story 4.2 can be split: 4.2a (SSE notification) + 4.2b (Status lifecycle) without changing any FR coverage.

---

### Dependency Analysis Summary

**Within-epic story sequencing (all acceptable):**
- Epic 1: 1.1 → 1.2 → 1.3/1.4 (user creation before auth)
- Epic 2: 2.1 → 2.2 (zones before desks) → 2.3 → 2.4 → 2.5
- Epic 3: 3.1 → 3.2 → 3.3 → 3.4 → 3.5
- Epic 4: 4.1 → 4.2 → 4.3 → 4.4 → 4.5 → 4.6

**Cross-epic data dependencies (all handled by mock strategy):**
- Epic 4 stories require reservation data from Epic 3 — handled by OpenAPI mocks in Weeks 2–5 ✅

**No forward dependencies detected across epics.**

---

### Database Migration Timing Validation

| Flyway Migration | Created In | First Used In | Status |
|----|----|----|-----|
| V1__init.sql | Story 0.2 | Story 0.2 | ✅ |
| V2__users.sql | Story 1.1 | Story 1.1 | ✅ |
| V3__assets.sql | Story 2.1 | Story 2.1 | ✅ |
| V4__reservations.sql | Story 3.1 | Story 3.1 | ✅ |
| V5__checkin_tokens.sql | Story 3.3 | Story 3.3 | ✅ |
| V6__incidents.sql | Story 4.1 | Story 4.1 | ✅ |
| **V?__audit_log.sql** | **Not assigned** | **Story 1.4 (Epic 1!)** | **❌ MISSING** |

---

### Epic Quality Summary

- Critical violations: 0
- Major issues: 2 (audit_log migration gap + Story 4.6 breadth)
- Minor concerns: 5
- Stories with clear BDD ACs: 23/23 ✅
- FR traceability: 43/43 ✅
- Stories with user value: 20/23 (Epic 0's 3 stories are intentional technical foundation) ✅

---

## Summary and Recommendations

### Overall Readiness Status

> ## 🟡 NEEDS WORK — Address 2 Major Issues Before Sprint 1 Kickoff

The project is well-planned and remarkably complete for a 4-student academic greenfield project. FR coverage is 100%, UX is documented and well-aligned, epics have clear BDD acceptance criteria, and architectural decisions are coherent. There are **no critical violations**. However, two major issues must be resolved before the first implementation sprint begins, or they will cause integration failures and scope confusion.

---

### Critical Issues Requiring Immediate Action

#### 🟠 ACTION-1 (Before Sprint 1) — Add `anl_audit_log` Flyway Migration

**Why critical:** The `anl_audit_log` table is referenced and written to starting in Story 1.4 (Epic 1), but no Flyway migration creates it. All four blocks write audit events. Without the table, Story 1.4 will throw a runtime `Table 'ecotrack.anl_audit_log' doesn't exist` error at the first integration test.

**Fix (< 30 min):**
1. Add `V7__audit_log.sql` Flyway migration creating `anl_audit_log(id, event_type, entity_type, entity_id, actor, timestamp)`
2. Add this migration to Story 1.4 AC or create a Story 4.0 "Shared Audit Infrastructure" at the top of Epic 4
3. Update the migration numbering table in the epics document

---

#### 🟠 ACTION-2 (Before Sprint 1) — Resolve Mobile Scope Discrepancy

**Why critical:** The PRD defines "fully responsive across desktop, tablet, and mobile" and Guillermo's primary journey shows booking **from his phone on the bus**. The UX spec narrows mobile to "check-in only." If teams implement Story 2.4 (map viewer) and Story 3.1 (reservation) without touch support, Guillermo's journey breaks on mobile.

**Fix (team decision, < 1 hour):**
- Option A: Update UX spec to add mobile touch support for the map + booking bottom sheet (adds ~2-3 story points to Epic 2-3)
- Option B: Update PRD browser support to explicitly exclude mobile booking (booking = desktop only; mobile = check-in + read-only map), and update Guillermo's journey accordingly

Either decision is valid. The risk is leaving it ambiguous.

---

### Recommended Next Steps

1. **Fix Flyway audit_log migration** — Assign to the Epic 4 student (Block 4 owns the `anl_` prefix). Add `V7__audit_log.sql` to Story 1.4 or as a new Story 4.0. Should take 20 minutes. [BLOCKER]

2. **Resolve mobile scope** — 30-minute team meeting to align PRD, UX, and stories. Update ONE of the two documents (PRD or UX spec) to make the mobile scope explicit and consistent. [DECISION REQUIRED]

3. **Add `UPLOAD_DIR` to Story 0.3** — One-line addition to the env setup story AC. The Block 2 and Block 4 students need this in their `.env.example` files from Day 1. [30-MINUTE FIX]

4. **Document Story 3.4 depends on Story 3.3** — Add `Depends on: Story 3.3` to Story 3.4's header. Prevents a student picking it up out of order during sprint 2. [5-MINUTE FIX]

5. **Consider splitting Story 4.6** — If sprint velocity is a concern, split into 4.6a (Admin: reports + retention + audit log) and 4.6b (Employee: GDPR data export). Both remain in Epic 4 and FR coverage is unchanged. [OPTIONAL]

6. **Add algorithm specifics to Story 4.5** — One AC clause defining how the zone ranking works prevents two students from arguing about the "correct" consolidation algorithm at integration time. [30-MINUTE FIX]

---

### Issues Summary

| ID | Severity | Area | Description | Effort to Fix |
|----|----------|------|-------------|--------------|
| MAJOR-01 | 🟠 Major | Epic DB | `anl_audit_log` Flyway migration unassigned | 20 min |
| MAJOR-02 | 🟠 Major | Story Size | Story 4.6 spans 4 FRs + 2 personas | 30 min (split) |
| UX-01 | ⚠️ Medium | UX/PRD Scope | Mobile booking scope misaligned | 1h team decision |
| UX-02 | ⚠️ Low | UX Scope | High-contrast toggle not in any story | 10 min (add/exclude) |
| MINOR-02 | 🟡 Minor | Story 0.3 | `UPLOAD_DIR` missing from env docs | 5 min |
| MINOR-03 | 🟡 Minor | Story 4.5 | Zone algorithm too vague | 30 min |
| MINOR-04 | 🟡 Minor | Story 3.4 | Ordering dependency undocumented | 5 min |
| MINOR-05 | 🟡 Minor | Story 4.2 | Two concerns in one story (acceptable) | Optional split |

**Total: 8 issues | Critical: 0 | Major: 2 | Minor: 6**

---

### Strengths Noted

- **FR coverage: 100%** — All 43 functional requirements have traceable implementation paths
- **Consistent BDD ACs** — All 23 stories use Given/When/Then format with error conditions covered
- **Architecture-UX coherence** — Technology choices (Angular Material, SVG map, SSE, RxJS polling) are consistent across all three planning documents
- **Parallel development strategy** — OpenAPI-first contract + mock data plan enables 4 students to work independently from Week 2
 - **Architecture-UX coherence** — Technology choices (Angular Material, dynamic floor map generation, SSE, RxJS polling) are consistent across all three planning documents
 - **Security design (production planned)** — Production-grade security (bcrypt hashing, JWT-based sessions stored in HttpOnly cookies, RBAC enforcement, input sanitization, MIME validation) is specified for Growth; note that the MVP demo uses simplified Basic Auth and some persistence hardening is deferred

---

### Final Note

This assessment identified **8 issues across 3 categories**. Two require action before Sprint 1 starts (audit log migration and mobile scope decision). The remaining 6 are quick documentation fixes totalling under 2 hours of effort. Once addressed, the project artifacts are implementation-ready.

**Assessment complete.**
**Report:** `_bmad-output/planning-artifacts/implementation-readiness-report-2026-04-24.md`
**Assessor:** GitHub Copilot — Implementation Readiness Skill
**Date:** 2026-04-24
