---
stepsCompleted: ['step-01-init', 'step-02-discovery', 'step-02b-vision', 'step-02c-executive-summary', 'step-03-success', 'step-04-journeys', 'step-05-domain', 'step-06-innovation', 'step-07-project-type', 'step-08-scoping', 'step-09-functional', 'step-10-nonfunctional', 'step-11-polish', 'step-12-complete']
workflowStatus: complete
completedAt: '2026-04-10'
inputDocuments: ['project/initial_suggestion.md']
workflowType: 'prd'
briefCount: 0
researchCount: 0
brainstormingCount: 0
projectDocsCount: 1
classification:
  projectType: web_app
  domain: building_automation
  complexity: medium
  projectContext: greenfield
---

# Product Requirements Document — EcoTrack Office

**Author:** Sensei
**Date:** 2026-04-10
**Project Type:** Web Application (Angular SPA + Spring Boot REST API)
**Domain:** Smart Building / Workspace Management | Complexity: Medium | Context: Greenfield

---

## Executive Summary

EcoTrack Office is a smart workspace management platform for large office buildings navigating the shift to hybrid and flexible work. Unpredictable attendance has made space utilization chaotic: employees cannot easily find available workspaces in multi-floor buildings, while facilities managers battle daily energy waste from ghost reservations and underused zones.

EcoTrack resolves both problems simultaneously. Employees get real-time visual clarity — an interactive floor map showing exactly what is available, where, and with what equipment. Facilities managers get automated enforcement — check-in timeouts release abandoned desks automatically, zone consolidation suggestions eliminate manual coordination, and occupancy dashboards replace spreadsheet-based guesswork.

### What Makes This Special

Two capabilities distinguish EcoTrack from standard booking tools:

**Map-first booking with behavioral nudging.** The building floor plan is the primary interaction surface. Color-coded zone overlays nudge employees toward energy-efficient clustering — desks near already-occupied zones are highlighted, making the sustainable choice the obvious choice without mandating it.

**Friction-tolerant automation.** The system is designed for users who will not comply with procedures. Non-compliance is made harmless: missed check-ins trigger auto-release, check-in works via email link with no session required, and zone suggestions are recommendations rather than enforced rules. The tool adapts to human behavior rather than demanding humans adapt to the tool.

---

## Success Criteria

### User Success

**Employee:**
- Finds and reserves an available workspace in ≤ 60 seconds from any device
- Never arrives to find a reserved desk already occupied
- Recovers an available desk automatically when a no-show occurs — without contacting the facilities team
- Understands zone activity at a glance via color-coded floor map

**Facilities Manager:**
- Zero manual intervention for ghost-desk recovery (handled automatically by check-in timeout)
- Occupancy and energy-zone reports available without manual data extraction
- Faulty resources excluded from booking in under 2 minutes via incident workflow

### Business Success

- Energy waste reduced by concentrating reservations into active zones — measurable via per-zone occupancy data in the analytics dashboard
- Facilities management overhead (manual cancellations, resource tracking) reduced to near zero through automation
- Platform architecture ready for ESG reporting extension (CO₂ savings data) as a Growth milestone

### Technical Success

- All four functional blocks independently deployable and testable
- Each student delivers one complete CRUD module with working API and UI
- Full-stack integration between Angular SPA and Spring Boot API operational within 8 weeks
- JWT-based authentication enforces role separation across all features

### Measurable Outcomes

| Metric | Target |
|---|---|
| Ghost desk rate | < 5% of reservations result in unclaimed desks without auto-release |
| Booking time | Employee completes reservation in ≤ 60 seconds end-to-end |
| Zone efficiency | ≥ 80% of active occupants consolidated into fewest zones on low-attendance days |
| Academic delivery | 4 CRUD modules delivered and integrated within 8 weeks |

---

## User Journeys

### Journey 1 — Guillermo: The Flexible Employee (Primary User – Happy Path)

**Persona:** Guillermo, 34, R&D engineer. Works three days a week in the office on a variable schedule. Two non-negotiable criteria: proximity to restrooms (knee tendinitis) and a south-facing window with a view of the Sierra Nevada.

**Opening Scene:** 7:42 AM. Guillermo is on the bus and decides to come in today. He opens EcoTrack on his phone — the building has four floors and dozens of workstations, and he has no idea what he will find.

**Rising Action:** He filters the floor map: *near restrooms* + *south-facing window*. Green spots match his criteria; orange spots mark zones where colleagues are already concentrated (energy bonus). He spots a third-floor desk by the window, fifteen meters from the restrooms. Two taps to book.

**Climax:** At 9:05 AM he arrives and scans the desk QR code to confirm check-in. The view is there. He has spoken to no one and sent no emails.

**Resolution:** Guillermo comes when he wants, on his own terms. Tomorrow his usual search will be pre-filled.

**Capabilities revealed:** Advanced map filters, interactive floor map with criteria matching, QR check-in, persistent user preferences.

---

### Journey 2 — Eduardo: The Reluctant User (Primary User – Edge Case)

**Persona:** Eduardo, 41, sales representative. Efficient, overloaded, and allergic to procedures. He booked a desk because José Luis asked him to but no longer remembers the tool's name — let alone how to log in.

**Opening Scene:** Tuesday, 8:50 AM. Eduardo walks straight into the office. He never confirmed his 8:00 AM reservation. At 8:15 AM the system released the desk automatically and offered it to someone else.

**Rising Action:** Eduardo looks for a free spot. The map shows available desks in real time — three are free in the same zone. He settles at one and checks in via the link received by email: no app, no memorized password.

**Climax:** No conflict. No "this desk is reserved." José Luis did not intervene. The system handled the disruption without friction.

**Resolution:** Eduardo does not change his habits — and that is fine. EcoTrack was built for people like him.

**Capabilities revealed:** Auto-release timeout (15 min), lightweight check-in via email link, real-time map updates, reservation conflict management.

---

### Journey 3 — Raimundo: The Concierge on the Ground (Operations User)

**Persona:** Raimundo, 52, building concierge and maintenance technician. He learns about failures by word of mouth, or by stumbling upon them during an unrelated task. Reactive but never informed.

**Opening Scene:** 10:23 AM. The projector in Meeting Room B has had a distorted image since morning. Three people noticed it; two complained to each other. Nobody reported anything.

**Rising Action:** Clara, booked in Room B at 11:00 AM, opens EcoTrack before heading over and taps "Report an issue." She describes the problem and attaches a photo. Submitted in under thirty seconds.

**Climax:** Raimundo receives an instant push notification with description, photo, and exact location. Room B is automatically flagged "incident in progress" — invisible to new bookings. He fixes it before Clara arrives.

**Resolution:** Clara finds the room working. Raimundo lost no time searching. José Luis sees the incident resolved in 38 minutes on his dashboard.

**Capabilities revealed:** In-app incident reporting with photo, real-time push notification to technician, automatic resource blocking, resolution tracking, incident metrics for manager.

---

### Journey 4 — José Luis: The Worn-Down Facilities Manager (Admin User)

**Persona:** José Luis, 48, head of general services. Twelve years managing the building. His spreadsheet-based reservation system is universally described as "badly designed" — in practice, nobody fills it in. His day starts with a walk-through to see who is in and who has taken which desk.

**Opening Scene:** Monday, 8:00 AM. José Luis opens his EcoTrack dashboard: 23 confirmed reservations for today, 2 open incidents, and an automatic suggestion: *"Low attendance forecast — activate zones A and C only, shut down zone B."*

**Rising Action:** He approves the zone consolidation with one click. The system notifies the 4 people in zone B, suggesting they move. Three accept; one replies "I'll stay" — José Luis sees the update in real time.

**Climax:** At 5:30 PM he generates the weekly report — occupancy by zone, energy savings estimate, incidents resolved — and sends the PDF to management in two clicks.

**Resolution:** For the first time in years, José Luis does not spend his morning chasing people. The tool gives him visibility without demanding compliance.

**Capabilities revealed:** Real-time admin dashboard, zone consolidation with automated suggestions, targeted employee notifications, exportable weekly reports, multi-incident management.

---

### Journey Requirements Summary

| Journey | Capabilities Required |
|---|---|
| Guillermo | Filtered interactive map, criteria-based search, QR check-in, persistent preferences |
| Eduardo | Auto-release timeout, lightweight check-in (email link), real-time conflict handling |
| Raimundo | In-app incident reporting with photo, push notification to technician, auto resource block |
| José Luis | Admin dashboard, zone consolidation suggestions, targeted notifications, exportable reports |

---

## Domain-Specific Requirements

EcoTrack operates as a business-logic and analytics layer over a managed office building. It does not interface with physical control systems (HVAC, lighting hardware) in MVP scope. Domain constraints are lightweight but non-trivial.

### Compliance & Regulatory

- **GDPR (EU 2016/679):** Platform collects PII (name, presence history, booking patterns, in-building location by zone). Requirements: explicit consent on registration, right to access and erasure, data minimization, privacy policy surfaced at onboarding.
- **Data retention:** Reservation history and occupancy logs retained for a maximum of 12 months, then anonymized or deleted. Configurable by Facilities Manager.
- **Role-based access control:** Three permission levels enforced at API level — Employee (self-service booking), Facilities Manager (full admin + reports), Technician (incident management only). No role escalation without admin action.

### Technical Constraints

- **Authentication:** JWT stateless auth; tokens expire after 8 hours; refresh token strategy required for continuous sessions.
- **Audit trail:** All reservation create/update/cancel events and incident state changes logged with timestamp and user ID; accessible to Facilities Manager; minimum retention 90 days.
- **Input validation:** All user-submitted text sanitized server-side. Incident photos: images only (JPEG, PNG, WebP), ≤ 5MB, MIME-type validated server-side.

### Integration Requirements

- No third-party system integrations at MVP scope.
- REST API with JSON payloads designed for future integration with calendar systems or HVAC platforms.
- Email delivery (reminders, incident notifications) via SMTP; provider configurable via environment variable.

### Risk Mitigations

| Risk | Mitigation |
|---|---|
| Ghost desks despite auto-release | Configurable timeout (default 15 min); reminder sent at T−10 min before release |
| Personal data exposure in reports | Occupancy reports aggregate by zone only; individual data visible only to the user and Facilities Manager |
| App unavailability on arrival | Email-link check-in fallback requires no active app session |
| Scope creep toward hardware integration | Hardware control scoped to Vision tier; MVP API layer is read-only toward any future BAS integration |

---

## Innovation & Novel Patterns

### Detected Innovation Areas

**1. Map-First Booking with Behavioral Nudging**
EcoTrack replaces the conventional list/calendar booking paradigm with the building floor plan as the primary interaction surface. Color-coded zone heat overlays nudge employees toward energy-efficient clustering — making the sustainable choice the obvious choice without mandating it.

**2. Friction-Tolerant Automation Design**
The system is designed for users who will not fully comply with procedures. Non-compliance is made harmless: auto-release handles missed check-ins, email-link check-in requires no session, and zone suggestions are recommendations. This inverts the traditional facilities management model — the tool adapts to human behavior rather than demanding compliance.

### Validation Approach

| Signal | Target |
|---|---|
| Map-first UX | ≤ 60 seconds to complete booking; zero support requests in first week |
| Nudge effectiveness | ≥ 40% of low-attendance days result in at least one zone deactivated |
| Friction tolerance | < 5% ghost desk rate after auto-release is active |

### Innovation Risk Mitigations

| Risk | Mitigation |
|---|---|
| Map UI unfamiliar to non-technical users | Onboarding tooltip overlay on first login; list-view fallback always available |
| Nudge perceived as surveillance | Visual only, never blocking; privacy policy states no individual tracking in reports |
| Auto-release disrupts late arrivals | Configurable timeout; pre-release reminder at T−10 min via email |

---

## Web Application Specific Requirements

### Architecture Overview

Angular SPA communicating with Spring Boot REST API. Internal tool used across desktop and mobile (employees book in transit — see Guillermo journey). No SEO required. Primary challenge: live interactive floor map with real-time availability at fast load times across all browsers.

### Browser & Platform Support

**MVP — Responsive Web:** Chrome, Firefox, Safari, Edge (latest two stable versions each); fully responsive across desktop, tablet, and mobile; no app store distribution.

**Vision — Native Mobile App:** Android/iOS via Angular + Capacitor/Ionic, subject to team capacity after MVP delivery; not in 8-week scope.

### Performance

- Initial load (shell + active floor map): ≤ 3 seconds on 50 Mbps connection
- Core API responses (availability check, reservation create/cancel, check-in): ≤ 500ms at p95
- Lazy loading per Angular feature module; floor plan assets loaded on-demand per floor navigated
- Desk availability polling every 30 seconds (MVP); WebSocket upgrade candidate for Growth tier
- Incident photo uploads processed asynchronously; UI confirms submission immediately

### Accessibility

No formal WCAG certification required. Documented baseline practices:

- WCAG AA color contrast (4.5:1 minimum) enforced via Angular Material theme
- All primary actions keyboard-navigable
- List/table fallback view for map (also serves low-bandwidth scenarios)
- `aria-label` on all icons and status indicators
- Focus returned programmatically after modal dialogs and navigation events

### Technical Architecture

- **Map rendering:** SVG floor plan (uploaded by admin); desk status overlaid as Angular components on SVG anchors — no third-party mapping library dependency
- **State management:** Angular services + RxJS observables; no NgRx required at MVP scale
- **Auth flow:** JWT in `HttpOnly`, `Secure`, `SameSite=Strict` cookies; Angular route guards enforce role access
- **API communication:** Angular `HttpClient` with interceptors for token injection and global error handling; OpenAPI/Swagger spec maintained as the frontend–backend contract

### Implementation Constraints

- 4 students × 8 weeks; each owns one Angular feature module + Spring Boot controller/service/repository + MySQL schema (one full CRUD vertical)
- Angular Material shared component library established in Week 1
- All configuration via environment variables — no hardcoded values

---

## Project Scoping & Phased Development

### MVP Strategy

**Approach:** Experience MVP — four independent but integrated functional blocks, each independently evaluable and contributing to a unified product. Matches the academic constraint (one CRUD per student) and the four user value areas.

**Parallel Development:** Block dependencies managed via OpenAPI/Swagger contract drafted in Week 1. Each student develops against mock data until integration (Week 6–7).

### Development Blocks & CRUD Ownership

| Block | Domain | Core CRUD | Dependencies |
|---|---|---|---|
| **Block 1** | Users & Security | User accounts, roles, JWT authentication | None — foundational |
| **Block 2** | Physical Assets | Floors, zones, desks, rooms, SVG floor plan | Block 1 (auth) |
| **Block 3** | Reservations & Logic | Bookings, check-in, auto-release, schedules | Blocks 1 + 2 |
| **Block 4** | Analytics & Incidents | Dashboard, occupancy reports, incident lifecycle | Block 3 |

### Delivery Risks

| Risk | Mitigation |
|---|---|
| Block 2 SVG complexity | Week 1 spike to validate SVG-as-Angular-component; grid/table fallback if needed; nudge overlay delivered incrementally |
| Integration week (6–7) | Shared component library from Week 1; API mocks from Week 2; integration testing starts Week 6 |
| Block 4 scope creep | Hard limit: occupancy-per-zone view + incident list + one exportable report; CO₂ estimation is Growth, not MVP |

### Phase 1 — MVP (8-Week Delivery)

All four user journeys (Guillermo, Eduardo, Raimundo, José Luis) supported at happy-path level.

1. User registration, login, and role management (Employee / Facilities Manager / Technician)
2. Physical asset management: floors, zones, desks, meeting rooms, equipment attributes
3. Interactive SVG floor map with real-time color-coded availability and zone heat overlay
4. Desk/room reservation with criteria-based filtering
5. Check-in via QR code and email link fallback, with 15-minute auto-release
6. Incident reporting with photo, automatic resource blocking, technician push notification
7. Occupancy dashboard: reservations and check-in rates per zone, daily/weekly view
8. Zone consolidation suggestion engine for low-attendance days

### Phase 2 — Growth (Post-MVP)

- Recurring reservations and calendar sync (Google Calendar, Outlook)
- Email/push notification system for reminders and desk-release warnings
- Advanced map filters (equipment type, proximity, floor)
- Exportable weekly reports (PDF/CSV)
- WebSocket-based real-time map updates

### Phase 3 — Vision (Future)

- CO₂ savings dashboard with ESG-ready data export
- Native mobile app (Android/iOS via Capacitor)
- Real HVAC/lighting system integration
- Predictive occupancy modeling
- Multi-building support

---

## Functional Requirements

### User Management & Authentication

- **FR1:** A visitor can register an account with name, email, and password
- **FR2:** A registered user can log in and receive a session token valid for 8 hours
- **FR3:** An authenticated user can log out and invalidate their session
- **FR4:** A Facilities Manager can create, update, deactivate, and delete user accounts
- **FR5:** A Facilities Manager can assign and change roles (Employee, Facilities Manager, Technician) for any user
- **FR6:** The system enforces role-based access at API level: Employees (self-service only), Facilities Managers (full admin), Technicians (incident management only)
- **FR7:** An Employee can view and update their own profile and saved search preferences

### Physical Asset Management

- **FR8:** A Facilities Manager can create, update, and deactivate floors within the building
- **FR9:** A Facilities Manager can create, update, and deactivate zones within a floor, each with an energy-management flag
- **FR10:** A Facilities Manager can create, update, and deactivate individual desks and meeting rooms with attributes (equipment list, capacity, floor plan position)
- **FR11:** A Facilities Manager can upload and replace the SVG floor plan for any floor
- **FR12:** A Facilities Manager can associate desks and rooms with their SVG anchor positions on the floor plan

### Interactive Floor Map

- **FR13:** An Employee can view any floor as an interactive SVG map
- **FR14:** The map displays each resource with a real-time color-coded status (available, reserved, unavailable/incident)
- **FR15:** The map displays a zone heat overlay showing zones with active reservations to nudge energy-efficient clustering
- **FR16:** An Employee can filter the map by criteria (proximity to restrooms, window-facing, equipment type)
- **FR17:** An Employee can access a list/table view of available spaces as an alternative to the map
- **FR18:** An Employee can view a detail card for any desk or room (equipment, capacity, zone, current status)

### Reservation & Booking

- **FR19:** An Employee can reserve an available desk or meeting room for a specific date and time slot
- **FR20:** The system prevents double-booking: a reserved resource cannot be booked by another user for the same slot
- **FR21:** An Employee can view their upcoming and past reservations
- **FR22:** An Employee can cancel their own reservation before the reservation start time
- **FR23:** The system saves an Employee's last-used search criteria and pre-fills them on subsequent visits

### Check-in & Auto-release

- **FR24:** An Employee can confirm check-in by scanning a QR code displayed at the desk
- **FR25:** An Employee can confirm check-in via a unique email link, without requiring an active app session
- **FR26:** The system automatically releases a reservation if check-in is not confirmed within 15 minutes of start time (timeout configurable by Facilities Manager)
- **FR27:** The system sends a reminder to the Employee 10 minutes before the auto-release deadline
- **FR28:** A released desk becomes immediately available for new bookings in real time

### Incident Management

- **FR29:** Any authenticated user can report an incident on a specific resource with a text description and optional photo
- **FR30:** Upon incident submission, the system automatically marks the resource as unavailable and removes it from booking availability
- **FR31:** The system sends an immediate push notification to all Technicians when a new incident is submitted, with location and description
- **FR32:** A Technician can update incident status (open → in progress → resolved)
- **FR33:** When a Technician marks an incident resolved, the resource is automatically re-enabled for booking
- **FR34:** A Facilities Manager can view all incidents with status, resolution time, and technician assignment

### Analytics & Occupancy Dashboard

- **FR35:** A Facilities Manager can view total reservations per zone for the current day and current week
- **FR36:** A Facilities Manager can view occupancy rate (confirmed check-ins vs. total reservations) per zone per day
- **FR37:** The system generates a zone consolidation suggestion when daily attendance falls below a configurable threshold, identifying zones to activate and zones to shut down
- **FR38:** A Facilities Manager can send a targeted notification to Employees booked in a zone recommended for shutdown
- **FR39:** A Facilities Manager can export an occupancy summary report for a selected date range

### Data & Privacy

- **FR40:** The system displays a privacy notice and requests explicit consent during registration
- **FR41:** An Employee can request the export of their personal data (reservations, profile)
- **FR42:** A Facilities Manager can configure the data retention period for reservation history (default: 12 months)
- **FR43:** The system maintains an audit log of all reservation and incident state-change events, accessible to the Facilities Manager

---

## Non-Functional Requirements

### Performance

- Initial load (application shell + active floor map): ≤ 3 seconds on a 50 Mbps connection
- Core API responses (availability check, reservation create/cancel, check-in): ≤ 500ms at p95
- Floor plan assets and desk data loaded per active floor only (lazy loading); no full pre-load on startup
- Availability polling: every 30 seconds; stale data indicated clearly if refresh fails
- Incident photo uploads: processed asynchronously; UI confirms submission immediately without blocking

### Security

- All data in transit encrypted via HTTPS/TLS 1.2+
- Passwords stored with bcrypt (minimum cost factor 12); never logged or returned in API responses
- JWT tokens expire after 8 hours; refresh token strategy required for continuous sessions
- JWT stored in `HttpOnly`, `Secure`, `SameSite=Strict` cookies
- All API endpoints require valid authentication except login, registration, and email check-in link
- Role enforcement applied at API layer (Spring Security), not only in the Angular frontend
- Incident photo uploads: server-side MIME-type validation, 5MB maximum
- All user-submitted text sanitized server-side to prevent injection attacks

### Scalability

- System supports up to 500 concurrent authenticated users without degradation beyond stated performance targets
- Database schema supports multiple buildings (multi-floor, multi-zone) from day one
- No hardcoded limits on floors, zones, or desks per building

### Reliability

- Availability target: 99% during office hours (07:00–20:00 local time, Monday–Friday)
- Graceful degradation: if the API is unreachable, the SPA displays the last-known floor map with a "live data unavailable" banner; booking actions disabled rather than silently failing
- Auto-release scheduler recovers pending jobs from database state on server restart — jobs are not held in memory only

### Maintainability

- OpenAPI/Swagger specification maintained in sync with Spring Boot controllers; serves as the binding frontend–backend contract
- Each of the four functional blocks deployable independently with its own database schema prefix
- All environment-specific values provided via environment variables — no hardcoded configuration
