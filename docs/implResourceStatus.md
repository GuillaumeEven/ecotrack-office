# Implementación de Cálculo Dinámico de Estado de Recursos

## Descripción General

Implementación de un servicio de cálculo de estado dinámico para recursos (desks y rooms) basado en la fecha y las reservas existentes. El estado ya no será estático en la base de datos, sino calculado en tiempo real según las reservas del día.

## Arquitectura

### Lógica de Estado

**Para cada desk:**
- Si existe una reserva CONFIRMED para ese día → `RESERVED`
- Si el desk está inactivo (is_active=false) → `UNAVAILABLE`
- En otro caso → `AVAILABLE`

**Para cada room (tipo desk_area):**
- Si ocupación >= 80% → `RESERVED`
- Si ocupación > 0% y < 80% → `RESERVED` (pero los desks restantes siguen disponibles)
- Si ocupación = 0% → `AVAILABLE`
- Si is_active=false → `UNAVAILABLE`

**Lógica de desbloqueo de desk_areas:**
- Inicialmente: una sola DESK_AREA está `AVAILABLE`, las otras están `UNAVAILABLE`
- Cuando una DESK_AREA `AVAILABLE` alcanza >= 80% de ocupación para ese día:
  - Se abre la siguiente DESK_AREA (si is_active=true)
  - La primera sigue aceptando las 20% de desks restantes
- Si is_active=false → no se puede desbloquear automáticamente

**Para meeting_rooms:**
- Si totalmente reservado ese día → `RESERVED`
- Si is_active=false → `UNAVAILABLE`
- En otro caso → `AVAILABLE`

### DTOs

```java
DeskWithStatusDto {
  desk: DeskResponseDto,
  calculatedStatus: ResourceStatus,
  reservedBy: String? (email usuario)
}

RoomWithStatusDto {
  room: RoomResponseDto,
  desks: List<DeskWithStatusDto>,
  occupancyRate: Double,
  roomStatus: ResourceStatus
}

FloorWithStatusDto {
  floor: FloorResponseDto,
  rooms: List<RoomWithStatusDto>,
  date: LocalDate
}
```

**Nota:** Se reutilizan los DTOs existentes:
- `DeskResponseDto` (contiene: id, name, status, isActive, equipmentList, roomId)
- `RoomResponseDto` (contiene: id, name, status, isActive, equipmentList, roomType, surfaceArea, floorId, capacity)
- `FloorResponseDto` (contiene: id, level, isActive, organizationId)

Posteriormente, al eliminar las columnas `status` de la DB, se retirará automáticamente el campo `status` de `DeskResponseDto` y `RoomResponseDto`.

### Endpoint

```
GET /api/v1/floors/{floorId}/status?date=YYYY-MM-DD
Response: FloorWithStatusDto
```

## Plan de Implementación

### Fase 1: Desarrollo Principal

- [ ] 1. Crear DTO: DeskWithStatusDto
- [ ] 2. Crear DTO: RoomWithStatusDto
- [ ] 3. Crear DTO: FloorWithStatusDto
- [ ] 4. Crear ResourceStatusCalculatorService con métodos:
  - `calculateDeskStatus(desk, date): ResourceStatus`
  - `calculateRoomStatus(room, date): ResourceStatus`
  - `getNextAvailableDeskArea(floor, date): RoomEntity?`
  - `getFloorWithStatusForDate(floorId, date): FloorWithStatusDto`
- [ ] 5. Agregar método `getFloorStatus(floorId, date)` en FloorController
- [ ] 6. Adaptar `ReservationService.createReservation()` para disparar lógica de desbloqueo
- [ ] 7. Probar el conjunto (fechas, desbloqueos, casos límite)
- [ ] 8. Archivar `syncRoomStatus()` en ResourceServiceImpl (comentar, no eliminar)

### Fase 2: Migración Futura (Deuda Técnica)

- [ ] 9. Eliminar columnas `status` de las tablas en DB (impacts en cascada)
- [ ] 10. Actualizar todas las referencias a status estático en servicios
- [ ] 11. Validar no hay queries que dependan del status viejo

## Notas

- El cálculo es **sin estado**, basado en:
  - Estado del recurso en DB (is_active)
  - Reservas para la fecha específica
  - Tipo de room
  
- El desbloqueo de desk_areas es **automático** al crear una reserva que cruza 80%
- El endpoint retorna **snapshot** completo del floor para una fecha

## Estado del Progreso

| Tarea | Estado | Notas |
|-------|--------|-------|
| Diseño arquitectura | ✅ Completado | |
| DTOs | ⏳ Por hacer | |
| ResourceStatusCalculatorService | ⏳ Por hacer | |
| Endpoint FloorController | ⏳ Por hacer | |
| Integración ReservationService | ⏳ Por hacer | |
| Tests | ⏳ Por hacer | |
| Cleanup syncRoomStatus | ⏳ Por hacer | |
| Eliminar columnas status DB | ⏳ Futura | |
