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
  <a href="_bmad-output/planning-artifacts/prd.md">📋 Product Requirements Document</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/architecture.md">🏗️ Architecture</a>
</p>

---

## The Problem

Large hybrid offices are wasting space and energy every day. Employees book desks and don't show up — desks sit empty and "reserved" simultaneously. Facilities managers spend mornings manually recovering abandoned bookings from a spreadsheet nobody fills in. Nobody knows which floors are actually occupied until someone walks through them.

## The Solution

EcoTrack Office is a web platform that solves both sides of the problem at once:

- **Employees** see a live interactive floor map, filter by their criteria (near restrooms, south-facing, specific equipment), and reserve in two taps.
- **Facilities managers** get automated ghost-desk recovery, zone consolidation suggestions, occupancy dashboards, and exportable reports — without chasing anyone.

## What Makes It Different

| Feature | How it works |
|---|---|
| 🗺️ **Map-first booking** | The building floor plan *is* the UI. Color-coded zones nudge users toward energy-efficient clustering — the sustainable choice is the obvious choice. |
| 🤖 **Friction-tolerant automation** | Missed check-in? Desk auto-releases after 15 min. No login? Check in via email link. The system absorbs non-compliance instead of fighting it. |
| ⚡ **Zero manual recovery** | Ghost desks, faulty resources, zone shutdowns — all handled automatically. Facilities managers supervise, they don't intervene. |

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

## Personas

| Name | Role | Core need |
|---|---|---|
| **Guillermo** | R&D Engineer | Book a desk matching personal criteria in seconds, from his phone on the bus |
| **Eduardo** | Sales Rep | Never think about the tool — it works around his habits |
| **Raimundo** | Building Technician | Get instantly notified when something breaks, with photo and location |
| **José Luis** | Facilities Manager | Replace his morning walk-through with a 30-second dashboard |

## Project Context

Academic project — Final Master's Project (TFM) at Ediae.
Team: 4 students × 8 weeks. Each student owns one full-stack vertical (Angular module + Spring Boot CRUD + MySQL schema).

| Block | Domain |
|---|---|
| Block 1 | Users & Authentication |
| Block 2 | Physical Assets & SVG Floor Map |
| Block 3 | Reservations, Check-in & Auto-release |
| Block 4 | Analytics, Dashboard & Incidents |

## Roadmap

See the full project roadmap → [project/roadmap.md](project/roadmap.md)

## Documentation

| Document | Description |
|---|---|
| [📋 PRD](_bmad-output/planning-artifacts/prd.md) | Full Product Requirements Document — 43 functional requirements, 5 NFR categories, user journeys, domain constraints, phased roadmap |
| [🏗️ Architecture](_bmad-output/planning-artifacts/architecture.md) | Architecture Decision Record — stack, 14 architectural decisions, implementation patterns, full project structure, FR mapping |
| [🗺️ Roadmap](project/roadmap.md) | Project checklist and next BMAD workflow steps |

---

<p align="center">
  Built with the <a href="https://github.com/bmad-method/bmad-method">BMAD Method</a> v6.2.2
</p>
