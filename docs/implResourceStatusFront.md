# Implémentation Frontend - Status Dynamique des Ressources

**Fecha:** 10 de junio de 2026  
**Branch:** EK-20-FE-2.1-Servicios-y-modelos-del-bloque-Assets  
**Componente:** assets-mgmt  
**Dependencia Backend:** Endpoint `GET /api/v1/floors/{id}/status?date=YYYY-MM-DD`

---

## 1. Visión General

Refactorizar el componente `AssetsMgmtComponent` para consumir el nuevo endpoint dinámico del backend que retorna el estado de recursos calculado por fecha, en lugar de hacer 4+ llamadas API independientes.

**Cambio de paradigma:**
- ❌ Antes: 4 llamadas API (floors → rooms → desks + reservations) + cálculo manual de estado
- ✅ Ahora: 1 llamada API que retorna todo calculado (floor + rooms + desks + status + occupancy)

---

## 2. Arquitectura

### 2.1 Stack de Tecnologías

| Componente | Tecnología |
|-----------|-----------|
| Framework | Angular 19+ (standalone components) |
| Http Client | HttpClientModule (RxJS Observables) |
| Modelos | TypeScript interfaces |
| Styling | Custom CSS (no Tailwind) |
| Change Detection | ChangeDetectorRef (OnPush compatible) |

### 2.2 Estructura de Datos

```typescript
// DTOs del Backend (a replicar en Frontend)

interface FloorWithStatus {
  floor: FloorResponseDto;
  rooms: RoomWithStatus[];
  date: string; // YYYY-MM-DD
}

interface RoomWithStatus {
  room: RoomResponseDto;
  desks: DeskWithStatus[];
  occupancyRate: number; // 0.0 - 1.0 (ej: 0.75 = 75%)
  roomStatus: ResourceStatus; // AVAILABLE | RESERVED | UNAVAILABLE
}

interface DeskWithStatus {
  desk: DeskResponseDto;
  calculatedStatus: ResourceStatus; // AVAILABLE | RESERVED | UNAVAILABLE
  reservedBy?: string; // email del usuario que reservó
}

type ResourceStatus = 'AVAILABLE' | 'RESERVED' | 'UNAVAILABLE';
```

### 2.3 Flujo de Datos

```
┌─────────────────────────────────────────┐
│    AssetsMgmtComponent (main)           │
│  - selectedFloorId                      │
│  - selectedDate                         │
│  - floorWithStatus: FloorWithStatus     │
└─────────────────────────────────────────┘
                    │
                    │ (1 call)
                    ▼
┌─────────────────────────────────────────┐
│  FloorService.getFloorWithStatus()      │
│  GET /api/v1/floors/{id}/status         │
│      ?date=YYYY-MM-DD                   │
└─────────────────────────────────────────┘
                    │
                    │ FloorWithStatus
                    ▼
┌─────────────────────────────────────────┐
│    Template (assets-mgmt.html)          │
│  - Rooms + Desks + States               │
│  - Cinema view with status colors       │
│  - Occupancy % per room                 │
└─────────────────────────────────────────┘
```

---

## 3. Roadmap de Implementación

### Fase 1: Modelos TypeScript (2 pasos)

#### ✨ Paso 1: Crear interfaces de DTOs

**Archivo:** `src/app/assets-mgmt/models/floor-with-status.ts`

```typescript
import { ResourceStatus } from './resource-status';
import { Floor } from './floor';
import { Room } from './room';
import { Desk } from './desk';

export interface DeskWithStatus {
  desk: Desk;
  calculatedStatus: ResourceStatus;
  reservedBy?: string;
}

export interface RoomWithStatus {
  room: Room;
  desks: DeskWithStatus[];
  occupancyRate: number;
  roomStatus: ResourceStatus;
}

export interface FloorWithStatus {
  floor: Floor;
  rooms: RoomWithStatus[];
  date: string;
}
```

#### ✨ Paso 2: Crear enum de ResourceStatus

**Archivo:** `src/app/assets-mgmt/models/resource-status.ts`

```typescript
export enum ResourceStatus {
  AVAILABLE = 'AVAILABLE',
  RESERVED = 'RESERVED',
  UNAVAILABLE = 'UNAVAILABLE'
}
```

**Actualizar:** `src/app/assets-mgmt/models/index.ts` para exportar nuevos tipos

---

### Fase 2: Servicio (1 paso)

#### ✨ Paso 3: Extender FloorService

**Archivo:** `src/app/assets-mgmt/services/floor.service.ts`

Agregar método:
```typescript
/**
 * Fetch floor with all rooms, desks and their status for a specific date
 * Single API call replacing 4+ individual calls
 */
getFloorWithStatus(id: number, date: string): Observable<FloorWithStatus> {
  const url = `${this.apiUrl}/${id}/status`;
  const params = { date };
  return this.httpClient.get<FloorWithStatus>(url, { params });
}
```

**Importar:** `FloorWithStatus` from models

---

### Fase 3: Componente (4 pasos)

#### 🔄 Paso 4: Refactorizar estado del componente

**Archivo:** `src/app/assets-mgmt/components/assets-mgmt.component.ts`

**Cambios:**
- ❌ Eliminar: `floors[]`, `rooms[]`, `desks[]`, `reservations[]`
- ✅ Agregar: `floorWithStatus: FloorWithStatus | null = null`
- ✅ Agregar: `allFloors: Floor[]` (solo para selector)
- Mantener: `selectedFloorId`, `selectedDate`, `loading`, `errors`

**Lógica de estado simplificada:**
```typescript
export class AssetsMgmtComponent implements OnInit {
  allFloors: Floor[] = [];
  floorWithStatus: FloorWithStatus | null = null;
  
  selectedFloorId: number | null = null;
  selectedDate: Date = new Date();
  
  loading = {
    floors: false,
    floorStatus: false
  };
  
  errors = {
    floors: null as string | null,
    floorStatus: null as string | null
  };
}
```

#### 🔄 Paso 5: Refactorizar métodos de carga

**Cambios en `onFloorSelect()`:**
```typescript
onFloorSelect(floorId: number): void {
  this.selectedFloorId = floorId;
  this.floorWithStatus = null;
  this.loadFloorWithStatus();
}
```

**Nuevo método `loadFloorWithStatus()`:**
```typescript
loadFloorWithStatus(): void {
  if (!this.selectedFloorId) return;
  
  this.loading.floorStatus = true;
  this.errors.floorStatus = null;
  
  const dateISO = this.formatDateToISO(this.selectedDate);
  
  this.floorService.getFloorWithStatus(this.selectedFloorId, dateISO)
    .subscribe({
      next: (data) => {
        this.floorWithStatus = data;
        this.loading.floorStatus = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errors.floorStatus = `Failed to load floor status: ${err.message}`;
        this.loading.floorStatus = false;
        this.cdr.markForCheck();
      }
    });
}
```

**Cambios en `onDateChange()`:**
```typescript
onDateChange(event: any): void {
  this.selectedDate = new Date(event.target.value);
  this.loadFloorWithStatus();
}
```

**Métodos a eliminar:**
- ❌ `loadRooms()` - ya no necesario
- ❌ `loadDesks()` - ya no necesario
- ❌ `loadReservations()` - ya no necesario
- ❌ `getDeskStatus()` - ya viene calculado

#### 🔄 Paso 6: Simplificar handlers

**`onRoomSelect()` se convierte en selector puro** (sin carga):
```typescript
onRoomSelect(roomId: number): void {
  // Solo para UI, no necesita cargar nada
  // Los datos ya están en floorWithStatus
}
```

#### 🔄 Paso 7: Helpers para template

**Agregar métodos utilitarios:**
```typescript
/**
 * Get room by ID from floorWithStatus
 */
getRoomById(roomId: number): RoomWithStatus | undefined {
  return this.floorWithStatus?.rooms.find(r => r.room.id === roomId);
}

/**
 * Get desks for a specific room
 */
getDesksByRoom(roomId: number): DeskWithStatus[] {
  return this.getRoomById(roomId)?.desks ?? [];
}

/**
 * Get occupancy percentage display
 */
getOccupancyPercent(room: RoomWithStatus): string {
  return `${Math.round(room.occupancyRate * 100)}%`;
}

/**
 * Check if desk is available for reservation
 */
isDeskAvailable(desk: DeskWithStatus): boolean {
  return desk.calculatedStatus === ResourceStatus.AVAILABLE;
}
```

---

### Fase 4: Template (actualización de referencias)

#### 📝 Paso 8: Actualizar template

**Cambios en `assets-mgmt.component.html`:**

- Reemplazar: `rooms` → `floorWithStatus?.rooms`
- Reemplazar: `desks` → `getDesksByRoom(selectedRoomId)`
- Reemplazar: `getDeskStatus(desk)` → `desk.calculatedStatus`
- Agregar: Mostrar `occupancyRate` de la room
- Agregar: Mostrar `reservedBy` si desk está RESERVED

**Ejemplo de cambios:**
```html
<!-- Rooms Section -->
<div *ngIf="floorWithStatus">
  <div class="btn-room-grid">
    <button
      *ngFor="let room of floorWithStatus.rooms"
      (click)="onRoomSelect(room.room.id)"
      [class.active]="selectedRoomId === room.room.id"
      class="btn-room">
      <strong class="text-body-md">{{ room.room.name }}</strong>
      <small class="text-on-surface-variant">
        Cap: {{ room.room.capacity }} | {{ getOccupancyPercent(room) }}
      </small>
    </button>
  </div>
</div>

<!-- Desks Cinema View -->
<div *ngIf="floorWithStatus && selectedRoomId">
  <div class="cinema-desks">
    <button
      *ngFor="let desk of getDesksByRoom(selectedRoomId)"
      [class.available]="isDeskAvailable(desk)"
      [class.reserved]="desk.calculatedStatus === 'RESERVED'"
      [class.unavailable]="desk.calculatedStatus === 'UNAVAILABLE'"
      class="desk-button">
      {{ desk.desk.name }}
      <span *ngIf="desk.reservedBy" class="text-xs">
        ({{ desk.reservedBy }})
      </span>
    </button>
  </div>
</div>
```

---

### Fase 5: Testing (2 pasos)

#### ✅ Paso 9: Test - Estados por fecha

**Escenarios:**
- Cargar floor sin reservaciones (date=2026-07-10)
- Cargar floor con reservaciones (date=2026-07-15)
- Cambiar fecha y verificar actualización automática
- Verificar desks mostrados con estado correcto

#### ✅ Paso 10: Test - Déblocage y ocupancy

**Escenarios:**
- Crear nueva reserva y verificar actualización
- Simular occupancy >= 80% en desk_area
- Verificar que próxima room se desbloquea
- Verificar `reservedBy` se muestra correctamente

---

## 4. Consideraciones de Implementación

### 4.1 Performance

- ✅ Reducción de 75% en llamadas API (4 → 1)
- ✅ Eliminación de cálculos de estado en frontend
- ✅ Mejor UX: datos completos en 1 request

### 4.2 Compatibilidad

- ✅ Mantener interfaces Floor, Room, Desk intactas
- ✅ Agregar nuevas interfaces sin romper existentes
- ✅ FloorService existente permanece, solo se agrega método

### 4.3 Styling

- ✅ Reutilizar clases CSS existentes
- ✅ Agregar clases para occupancy % (opcional)
- ✅ Mantener tema dark del cinema view

### 4.4 Change Detection

- Mantener `ChangeDetectorRef.markForCheck()` en subscriptions
- Compatible con `OnPush` strategy si se implementa

---

## 5. Orden de Implementación (Recomendado)

1. **Paso 1-2:** Crear modelos (10 min)
2. **Paso 3:** Extender service (5 min)
3. **Paso 4-7:** Refactorizar componente (30 min)
4. **Paso 8:** Actualizar template (20 min)
5. **Paso 9-10:** Testing manual (20 min)

**Total estimado:** ~1.5 horas

---

## 6. Rollback Plan

Si surge un problema:
1. Los cambios son aditivos (nueva lógica convive con antigua)
2. Mantener branch actual
3. Revertibar solo service.ts si es necesario
4. Template y componente pueden revertirse independientemente

---

## 7. Notas Técnicas

### Mapeo de estatus

Backend calcula status como:
```
DESK:
  - RESERVED si existe reserva CONFIRMED para esa fecha
  - UNAVAILABLE si is_active=false
  - AVAILABLE sino

ROOM (DESK_AREA):
  - RESERVED si occupancy >= 80%
  - UNAVAILABLE si is_active=false
  - AVAILABLE sino

ROOM (MEETING_ROOM):
  - RESERVED si completamente reservado ese día
  - UNAVAILABLE si is_active=false
  - AVAILABLE sino
```

Frontend debe solo mostrar estos valores sin recalcular.

### Edge Cases Cubiertos

- ✅ Floor sin rooms
- ✅ Rooms sin desks
- ✅ Desks sin reservaciones
- ✅ Múltiples desks_areas en mismo floor
- ✅ Cambio de fecha varias veces
- ✅ Desks inactivos (is_active=false)

---

## 8. Archivos a Modificar

```
front/ecotrack-office/src/app/assets-mgmt/
├── models/
│   ├── floor-with-status.ts           [NEW]
│   ├── resource-status.ts             [NEW]
│   └── index.ts                       [EDIT - agregar exports]
├── services/
│   ├── floor.service.ts               [EDIT - agregar getFloorWithStatus()]
│   └── api.config.ts                  [CHECK - URLs]
└── components/
    ├── assets-mgmt.component.ts       [EDIT - refactor principal]
    ├── assets-mgmt.component.html     [EDIT - actualizar refs]
    └── assets-mgmt.component.css      [CHECK - estilos para occupancy]
```

---

**Status:** 📋 Listo para implementación  
**Última actualización:** 2026-06-10  
**Responsable:** Frontend Team
