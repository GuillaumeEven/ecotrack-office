---
type: bmad-distillate
sources:
  - "prd.md"
downstream_consumer: "general"
created: "2026-05-19"
token_estimate: 3420
parts: 1
---

## Project Identity
- EcoTrack Office: smart workspace management for hybrid offices; Angular SPA + Spring Boot REST API + MySQL; greenfield; medium complexity; web_app / general domain
- Author: Sensei; PRD completed 2026-04-10; last edited 2026-05-03; 8-week academic delivery (4 students × 1 CRUD block each)
- Two distinguishing patterns: (1) map-first booking with behavioral nudging (floor plan as primary interaction surface; color-coded overlays make energy-efficient clustering the obvious choice without mandating it); (2) friction-tolerant automation (auto-release, app-based check-in, zone suggestions as recommendations — tool adapts to non-compliance rather than enforcing it)

## Users & Personas
- Employee (Guillermo, 34, R&D): reserves via floor map with filters; happy path; needs ≤60s booking; persistent preferences
- Employee (Eduardo, 41, sales): non-compliant, never confirms reservations; auto-release + email check-in fallback designed for him
- Technician (Raimundo, 52, concierge/maintenance): receives push notifications for incidents; can modify/cancel any reservation; manages incident lifecycle
- Organization Admin (José Luis, 48, head of general services): dashboard, zone consolidation, energy/CO₂ reports, building asset management, user management, tenant settings

## Success Criteria
- Employee: reserve in ≤60s; no occupied-reserved conflicts; auto-release recovers no-shows without staff intervention
- Organization Admin: zero manual ghost-desk recovery; reports without manual extraction; incident exclusion in <2min
- Business: ghost desk rate <5%; zone efficiency ≥80% consolidation on low-attendance days; ≥40% of low-attendance days result in ≥1 zone deactivated
- Technical: 4 CRUD modules delivered + integrated in 8 weeks; session-based auth enforces role separation; 4 functional blocks independently deployable

## Phases
- MVP (8 weeks): user auth + registration with company code; physical asset management; floor map; shift booking; check-in/auto-release; incidents; analytics dashboard; zone consolidation
- Growth (post-MVP): advanced map filters (FR16); email check-in (FR25); pre-release reminder (FR27); zone-shutdown notification to affected employees (FR38); exportable reports (PDF/CSV); recurring reservations; calendar sync; WebSocket real-time map updates
- Vision (future): CO₂ ESG export; native mobile (Android/iOS via Capacitor); HVAC/lighting integration; predictive occupancy modeling; multi-building support

## Development Blocks (4 students × 8 weeks)
- Block 1 (foundational, no dependencies): Users + roles + session authentication
- Block 2 (needs Block 1): Floors + rooms (desk area / meeting room, m²) + desks + SVG floor plan + 80%-capacity-opening policy
- Block 3 (needs Blocks 1+2): Reservations (shift-based morning/afternoon) + check-in + auto-release + remote-work indicator
- Block 4 (needs Block 3): Analytics dashboard + energy/CO₂ savings estimates + incident lifecycle
- Integration milestone: Week 6–7; OpenAPI contract drafted Week 1; Angular Material shared library Week 1; API mocks from Week 2; integration testing starts Week 6

## FR — User Management & Auth
- FR1: register with name + email + password + company invitation code (required; no open self-registration; ties account to an existing organization)
- FR2: login → JWT session token valid 8h
- FR3: logout → invalidate session
- FR4: OrgAdmin CRUD user accounts
- FR5: OrgAdmin assign/change roles (Employee / OrgAdmin / Technician)
- FR6: RBAC enforced at API level — Employee (self-service only), OrgAdmin (full admin), Technician (incidents only)
- FR7: Employee views/updates own profile + saved search preferences

## FR — Physical Assets
- FR8: OrgAdmin CRUD floors; dedicated Create Floor / Create Room / Create Desk admin views
- FR9: OrgAdmin CRUD rooms per floor; types: Desk area or Meeting room; each has energy-management flag + surface area in m² (required for savings calculation)
- FR9b: 80%-capacity-opening policy — new room/floor opens only when current active capacity reaches 80% (configurable threshold); Technician can manually open/close any room at any time
- FR10: OrgAdmin CRUD desks with equipment attributes (chairs, tables, TV, etc.) + floor plan position
- FR11: Floor map for each floor dynamically rendered by frontend from stored floor/room/desk structure data; positions derived from creation order; no SVG file upload or storage in backend

## FR — Interactive Floor Map
- FR13: Employee views building as interactive map with rectangular tiles (floors/rooms); unavailable rooms (incident, closed) visually distinct and non-interactive
- FR14: real-time color-coded status per resource (available / reserved / unavailable/incident)
- FR15: room tile click → room-level view with individual desks; tooltip on each desk → shift booking (morning/afternoon) in single click; room drill-down includes desk detail
- FR16: (nice-to-have post-MVP) filter map by proximity to restrooms, window-facing, equipment type
- FR17: list/table fallback view always available (also serves low-bandwidth)
- FR18: detail card for any desk/room (equipment, capacity, zone, current status)

## FR — Reservations
- FR19: reserve desk or meeting room for date + shift (morning 08:00–14:00 / afternoon 14:00–20:00); shift granularity only (no arbitrary hours)
- FR19b: 7-day advance booking limit (configurable by OrgAdmin; provisional default)
- FR20: double-booking prevention for same resource + same shift
- FR21: Employee views upcoming + past reservations
- FR22: Employee cancels own reservation before start time
- FR22b: Technician modifies/cancels any reservation to resolve conflicts; no auto-notification to affected employees in demo scope
- FR23: system saves last-used search criteria; pre-fills on next visit
- FR23b: Employee marks workday as remote; recorded + included in CO₂ savings estimates (FR40c)

## FR — Check-in & Auto-release
- FR24: check-in from within app on active reservation (no QR code, no external link required); single action
- FR25: (nice-to-have post-MVP) email-link check-in without active session
- FR26: auto-release if check-in not confirmed within 15min of start (timeout configurable by OrgAdmin)
- FR27: (nice-to-have post-MVP) reminder to Employee 10min before auto-release deadline
- FR28: released desk immediately available for new bookings in real time

## FR — Incident Management
- FR29: any authenticated user can report incident on a resource (text description + optional photo)
- FR30: incident submission → resource automatically marked unavailable; removed from booking availability
- FR31: push notification delivered to all Technicians within 30 seconds of new incident submission (location + description)
- FR32: Technician updates incident status (open → in progress → resolved)
- FR33: resolved incident → resource automatically re-enabled for booking
- FR34: OrgAdmin views all incidents with status, resolution time, technician assignment

## FR — Analytics & Occupancy
- FR35: OrgAdmin views total reservations per zone (current day + current week)
- FR36: OrgAdmin views occupancy rate (confirmed check-ins vs. total reservations) per zone per day
- FR37: zone consolidation suggestion when daily attendance below configurable threshold; identifies zones to activate and zones to shut down
- FR38: (nice-to-have post-MVP) OrgAdmin sends targeted notification to Employees booked in zone recommended for shutdown
- FR39: OrgAdmin exports occupancy summary report for selected date range as CSV
- FR40b: energy savings estimate per closed room; formula: `savings = room_m2 × cost_per_m2_per_day × closure_days`; cost-per-m² set by OrgAdmin at onboarding (reference: ~8,200 €/year/room); building structure (floors, room m²) collected at setup
- FR40c: CO₂ savings estimate from (a) closed rooms (energy saved) + (b) employee remote-work days (FR23b); CO₂ factors are configurable constants

## FR — Data & Privacy
- FR40: privacy notice + explicit consent at registration (GDPR EU 2016/679)
- FR41: Employee requests personal data export (reservations, profile)
- FR42: OrgAdmin configures data retention period for reservation history (default 12 months; then anonymized/deleted)
- FR43: audit log of all reservation create/update/cancel + incident state-change events; OrgAdmin accessible; minimum retention 90 days

## Non-Functional Requirements
- Performance: initial load ≤3s on 50 Mbps; core API responses ≤500ms p95; availability polling every 30s; floor plan assets lazy-loaded per floor navigated; async photo upload (UI confirms immediately)
- Security: HTTPS/TLS 1.2+; passwords stored in plain text for MVP demo scope only (bcrypt min cost factor 12 deferred to Growth); all API endpoints require auth except login/registration; RBAC enforced at API layer (not only frontend); server-side MIME validation for photos (JPEG/PNG/WebP, ≤5MB); all user text sanitized server-side; JWT token expiry + refresh strategy + HttpOnly/Secure cookie storage deferred to Growth
- Scalability: ≤500 concurrent users without degradation; DB schema supports multiple buildings from day 1; no hardcoded limits on floors/zones/desks
- Reliability: 99% uptime 07:00–20:00 Mon–Fri local time; graceful degradation (last-known map + "live data unavailable" banner; booking actions disabled rather than silently failing); auto-release scheduler recovers from DB state on server restart (not memory-only)
- Maintainability: OpenAPI/Swagger spec = binding frontend–backend contract; 4 blocks independently deployable with own DB schema prefix; all config via env vars (no hardcoded values)
- Accessibility: WCAG AA color contrast (4.5:1 min) via Angular Material theme; keyboard-navigable primary actions; list/table fallback for map; aria-label on all icons/status indicators; focus returned programmatically after modals/navigation

## Technical Architecture
- Stack: Angular SPA + Spring Boot REST API + MySQL
- Map rendering: floor map SVG dynamically generated by Angular from stored floor/room/desk structure data (creation-order layout); desk status overlaid as Angular components — no third-party mapping library; no SVG file storage in backend
- State management: Angular services + RxJS observables; no NgRx required at MVP scale
- Auth: HTTP Basic Authentication for MVP demo (plain credentials); Angular route guards enforce role access; JWT with HttpOnly/Secure cookie storage deferred to Growth
- API: Angular HttpClient + interceptors for token injection + global error handling; OpenAPI/Swagger as frontend–backend contract
- Browser support (MVP): Chrome/Firefox/Safari/Edge latest 2 stable versions each; fully responsive (desktop/tablet/mobile); no app store distribution
- Vision: Android/iOS via Angular + Capacitor/Ionic (post-MVP, subject to team capacity after MVP delivery)
- No third-party system integrations at MVP; REST API JSON designed for future calendar/HVAC integration; email via SMTP (provider configurable via env var)

## Compliance & Domain Constraints
- GDPR (EU 2016/679): PII collected (name, presence history, booking patterns, zone location); explicit consent at registration; right to access + erasure; data minimization; privacy policy at onboarding
- Data retention: reservation history + occupancy logs max 12 months then anonymized/deleted; configurable by OrgAdmin
- Audit trail: all reservation create/update/cancel + incident state changes logged (timestamp + user ID); OrgAdmin accessible; min 90 days retention
- Hardware control (HVAC/lighting) scoped to Vision tier; MVP API read-only toward any future BAS integration

## SaaS Demo Mode
- No real payment processing; billing/invoicing/tax handling deferred to future release
- Pricing/Plans view removed from public navigation; accessible only from OrgAdmin account settings
- "Subscribe" → demo signup only (not live billing); visible "Demo environment — no live payments" banner throughout app and export files
- Demo tenants seeded manually or via admin console; logical data segmentation (not hardened multi-tenant isolation)
- Signup flow: create org/account → accept demo terms → optional sample data import → immediate trial activation
- OrgAdmin role = combined facilities + tenant admin (manages assets, users, tenant settings, demo plan)
- Telemetry: usage recorded for product decisions; not used for billing/chargeback in demo

## Key Decisions (edit history through 2026-05-19)
- Round 1 integrated: company-code registration (FR1); shift-based booking morning/afternoon (FR19); 7-day advance limit (FR19b); technician modifies/cancels reservations (FR22b); remote-work day indicator (FR23b); 80% capacity-opening policy (FR9b); admin UI for floors/rooms/desks (FR8 updated); map with rectangular tiles + shift tooltip (FR13/FR15); energy savings formula per m² (FR40b); CO₂ estimate from remote work (FR40c); pricing/plans removed from public nav
- Round 2 scope corrections: FR15 heat overlay removed (not discussed); FR15b merged into FR15 (room drill-down); FR16 map filters → nice-to-have post-MVP; FR22b no user notification in demo; FR24 app-based check-in (no QR); FR25 email check-in → nice-to-have; FR27 pre-release reminder → nice-to-have; FR38 zone shutdown notification → nice-to-have
- Round 3 demo scope: JWT removed from MVP; auth simplified to HTTP Basic Auth (plain credentials) for demo exercise; bcrypt + JWT + HttpOnly cookies deferred to Growth; FR11 replaced (SVG upload removed → floor map dynamically generated by Angular from stored structure data, creation-order positions); FR12 removed (manual SVG anchor association no longer needed)

## Risks & Mitigations
- Block 2 SVG/map complexity: Week 1 spike to validate room-tile + desk drill-down; list/table fallback always available
- Integration (Week 6–7): shared component library from Week 1; API mocks from Week 2
- Block 4 scope creep: hard limit = energy/CO₂ savings per closed room + occupancy view + incident list; zone-shutdown notification + exportable report → Growth
- Ghost desks despite auto-release: configurable timeout (default 15min); reminder at T−10min
- Personal data in reports: occupancy reports aggregate by zone only; individual data visible to user + OrgAdmin only
- App unavailability on arrival: email-link check-in fallback (post-MVP, FR25)
