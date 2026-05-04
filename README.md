<h1 align="center">
  <br>
  🌿 EcoTrack Office
  <br>
</h1>

<p align="center">
  <strong>Smart workspace management for hybrid offices</strong><br>
  Book a desk in 60 seconds. Let the system handle the rest.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-in%20development-yellow?style=flat-square" alt="Status"/>
  <img src="https://img.shields.io/badge/type-Final%20Master's%20Project-blueviolet?style=flat-square" alt="Type"/>
  <img src="https://img.shields.io/badge/PRD-complete-brightgreen?style=flat-square" alt="PRD"/>
  <img src="https://img.shields.io/badge/architecture-complete-brightgreen?style=flat-square" alt="Architecture"/>
  <img src="https://img.shields.io/badge/epics%20%26%20stories-complete-brightgreen?style=flat-square" alt="Epics"/>
  <img src="https://img.shields.io/badge/UX%20design-complete-brightgreen?style=flat-square" alt="UX Design"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=flat-square&logo=angular&logoColor=white" alt="Angular"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java"/>
</p>

<p align="center">
  <a href="project/roadmap.md">🗺️ Roadmap</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/prd.md">📋 PRD</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/architecture.md">🏗️ Architecture</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/epics.md">📚 Epics & Stories</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/ux-design-specification.md">🎨 UX Design</a>
</p>

---

## The Problem

Large hybrid offices are wasting space and energy every day. Employees book desks and don't show up — desks sit empty and "reserved" simultaneously. Organization Admins spend mornings manually recovering abandoned bookings from a spreadsheet nobody fills in. Nobody knows which floors are actually occupied until someone walks through them.

## The Solution

EcoTrack Office is a web platform that solves both sides of the problem at once:

- **Employees** see a live interactive floor map of rectangular room tiles, drill into a room to see available desks, and reserve by shift in two taps.
- **Organization Admins** get automated ghost-desk recovery, zone consolidation suggestions, energy/CO₂ savings estimates, and occupancy dashboards — without chasing anyone.

## What Makes It Different

| Feature | How it works |
|---|---|
| 🗺️ **Map-first booking** | The building appears as a grid of room tiles. Click a room to see individual desks. Tap Morning or Afternoon to book — done. |
| 🤖 **Friction-tolerant automation** | Missed check-in? Desk auto-releases after 15 min. Check-in is a single tap from within the app — no QR scanner, no email. The system absorbs non-compliance instead of fighting it. |
| ⚡ **Zero manual recovery** | Ghost desks, faulty resources, zone shutdowns — all handled automatically. Organization Admins supervise, they don't intervene. |

## Key Metrics (MVP Targets)

| Metric | Target |
|---|---|
| Booking time | ≤ 60 seconds end-to-end |
| Ghost desk rate | < 5% after auto-release |
| Zone efficiency | ≥ 80% occupants consolidated on low-attendance days |

## Stack

```
Frontend   Angular SPA + Angular Material + SVG interactive map
Backend    Spring Boot REST API (Java)
Database   MySQL
Auth       JWT (stateless, HttpOnly cookies)
```

## Installation and development

### Configure environment variables

You must have a MySQL database dedicated to the project.

```yml
# .env
DB_USERNAME=yourUsername
DB_PASSWORD=YourPassword
DB_URL=jdbc:postgresql://localhost:5432/yourDatabaseName
```

### Back-end

```bash
cd back/ecotrack-office/
mvn spring-boot:run
```

### Front-end

#### Install

```bash
cd font/ecotrack-office/
npm install
```

#### Run server

```bash
# from /font/ecotrack-office
npm run start
```


## Personas

| Name | Role | Core need |
|---|---|---|
| **Guillermo** | R&D Engineer | Book a desk matching personal criteria in seconds, from his phone on the bus |
| **Eduardo** | Sales Rep | Never think about the tool — it works around his habits |
| **Raimundo** | Building Technician | Get instantly notified when something breaks, with photo and location |
| **José Luis** | Organization Admin | Replace his morning walk-through with a 30-second dashboard |

## Project Context

Academic project — Final Master's Project (TFM) at Ediae.
Team: 4 students × 8 weeks. Each student owns one full-stack vertical (Angular module + Spring Boot CRUD + MySQL schema).

| Block | Domain |
|---|---|
| Block 1 | Users & Authentication (company invitation code, JWT, RBAC) |
| Block 2 | Physical Assets — rooms (Desk area / Meeting room, m², 80%-opening policy) & interactive map |
| Block 3 | Reservations (shift-based), app-based check-in, auto-release, remote-work indicator |
| Block 4 | Analytics, energy/CO₂ savings estimates, dashboard & incidents |

## Roadmap

See the full project roadmap → [project/roadmap.md](project/roadmap.md)

## Documentation

| Document | Description |
|---|---|
| [📋 PRD](_bmad-output/planning-artifacts/prd.md) | Full Product Requirements Document — 43 functional requirements, 5 NFR categories, user journeys, domain constraints, phased roadmap |
| [🏗️ Architecture](_bmad-output/planning-artifacts/architecture.md) | Architecture Decision Record — stack, 14 architectural decisions, implementation patterns, full project structure, FR mapping |
| [📚 Epics & Stories](_bmad-output/planning-artifacts/epics.md) | 5 epics, 23 user stories with full acceptance criteria mapped to all 43 FRs |
| [🎨 UX Design Specification](_bmad-output/planning-artifacts/ux-design-specification.md) | 14-section UX spec — design system, component strategy, user journeys, visual foundation, accessibility (WCAG 2.2 AA) |
| [🗓️ Sprint Calendar](_bmad-output/planning-artifacts/sprint-calendar.md) | 9 sprints, 23 stories organized by technical dependencies with complexity indicators |
| [📊 Sprint Status](_bmad-output/implementation-artifacts/sprint-status.yaml) | Live sprint tracking — story statuses updated throughout development |
| [🗺️ Roadmap](project/roadmap.md) | Project checklist and next BMAD workflow steps |

---

<p align="center">
  Built with the <a href="https://github.com/bmad-method/bmad-method">BMAD Method</a> v6.2.2
</p>
