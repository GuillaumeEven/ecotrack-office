# Intern checklist for the Ecotrack Office development workflow

## Done

- [x] Idea suggestions / brainstorming for our TFM (Final Master's Project) | *before 19 March 2026*
    > #### Submitted ideas
    > - Smart Menu: suggest and organize weekly menus according to the season.
    > - Smart Shopping: compare everyday product prices, contributed by users and localized.
    > - Smart Hiking: social platform to share routes, points of interest and GPS tracks among hikers.
    > - ...

- [x] Selected project: Ecotrack Office | *19 March 2026*
- [x] PRD generated using the BMAD method | *10 April 2026*
- [x] Architecture generated using the BMAD method | *17 April 2026*
- [x] Epics & Stories generated using the BMAD method | *April 2026*
    > 5 epics, 23 user stories with full acceptance criteria, mapped to all 43 FRs.
    > → [`_bmad-output/planning-artifacts/epics.md`](_bmad-output/planning-artifacts/epics.md)

- [x] UX Design Specification generated using the BMAD method | *20 April 2026*
    > 14-section spec: design system (Angular Material M3, Teal #00897B), component strategy (5 custom components),
    > user journey flows (5 Mermaid diagrams), visual foundation, UX patterns, accessibility (WCAG 2.2 AA).
    > → [`_bmad-output/planning-artifacts/ux-design-specification.md`](_bmad-output/planning-artifacts/ux-design-specification.md)
    > → [`_bmad-output/planning-artifacts/ux-design-directions.html`](_bmad-output/planning-artifacts/ux-design-directions.html) *(interactive mockups)*

- [x] Sprint planning generated | *20 April 2026*
    > 9 sprints, 23 stories organized by technical dependencies.
    > → [`_bmad-output/planning-artifacts/sprint-calendar.md`](_bmad-output/planning-artifacts/sprint-calendar.md)
    > → [`_bmad-output/implementation-artifacts/sprint-status.yaml`](_bmad-output/implementation-artifacts/sprint-status.yaml)

## Work Calendar — Tasks per Student

The team works in **4 parallel verticals** after Sprint 1. Sprints 6, 7, and 8 can overlap once Sprint 5 (Floor Map) is complete.

> 🔴 = Large story · 🟡 = Medium · 🟢 = Small · ⚡ = Critical path blocker

### All students — Sprint 1: Foundation

| Story | Title |
|-------|-------|
| 0.1 | Initialize Angular Frontend Workspace 🟡 |
| 0.2 | Initialize Spring Boot Backend 🟡 |
| 0.3 | Local Development Environment Setup 🟢 |

---

### Student 1 — Block 1: Users & Authentication

| Sprint | Story | Title | Note |
|--------|-------|-------|------|
| 2 | 1.1 | User Registration with GDPR Consent 🟡 | — |
| 2 | 1.2 | User Login and Secure Session Management 🟡 | ⚡ unblocks all other students |
| 3 | 1.3 | Employee Profile & Saved Search Preferences 🟢 | — |
| 3 | 1.4 | Facilities Manager — User Administration 🟡 | — |

---

### Student 2 — Block 2: Physical Assets & Floor Map

| Sprint | Story | Title | Note |
|--------|-------|-------|------|
| 3 | 2.1 | Floor & Zone Management 🟡 | — |
| 4 | 2.2 | Desk & Meeting Room Management 🟡 | — |
| 4 | 2.3 | SVG Floor Plan Upload & Anchor Association 🔴 | — |
| 5 | 2.4 | Interactive Floor Map Viewer 🔴 | ⚡ unblocks S3 and S4 |
| 5 | 2.5 | Zone Heat Overlay, Map Filters & List View 🔴 | — |

---

### Student 3 — Block 3: Reservations, Check-in & Auto-release

| Sprint | Story | Title | Note |
|--------|-------|-------|------|
| 6 | 3.1 | Desk/Room Reservation & Conflict Prevention 🔴 | starts after 2.4 ✅ |
| 6 | 3.2 | My Reservations & Cancellation 🟡 | — |
| 7 | 3.3 | QR Code Generation & Desk Check-in 🟡 | — |
| 7 | 3.4 | Email Link Check-in & Pre-release Reminder 🟡 | — |
| 7 | 3.5 | Auto-release Scheduler 🟡 | — |

---

### Student 4 — Block 4: Analytics, Dashboard & Incidents

| Sprint | Story | Title | Note |
|--------|-------|-------|------|
| 8 | 4.1 | Incident Reporting & Automatic Resource Blocking 🟡 | starts after 2.4 ✅ |
| 8 | 4.2 | Real-time Technician Notification & Incident Lifecycle 🟡 | — |
| 8 | 4.3 | Facilities Manager — Incident Overview 🟡 | — |
| 9 | 4.4 | Occupancy Dashboard 🔴 | starts after 3.1 + 4.3 ✅ |
| 9 | 4.5 | Zone Consolidation Suggestions & Targeted Notifications 🔴 | — |
| 9 | 4.6 | Reporting, Data Export & GDPR Governance 🟡 | — |

---

### Key dependency: Sprint 5 is the critical blocker

```
Sprint 1 (all) → Sprint 2 (S1) → Sprint 3 (S1 + S2)
                                       ↓
                               Sprint 4 (S2)
                                       ↓
                               Sprint 5 (S2) ← CRITICAL
                                       ↓
                 Sprint 6 (S3) + Sprint 8 (S4)   ← parallel
                       ↓                  ↓
                 Sprint 7 (S3)      Sprint 9 (S4) ← parallel
```

While S2 is finishing the floor map (Sprint 5), S3 and S4 should be writing backend logic and tests for their upcoming stories.

## To do

- [ ] Create story files and start implementation
    <details>
    <summary>Next step: <code>bmad-create-story</code></summary>

    **Objective:** Generate a detailed, self-contained story spec file for one story at a time, ready for student implementation.

    Each story file contains:
    - Full context (epics, architecture, UX decisions)
    - Precise acceptance criteria (already written in epics.md)
    - Technical implementation notes (Angular component, Spring Boot endpoint, DB schema change)
    - Test scenarios

    **Workflow:**
    1. Run `skill:bmad-create-story` → select the next `backlog` story from `sprint-status.yaml` (start with **Story 0.1**)
    2. The story file is created in `_bmad-output/implementation-artifacts/`
    3. The student implements the story themselves (BMad does **not** write the code)
    4. Update `sprint-status.yaml` manually as you progress (`backlog` → `in-progress` → `review` → `done`)
    5. Repeat for the next story

    **Reference:**
    - Sprint calendar → [`sprint-calendar.md`](_bmad-output/planning-artifacts/sprint-calendar.md)
    - Live status → [`sprint-status.yaml`](_bmad-output/implementation-artifacts/sprint-status.yaml)

    </details>

- [x] Meeting with Manuel | *16 April 2026* </br>
  [Notes](project/notes.md)