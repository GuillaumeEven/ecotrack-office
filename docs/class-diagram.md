# EcoTrack Office — Class Diagram

_Generated from PRD and Architecture Decision Document._

```plantuml
@startuml
title EcoTrack Office — Class Diagram (PlantUML)

' Enumerations
enum Role {
    EMPLOYEE
    ADMIN
    TECHNICIAN
}
enum ResourceStatus {
    AVAILABLE
    RESERVED
    UNAVAILABLE
}
enum ReservationStatus {
    CONFIRMED
    CANCELLED
    RELEASED
    CHECKED_IN
}
enum IncidentStatus {
    IN_PROGRESS
    RESOLVED
}
enum RoomType {
    DESK_AREA
    MEETING_ROOM
}

' Users & Organization (usr_)
class User {
    - id : Long
    - email : String
    - passwordHash : String
    - firstName : String
    - lastName : String
    - role : Role
    - organizationId : Long
    - isActive : Boolean
    - consentGiven : Boolean
    - savedPreferencesJson : String
    - createdAt : LocalDateTime
}

class Organization {
    - id : Long
    - name : String
    - CIF : String
    - address : String
    - email : String
    - endSubscription : Date
    - isActive : Boolean
    - createdAt : LocalDateTime
}

' Physical Assets (ast_)
class Floor {
    - id : Long
    - level : Integer
    - isActive : Boolean
    - organizationId : Long
}

abstract class Resource {
    - id : Long
    - name : String
    - status : ResourceStatus
    - isActive : Boolean
    - equipmentList : String
}

class Room {
    - type : RoomType
    - surfaceAreaM2 : Double
    - floorId : Long
    - capacity : Integer
}

class Desk {
    - roomId : Long
}

' Reservations (rsv_)
class Reservation {
    - id : Long
    - date : LocalDate
    - status : ReservationStatus
    - createdAt : LocalDateTime
    - userId : Long
    - resourceId : Long
}

' Analytics & Incidents (anl_)
class Incident {
    - id : Long
    - description : String
    - status : IncidentStatus
    - createdAt : LocalDateTime
    - resolvedAt : LocalDateTime
    - userId : Long
    - resourceId : Long
}

class AnaliticReport {
    - id : Long
    - co2SavingsKg : Double
    - energySavingsEuros : Double
    - totalReservations : Integer
    - confirmedCheckIns : Integer
    - emptyRooms : Integer
    - generatedAt : LocalDateTime
    - organizationId : Long
}

' Inheritance
Resource <|-- Desk
Resource <|-- Room

' Relationships
User "1" --> "0..*" Reservation : makes
User "1" --> "0..*" Incident : reports
Organization "1" --> "0..*" Floor : has
Organization "1" --> "1..*" User : hire
Organization "1" --> "0..*" AnaliticsReport : generates

Floor "1" --> "1..*" Room : contains
Room "1" --> "0..*" Desk : groups
Resource "1" --> "0..*" Reservation : has
Resource "1" --> "0..*" Incident : has

Reservation "0..*" --> "1" User : booked by
Reservation "0..*" --> "1" Resource : books

Incident "0..*" --> "1" Resource : targets

@enduml
```
