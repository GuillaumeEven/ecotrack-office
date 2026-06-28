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

' User & Organization (usr_)
class usr_user {
    - id : Long
    - email : String
    - password_hash : String
    - first_name : String
    - last_name : String
    - role : Role
    - organization_id : Long
    - is_active : Boolean
    - consent_given : Boolean
    - preferences_json : String
    - created_at : LocalDateTime
    + createUserWithCif(UserCreateRequestDto dto): UserModel
    + getUsersByOrganization(Long organizationId, Pageable pageable): PageResponseDto<UserResponseDto>
}

class organization {
    - id : Long
    - name : String
    - cif : String
    - address : String
    - email : String
    - end_subscription : Date
    - is_active : Boolean
    - created_at : LocalDateTime
    + createOrganization(OrganizationCreateDto dto): OrganizationModel
    + updateOrganizatioinById(Long id, OrganizationUpdateDto dto): OrganizationModel
}

' Physical Assets (ast_)
class ast_floor {
    - id : Long
    - level : Integer
    - is_active : Boolean
    - organization_id : Long
    - name: String
    + createFloor(FloorRequestDto floorRequestDto): FloorModel
    + updateFloor(Long id, FloorReqestDto floorRequestDto): FloorModel
}

abstract class ast_resource {
    - id : Long
    - name : String
    - status : ResourceStatus
    - is_active : Boolean
    - equipment_list : String
    + updateStatus(Long resourceId, ResourceStatus newStatus): void
    + getResourceById(Long resourceId): ResourceModel
}

class ast_room {
    - type : RoomType
    - surface_area : Double
    - floor_id : Long
    - capacity : Integer
    + getRoomsByFloorId(Long floorId): List<RoomModel>
    + deleteRoomById(Long roomId): void
}

class ast_desk {
    - room_id : Long
    + getDesksByRoomId(Long roomId): List<DeskModel>
    + createDesk(DeskRequestDto deskRequestDto): DeskModel
    + deleteDeskById(Long deskId): void
}

' Reservation (rsv_)
class reservation {
    - id : Long
    - date : LocalDate
    - status : ReservationStatus
    - created_at : LocalDateTime
    - user_id : Long
    - resource_id : Long
    + getReservationsByUserId(Long userId): List<ReservationModel>
    + getReservationsByFloorIdAndDate(Long floorId, String date): List<ReservationModel>
}

' Analytic, Incident & AuditLog (anl_)
class incident {
    - id : Long
    - description : String
    - status : IncidentStatus
    - created_at : LocalDateTime
    - resolved_at : LocalDateTime
    - user_id : Long
    - resource_id : Long
    + createIncident(IncidentRequestDto dto): IncidentResponseDto
    + resolveIncident(Long id): IncidentResponseDto
}

class analitic_report {
    - id : Long
    - co2_savings_kg : Double
    - energy_savings_euros : Double
    - total_reservations : Integer
    - confirmed_check_ins : Integer
    - empty_rooms : Integer
    - generated_at : LocalDateTime
    - organization_id : Long
    + getAllReports(Authentication auth): List<AnalyticsReportModel>
    + generateReport(Long userId, AnalyticsReportGenerateDto dto): AnalyticsReportModel
}

class anl_audit_log {
    - id: Long
    - event_type: String
    - entity_type: String
    - entity_id: Long
    - actor_id: Long
    - created_at: LocalDateTime
    + log(String enventType, String entityType, Long entityId, Long actorId): void
}

' Inheritance
ast_resource <|-- ast_desk
ast_resource <|-- ast_room

' Relationships
usr_user "1" --> "0..*" reservation : makes
usr_user "1" --> "0..*" incident : reports
organization "1" --> "0..*" ast_floor : has
organization "1" --> "1..*" usr_user : hire
organization "1" --> "0..*" analitic_report : generates

ast_floor "1" --> "1..*" ast_room : contains
ast_room "1" --> "0..*" ast_desk : groups
ast_resource "1" --> "0..*" reservation : has
ast_resource "1" --> "0..*" incident : has

reservation "0..*" --> "1" ast_resource : books

incident "0..*" --> "1" ast_resource : targets

@enduml
```
