# Patrón Barrel en Angular / TypeScript

Un **barrel** es un fichero `index.ts` que re-exporta todo el contenido de una carpeta, actuando como punto de entrada único del módulo.

## Problema sin barrel

```typescript
import { Floor }    from './assets-mgmt/models/floor.model';
import { Room }     from './assets-mgmt/models/room.model';
import { Desk }     from './assets-mgmt/models/desk.model';
import { DeskStatus } from './assets-mgmt/models/desk-status.enum';
```

## Solución con barrel

**`assets-mgmt/models/index.ts` :**
```typescript
export * from './floor.model';
export * from './room.model';
export * from './desk.model';
export * from './desk-status.enum';
```

**Import resultante :**
```typescript
import { Floor, Room, Desk, DeskStatus } from './assets-mgmt/models';
```

## Cuándo usarlo

| Usar barrel | No usar barrel |
|---|---|
| Carpetas con ≥ 3 ficheros relacionados | Ficheros únicos (un solo servicio) |
| Modelos, enums, interfaces | Componentes standalone (Angular los gestiona solo) |
| Servicios de un mismo bloque funcional | Ficheros de configuración (`app.config.ts`) |

## Estructura en EcoTrack

```
assets-mgmt/
├── models/
│   ├── index.ts          ← barrel: exporta Floor, Room, Desk...
│   ├── floor.model.ts
│   ├── room.model.ts
│   └── desk.model.ts
├── services/
│   ├── index.ts          ← barrel: exporta FloorService, RoomService...
│   ├── floor.service.ts
│   └── room.service.ts
```

## Precaución

Evitar los **barrel circular imports** : si `A` exporta `B` y `B` importa desde el barrel de `A`, TypeScript puede entrar en un ciclo de resolución. En ese caso, importar directamente desde el fichero fuente.
