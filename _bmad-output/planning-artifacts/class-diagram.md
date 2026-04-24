# EcoTrack Office — Class Diagram

_Generated from PRD and Architecture Decision Document._

```mermaid
classDiagram
    direction TB

    %% ── Enumerations ─────────────────────────────────────────────────
    class Role {
        <<enumeration>>
        EMPLOYEE
        FACILITIES_MANAGER
        TECHNICIAN
    }

    class ResourceStatus {
        <<enumeration>>
        AVAILABLE
        RESERVED
        UNAVAILABLE
    }

    class ReservationStatus {
        <<enumeration>>
        CONFIRMED
        CANCELLED
        RELEASED
        CHECKED_IN
    }

    class IncidentStatus {
        <<enumeration>>
        OPEN
        IN_PROGRESS
        RESOLVED
    }

    %% ── Block 1 – Users & Auth (usr_) ────────────────────────────────
    class User {
        +Long id
        +String email
        +String passwordHash
        +String firstName
        +String lastName
        +Role role
        +Boolean isActive
        +Boolean consentGiven
        +String savedPreferencesJson
        +LocalDateTime createdAt
    }

    class RefreshToken {
        +Long id
        +String token
        +LocalDateTime expiresAt
        +LocalDateTime revokedAt
        +Long userId
    }

    %% ── Block 2 – Physical Assets (ast_) ─────────────────────────────
    class Building {
        +Long id
        +String name
        +String address
        +Boolean isActive
    }

    class Floor {
        +Long id
        +String name
        +Integer level
        +Boolean isActive
        +String svgFloorPlanPath
    }

    class Zone {
        +Long id
        +String name
        +Boolean energyFlag
        +Boolean isActive
    }

    class Resource {
        <<abstract>>
        +Long id
        +String name
        +Float svgAnchorX
        +Float svgAnchorY
        +ResourceStatus status
        +Boolean isActive
    }

    class Desk {
        +String equipmentList
    }

    class MeetingRoom {
        +Integer capacity
        +String equipmentList
    }

    %% ── Block 3 – Reservations (rsv_) ────────────────────────────────
    class Reservation {
        +Long id
        +LocalDate date
        +LocalTime startTime
        +LocalTime endTime
        +ReservationStatus status
        +LocalDateTime createdAt
    }

    class CheckInToken {
        +Long id
        +String token
        +LocalDateTime expiresAt
        +LocalDateTime usedAt
    }

    %% ── Block 4 – Analytics & Incidents (anl_) ───────────────────────
    class Incident {
        +Long id
        +String description
        +String photoPath
        +IncidentStatus status
        +LocalDateTime createdAt
        +LocalDateTime resolvedAt
    }

    class AuditLog {
        +Long id
        +String entityType
        +Long entityId
        +String action
        +LocalDateTime timestamp
    }

    class ZoneOccupancy {
        +Long id
        +LocalDate date
        +Integer totalReservations
        +Integer confirmedCheckIns
        +LocalDateTime generatedAt
    }

    %% ── Inheritance ──────────────────────────────────────────────────
    Resource <|-- Desk
    Resource <|-- MeetingRoom

    %% ── Block 1 Relationships ────────────────────────────────────────
    User "1" --> "0..*" RefreshToken : owns
    User "1" --> "0..*" Reservation : makes
    User "1" --> "0..*" Incident : reports
    User "1" --> "0..*" Incident : resolves
    User "1" --> "0..*" AuditLog : actor

    %% ── Block 2 Relationships ────────────────────────────────────────
    Building "1" --> "1..*" Floor : has
    Floor "1" --> "1..*" Zone : contains
    Zone "1" --> "0..*" Resource : groups

    %% ── Block 3 Relationships ────────────────────────────────────────
    Reservation "0..*" --> "1" User : booked by
    Reservation "0..*" --> "1" Resource : books
    Reservation "1" --> "0..1" CheckInToken : confirmed via

    %% ── Block 4 Relationships ────────────────────────────────────────
    Incident "0..*" --> "1" Resource : targets
    Zone "1" --> "0..*" ZoneOccupancy : tracked by
```
