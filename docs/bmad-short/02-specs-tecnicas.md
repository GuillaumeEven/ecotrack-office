# Especificaciones Técnicas — EcoTrack Office

## Contenido

- [Contenido](#contenido)
- [Stack tecnológico](#stack-tecnológico)
- [Inicialización del proyecto](#inicialización-del-proyecto)
  - [Frontend](#frontend)
  - [Backend](#backend)
- [Árbol de archivos](#árbol-de-archivos)
  - [Frontend (`src/app/`)](#frontend-srcapp)
  - [Backend (`src/main/java/com/ecotrack/`)](#backend-srcmainjavacomecotrack)
  - [Migraciones de base de datos (`src/main/resources/db/migration/`)](#migraciones-de-base-de-datos-srcmainresourcesdbmigration)
- [Prefijos de tablas MySQL](#prefijos-de-tablas-mysql)
- [Variables de entorno obligatorias](#variables-de-entorno-obligatorias)

---

## Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Frontend | Angular + Angular Material | 21.x |
| Backend | Spring Boot (Maven, Java) | 3.5.x / Java 21 |
| Base de datos | MySQL | 8.x |
| Migraciones DB | Flyway | (automático con Spring Boot) |
| Auth | JWT (jjwt) + Spring Security | — |
| API docs | SpringDoc OpenAPI / Swagger UI | 2.8.x |
| Email (dev) | MailHog (local SMTP fake) | — |
| Entorno local | Docker Compose | — |

---

## Inicialización del proyecto

### Frontend
```bash
npm install -g @angular/cli@21
ng new ecotrack-office-frontend --routing --style scss --strict
cd ecotrack-office-frontend
ng add @angular/material
```

### Backend
```bash
# Via Spring Initializr UI: https://start.spring.io
# Group: com.ecotrack | Artifact: ecotrack-office-api | Java: 21
# Dependencies: Spring Web, Spring Data JPA, MySQL Driver,
#               Spring Security, Validation, Spring Mail, Lombok, DevTools
```
Luego añadir manualmente en `pom.xml`:
- `springdoc-openapi-starter-webmvc-ui` (para Swagger UI)
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (para JWT)

---

## Árbol de archivos

### Frontend (`src/app/`)
```
src/app/
├── core/                          ← Servicios globales (1 sola vez en AppModule)
│   ├── auth/
│   │   ├── auth.interceptor.ts    ← Inyecta el token JWT en cada petición
│   │   ├── auth.guard.ts          ← Redirige a /login si no autenticado
│   │   ├── role.guard.ts          ← Redirige si el rol no tiene acceso
│   │   └── jwt.service.ts         ← Lee/escribe el token en cookies
│   └── error.interceptor.ts       ← Maneja errores RFC 7807 globalmente
│
├── shared/                        ← Componentes y pipes reutilizables
│   └── material.module.ts         ← Re-exporta todos los módulos de Angular Material
│
├── users/                         ← BLOQUE 1 (lazy-loaded)
│   ├── users.module.ts
│   ├── login/
│   ├── register/
│   ├── profile/
│   └── admin/                     ← Gestión de usuarios (solo Admin)
│
├── assets-mgmt/                   ← BLOQUE 2 (lazy-loaded)
│   ├── assets-mgmt.module.ts
│   ├── floor-map/
│   │   ├── floor-map.component.ts ← Componente principal del mapa SVG
│   │   └── desk-marker.component.ts
│   ├── floors/
│   └── zones/
│
├── reservations/                  ← BLOQUE 3 (lazy-loaded)
│   ├── reservations.module.ts
│   ├── booking/
│   ├── my-reservations/
│   └── checkin/
│
└── analytics/                     ← BLOQUE 4 (lazy-loaded)
    ├── analytics.module.ts
    ├── dashboard/
    └── incidents/
```

### Backend (`src/main/java/com/ecotrack/`)
```
com/ecotrack/
├── config/                        ← SecurityConfig, CorsConfig, etc.
│
├── users/                         ← BLOQUE 1
│   ├── controller/UserController.java
│   ├── service/UserService.java
│   ├── repository/UserRepository.java
│   ├── model/User.java            ← @Entity → tabla usr_users
│   └── dto/                       ← UserRequest.java, UserResponse.java
│
├── assets/                        ← BLOQUE 2
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/                     ← FloorEntity.java, ResourceEntity.java, RoomEntity.java, DeskEntity.java
│   └── dto/
│
├── reservations/                  ← BLOQUE 3
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/                     ← Reservation.java, CheckInToken.java
│   └── dto/
│
└── analytics/                     ← BLOQUE 4
    ├── controller/
    ├── service/
    ├── repository/
    └── model/                     ← Incident.java, RoomOccupancy.java, AuditLog.java
    └── dto/
```

### Migraciones de base de datos (`src/main/resources/db/migration/`)
```
V1__users.sql           ← Tables usr_users, usr_refresh_tokens
V2__assets.sql          ← Tables ast_resources, ast_floors, ast_rooms, ast_desks
V3__reservations.sql    ← Tables rsv_reservations, rsv_checkin_tokens
V4__analytics.sql       ← Tables anl_incidents, anl_room_occupancy, anl_audit_logs
```
> **Importante:** los números de versión de Flyway deben coordinarse entre los 4 estudiantes. V1 primero, siempre.

---

## Prefijos de tablas MySQL

| Bloque | Prefijo | Ejemplo |
|---|---|---|
| Usuarios | `usr_` | `usr_users`, `usr_refresh_tokens` |
| Recursos físicos | `ast_` | `ast_resources`, `ast_floors`, `ast_rooms`, `ast_desks` |
| Reservas | `rsv_` | `rsv_reservations`, `rsv_checkin_tokens` |
| Analítica | `anl_` | `anl_incidents`, `anl_room_occupancy` |

---

## Variables de entorno obligatorias

```properties
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/ecotrack
DB_USERNAME=ecotrack
DB_PASSWORD=secret

# JWT
JWT_SECRET=una-clave-larga-y-aleatoria
JWT_EXPIRATION_MS=28800000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:4200

# Email SMTP
MAIL_HOST=localhost
MAIL_PORT=1025

# Fotos de incidencias
UPLOAD_DIR=/tmp/ecotrack-uploads
```
> **Nunca** pongas valores reales en el código. Siempre usa variables de entorno.