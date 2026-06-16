
# Algoritmo de Cálculo de Estado de Plantas

## `calculateFloorsStatusForDate(Long organizationId, LocalDate date)`

**Propósito:** Calcular el estado de todas las plantas para una organización en una fecha específica.

### Lógica General

1. Crear una lista vacía `floorsWithStatus`
2. Recuperar todas las plantas de la organización
3. Para cada planta, agregar un `FloorWithStatusDto` con:
   - Información de la planta
   - Estado de las salas (usando `getRoomsStatus(floorId, date)`)
   - La fecha

### Procesamiento por Planta

#### Primera Planta (i=0)

Recorrer todas las salas y calcular su estado:

- **Si la sala es de tipo `DESK_AREA`:**
  - Si es la primera sala de este tipo → estado: `available`
  - Si ocupación ≥ 80%:
    - Si existe sala siguiente [i+1] y es `DESK_AREA` → marcarla como `available`
    - Si no → establecer `floor.desksOccupied = true`

- **Si la sala es de tipo `MEETING_ROOM`:**
  - Si es la primera sala de este tipo y NO está reservada → estado: `available`
  - Si la sala está reservada:
    - Si existe sala siguiente [i+1] y es `MEETING_ROOM` → marcarla como `available`
    - Si no → establecer `floor.meetingRoomsOccupied = true`

#### Plantas Siguientes (i>0)

- **Si la planta anterior tenía `desksOccupied == true`:**
  - Recorrer todas las salas de tipo `DESK_AREA`:
    - Si es la primera sala de este tipo → estado: `available`
    - Si ocupación ≥ 80%:
      - Si existe sala siguiente [i+1] y es `DESK_AREA` → marcarla como `available`
      - Si no → establecer `floor.desksOccupied = true`

- **Si la planta anterior tenía `meetingRoomsOccupied == true`:**
  - Recorrer todas las salas de tipo `MEETING_ROOM`:
    - Si es la primera sala de este tipo y NO está reservada → estado: `available`
    - Si la sala está reservada:
      - Si existe sala siguiente [i+1] y es `MEETING_ROOM` → marcarla como `available`
      - Si no → establecer `floor.meetingRoomsOccupied = true`

---

## `calculateRoomsOccupancy(List<RoomEntity> rooms, LocalDate date)`

**Propósito:** Calcular el estado de ocupación de todas las salas en una fecha específica.

### Lógica General

1. Crear una lista vacía `roomsWithStatus`
2. Separar las salas en dos grupos:

### Procesamiento de Salas de Escritorio (DESK_AREA)

```java
List<RoomEntity> deskAreas = rooms.stream()
  .filter(r -> r.getType() == RoomType.DESK_AREA)
  .collect(Collectors.toList());
```

Para cada sala de escritorio:
- Invocar `calculateDesksStatus(Long roomId, LocalDate date)`
- Calcular el porcentaje de ocupación de la sala

### Procesamiento de Salas de Reunión (MEETING_ROOM)

```java
List<RoomEntity> meetingRooms = rooms.stream()
  .filter(r -> r.getType() == RoomType.MEETING_ROOM)
  .collect(Collectors.toList());
```

Para cada sala de reunión:
- Si tiene reserva para esa fecha → estado: `Reserved`
- Si no tiene reserva → estado: `Unavailable`

### Retorno

Retornar la lista completa `roomsWithStatus` con todos los estados calculados