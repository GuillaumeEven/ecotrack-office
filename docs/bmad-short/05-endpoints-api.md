# Endpoints API — EcoTrack Office

> Todos los endpoints están bajo el prefijo `/api/v1/`.
> La documentación completa está disponible en Swagger UI: `http://localhost:8080/swagger-ui.html`

## Contenido

- [Contenido](#contenido)
- [Bloque 1 — Usuarios y Autenticación](#bloque-1--usuarios-y-autenticación)
- [Bloque 2 — Recursos Físicos y Mapa](#bloque-2--recursos-físicos-y-mapa)
- [Bloque 3 — Reservas y Check-in](#bloque-3--reservas-y-check-in)
- [Bloque 4 — Incidencias y Analítica](#bloque-4--incidencias-y-analítica)
- [Formato de respuestas](#formato-de-respuestas)
  - [Éxito — colección (con paginación)](#éxito--colección-con-paginación)
  - [Éxito — objeto único](#éxito--objeto-único)
  - [Éxito — acción sin datos](#éxito--acción-sin-datos)
  - [Error — formato RFC 7807](#error--formato-rfc-7807)
  - [Códigos HTTP](#códigos-http)

---

## Bloque 1 — Usuarios y Autenticación

| Método | URL | Rol requerido | Descripción |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Registro con consentimiento GDPR |
| `POST` | `/auth/login` | Público | Login → devuelve cookie JWT |
| `POST` | `/auth/logout` | Autenticado | Cierra sesión e invalida token |
| `POST` | `/auth/refresh` | Autenticado | Renueva el token JWT |
| `GET` | `/users/me` | EMPLOYEE | Ver mi perfil |
| `PATCH` | `/users/me` | EMPLOYEE | Actualizar mi perfil y preferencias |
| `GET` | `/users` | ADMIN | Listar todos los usuarios |
| `POST` | `/users` | ADMIN | Crear usuario |
| `PATCH` | `/users/{id}` | ADMIN | Actualizar usuario (rol, estado) |
| `DELETE` | `/users/{id}` | ADMIN | Eliminar usuario |
| `GET` | `/users/me/data-export` | EMPLOYEE | Exportar mis datos (GDPR) |

---

## Bloque 2 — Recursos Físicos y Mapa

| Método | URL | Rol requerido | Descripción |
|---|---|---|---|
| `GET` | `/floors` | Autenticado | Listar plantas |
| `POST` | `/floors` | ADMIN | Crear planta |
| `PATCH` | `/floors/{id}` | ADMIN | Actualizar planta |
| `GET` | `/floors/{id}/rooms` | Autenticado | Listar salas de una planta |
| `POST` | `/rooms` | ADMIN | Crear sala (tipo, m², capacidad) |
| `PATCH` | `/rooms/{id}` | ADMIN | Actualizar sala (indicador energético, capacidad) |
| `PATCH` | `/rooms/{id}/open` | TECHNICIAN | Abrir sala manualmente |
| `PATCH` | `/rooms/{id}/close` | TECHNICIAN | Cerrar sala manualmente |
| `GET` | `/floors/{id}/desks` | Autenticado | Escritorios de una planta (con estado en tiempo real) |
| `POST` | `/desks` | ADMIN | Crear escritorio |
| `PATCH` | `/desks/{id}` | ADMIN | Actualizar escritorio |

---

## Bloque 3 — Reservas y Check-in

| Método | URL | Rol requerido | Descripción |
|---|---|---|---|
| `POST` | `/reservations` | EMPLOYEE | Crear reserva (turno + fecha) |
| `GET` | `/reservations/me` | EMPLOYEE | Mis reservas (futuras y pasadas) |
| `DELETE` | `/reservations/{id}` | EMPLOYEE | Cancelar mi reserva |
| `PATCH` | `/reservations/{id}` | TECHNICIAN | Modificar o cancelar cualquier reserva |
| `PATCH` | `/reservations/{id}/check-in` | EMPLOYEE | Check-in en la app (un solo toque; autenticado) |
| `POST` | `/remote-work` | EMPLOYEE | Registrar día de trabajo remoto |

---

## Bloque 4 — Incidencias y Analítica

| Método | URL | Rol requerido | Descripción |
|---|---|---|---|
| `POST` | `/incidents` | Autenticado | Reportar una incidencia (+ foto opcional) |
| `GET` | `/incidents` | ADMIN | Listar todas las incidencias |
| `PATCH` | `/incidents/{id}/status` | TECHNICIAN | Actualizar estado (open→in_progress→resolved) |
| `GET` | `/incidents/stream` | TECHNICIAN | Stream SSE de notificaciones en tiempo real |
| `GET` | `/analytics/rooms/occupancy` | ADMIN | Ocupación por sala (día / semana) |
| `GET` | `/analytics/rooms/consolidation` | ADMIN | Sugerencias de consolidación de salas |
| `POST` | `/analytics/rooms/{id}/notify` | ADMIN | *(Post-MVP)* Notificar empleados en una sala |
| `GET` | `/analytics/reports` | ADMIN | Exportar informe de ocupación (CSV/PDF) |
| `GET` | `/audit-logs` | ADMIN | Consultar registro de auditoría |

---

## Formato de respuestas

### Éxito — colección (con paginación)
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 47
}
```

### Éxito — objeto único
```json
{ "id": 1, "email": "g@ecotrack.com", "role": "EMPLOYEE" }
```

### Éxito — acción sin datos
```
HTTP 204 No Content
```

### Error — formato RFC 7807
```json
{
  "type": "https://ecotrack.com/errors/desk-unavailable",
  "title": "Desk Unavailable",
  "status": 409,
  "detail": "Desk 42 is already reserved for this time slot.",
  "instance": "/api/v1/reservations"
}
```

### Códigos HTTP
| Situación | Código |
|---|---|
| Recurso creado | `201 Created` |
| Lectura / actualización | `200 OK` |
| Acción sin datos de retorno | `204 No Content` |
| Error de validación | `400 Bad Request` |
| No autenticado | `401 Unauthorized` |
| Rol incorrecto | `403 Forbidden` |
| No encontrado | `404 Not Found` |
| Doble reserva / conflicto | `409 Conflict` |