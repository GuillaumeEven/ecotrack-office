# EcoTrack Office — Class Diagram

_Generated from PRD and Architecture Decision Document._

```mermaid
classDiagram
    direction TB

    %% ── Enumerations ─────────────────────────────────────────────────
    class Role {
        <<enumeration>>
        EMPLOYEE
        ADMIN
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
        IN_PROGRESS
        RESOLVED
    }

    %% ── Block 1 – Users & Organization (usr_) ────────────────────────────────
    class User {
        -Long id
        -String email
        -String passwordHash
        -String firstName
        -String lastName
        -Role role
        -Long organizationId
        -Boolean isActive
        -Boolean consentGiven
        -String savedPreferencesJson
        -LocalDateTime createdAt
    }

    class Organization {
        -Long id
        -String name
        -String CIF
        -String adress
        -String emais
        -Date endSubscription
        -Boolean isActive
        -LocalDateTime createdAt
    }

    %% ── Block 2 – Physical Assets (ast_) ─────────────────────────────

    class Floor {
        -Long id
        -Integer level
        -Boolean isActive
        -Long organizationId
    }

    class Resource {
        <<abstract>>
        -Long id
        -String name
        -ResourceStatus status
        -Boolean isActive
        -Long floorId
    }

    class Room {
        -RoomType type
        -Float surfaceAreaM2
        -Integer capacity
        -Boolean isActive
    }

    class RoomType {
        <<enumeration>>
        DESK_AREA
        MEETING_ROOM
    }

    class Desk {
        -String equipmentList
        -Long RoomId
    }

    %% ── Block 3 – Reservations (rsv_) ────────────────────────────────
    class Reservation {
        -Long id
        -LocalDate date
        -ReservationStatus status
        -LocalDateTime createdAt
        -Long userId
        -Long resourceId
    }

    %% ── Block 4 – Analytics & Incidents (anl_) ───────────────────────
    class Incident {
        -Long id
        -String description
        -IncidentStatus status
        -LocalDateTime createdAt
        -LocalDateTime resolvedAt
        -Long userId
        -Long resourceId
    }

    class AnaliticReport {
        -Long id
        -Double co2SavingsKg
        -Double energySavingsEuros
        -Integer totalReservations
        -Integer confirmedCheckIns
        -Integer emptyRooms
        -LocalDateTime generatedAt
        -Lond organizationId

    }

    %% ── Inheritance ──────────────────────────────────────────────────
    Resource <|-- Desk
    Resource <|-- Room

    %% ── Block 1 Relationships ────────────────────────────────────────
    User "1" --> "0..*" Reservation : makes
    User "1" --> "0..*" Incident : reports
    Organization "1" --> "0..*" Floor : has
    Organization "1" --> "1..*" User : hire
    Organization "1" --> "0..*" AnaliticsReport : generates

    %% ── Block 2 Relationships ────────────────────────────────────────
    Floor "1" --> "1..*" Room : contains
    Room "1" --> "0..*" Desk : groups
    Resource "1" --> "0..*" Reservation : has
    Resource "1" --> "0..*" Incident : has

    %% ── Block 3 Relationships ────────────────────────────────────────
    Reservation "0..*" --> "1" User : booked by
    Reservation "0..*" --> "1" Resource : books

    %% ── Block 4 Relationships ────────────────────────────────────────
    Incident "0..*" --> "1" Resource : targets
    
```
