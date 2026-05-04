# EcoTrack Office — Class Diagram

_Generated from PRD and Architecture Decision Document._

```mermaid
classDiagram
    direction TB

    %% ── Enumerations ─────────────────────────────────────────────────
    class Role {
        <<enumeration>>
        EMPLOYEE
        ORGANIZATION_ADMIN
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
        PENDING
        CONFIRMED
        CANCELLED
        RELEASED
        CHECKED_IN
    }

    class Shift {
        <<enumeration>>
        MORNING
        AFTERNOON
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
        +Long organizationId
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

    class Room {
        +Long id
        +String name
        +RoomType type
        +Float surfaceAreaM2
        +Integer capacity
        +Boolean energyFlag
        +Boolean isOpen
        +Boolean isActive
    }

    class RoomType {
        <<enumeration>>
        DESK_AREA
        MEETING_ROOM
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

    %% ── Block 3 – Reservations (rsv_) ────────────────────────────────
    class Reservation {
        +Long id
        +LocalDate date
        +Shift shift
        +ReservationStatus status
        +LocalDateTime createdAt
    }

    class RemoteWorkEntry {
        +Long id
        +Long userId
        +LocalDate date
        +LocalDateTime createdAt
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
        +Long roomId
        +LocalDate date
        +Integer totalReservations
        +Integer confirmedCheckIns
        +Float energySavingsEuros
        +Float co2SavingsKg
        +LocalDateTime generatedAt
    }

    %% ── Inheritance ──────────────────────────────────────────────────
    Resource <|-- Desk

    %% ── Block 1 Relationships ────────────────────────────────────────
    User "1" --> "0..*" RefreshToken : owns
    User "1" --> "0..*" Reservation : makes
    User "1" --> "0..*" RemoteWorkEntry : logs
    User "1" --> "0..*" Incident : reports
    User "1" --> "0..*" Incident : resolves
    User "1" --> "0..*" AuditLog : actor

    %% ── Block 2 Relationships ────────────────────────────────────────
    Building "1" --> "1..*" Floor : has
    Floor "1" --> "1..*" Room : contains
    Room "1" --> "0..*" Resource : groups

    %% ── Block 3 Relationships ────────────────────────────────────────
    Reservation "0..*" --> "1" User : booked by
    Reservation "0..*" --> "1" Resource : books

    %% ── Block 4 Relationships ────────────────────────────────────────
    Incident "0..*" --> "1" Resource : targets
    Room "1" --> "0..*" ZoneOccupancy : tracked by
```
