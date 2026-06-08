# EcoTrack Office — Jira Kanban : Tareas Frontend (Angular)

> **Stack :** Angular 21 · Angular Material · TypeScript (strict) · SCSS · HTTP Basic Auth (JWT + cookies HttpOnly diferido a Growth)

Cada sección corresponde a un **Epic Jira**. Los elementos indentados son **Stories** (tarjetas del board).
Los criterios bajo cada story son la **Definition of Done** exclusivamente para el frontend.

---

## EPIC 0 — Foundation & Infrastructure

> Prerrequisito de todos los demás epics. Debe completarse en la semana 1, en paralelo con el Epic 0 del backend.

---

### [STORY] FE-0.1 — Inicializar el workspace Angular

**Resumen :** Generar el proyecto Angular 21 con routing, SCSS strict, Angular Material y la estructura `core/shared/`.

**DoD Frontend :**
- `ng new ecotrack-office-frontend --routing --style scss --strict` ejecutado dentro de `front/`
- Angular Material añadido con tema custom Teal (`#00897B`) — M3, CSS custom properties exportadas
- `core/` creado con : `auth/auth.service.ts`, `auth.guard.ts`, `role.guard.ts`, `jwt.service.ts`; `interceptors/auth.interceptor.ts`, `error.interceptor.ts`; `sse/sse-notification.service.ts` (stubs)
- `shared/` creado con : `material.module.ts` (re-export de todos los módulos Material), `components/loading-spinner/`, `components/confirm-dialog/`, `components/error-banner/` (stubs standalone), `pipes/relative-time.pipe.ts` (stub)
- `app.routes.ts` configura lazy-loading para : `users/`, `assets-mgmt/`, `reservations/`, `analytics/`
- `environments/environment.ts` y `environment.prod.ts` contienen `apiUrl`, `sseUrl`, `pollInterval`
- `ng lint` y `ng build` pasan sin errores

**Etiquetas :** `epic-0` `frontend` `setup`

---

### [STORY] FE-0.2 — Interceptores HTTP : auth y error handling

**Resumen :** Implementar los interceptores funcionales que inyectan las credenciales HTTP Basic y gestionan los errores RFC 7807 globalmente.

**DoD Frontend :**
- `auth.interceptor.ts` — `HttpInterceptorFn` que añade el header `Authorization: Basic <base64>` en cada petición autenticada *(Growth: sustituir por extracción de cookie HttpOnly)*
- `error.interceptor.ts` — captura `HttpErrorResponse`, extrae el `ProblemDetail` RFC 7807 y emite un evento global (p.ej. `ErrorBannerComponent`) ; redirige a `/login` en caso de 401
- Los interceptores registrados en `app.config.ts` con `provideHttpClient(withInterceptors([...]))`
- `ng test` cubre ambos interceptores (happy path + error 401/403/500)

**Etiquetas :** `epic-0` `frontend` `http` `auth`

---

## EPIC 1 — User Management & Authentication

---

### [STORY] FE-1.1 — Página de registro con consentimiento GDPR

**Resumen :** Formulario de registro conectado a `POST /api/v1/auth/register`.

**DoD Frontend :**
- `RegisterComponent` (standalone) en `users/register/`
- Formulario reactivo : `name`, `email`, `password` (≥ 8 caracteres), `invitationCode`, checkbox `gdprConsent` obligatorio
- Validación client-side (Angular Validators) + mensajes de error inline con `mat-error`
- Spinner durante la petición ; redirige a `/dashboard` en éxito
- Errores API mapeados : 409 → "Email ya registrado", 400 → mensaje del `ProblemDetail`
- `ng test` cubre validaciones y flujo happy path (mock HTTP)

**Etiquetas :** `epic-1` `frontend` `auth` `gdpr`

---

### [STORY] FE-1.2 — Página de login y gestión de sesión HTTP Basic

**Resumen :** Formulario de login conectado a `POST /api/v1/auth/login` ; gestión del estado de sesión en `AuthService`.

**DoD Frontend :**
- `LoginComponent` (standalone) en `users/login/`
- `AuthService.login()` llama a `POST /api/v1/auth/login`, almacena `UserProfile` en `currentUser$` (BehaviorSubject), persiste en `sessionStorage` *(Growth: eliminar sessionStorage, la sesión la gestiona la cookie HttpOnly)*
- `AuthService.logout()` llama a `POST /api/v1/auth/logout` y limpia el estado local ; redirige a `/login`
- `AuthGuard` implementado : redirige a `/login` si no hay sesión activa
- `RoleGuard` implementado : redirige a `/forbidden` si el rol no es suficiente
- Error 401 → mensaje "Credenciales incorrectas" (sin especificar campo)
- `ng test` cubre AuthService, AuthGuard y RoleGuard

**Etiquetas :** `epic-1` `frontend` `auth`

---

### [STORY] FE-1.3 — Perfil del empleado y preferencias de búsqueda

**Resumen :** Página de perfil conectada a `GET/PATCH /api/v1/users/me`.

**DoD Frontend :**
- `ProfileComponent` (standalone) en `users/profile/`
- Muestra : nombre, email (no editable), rol, organización
- Formulario de edición de nombre y preferencias de búsqueda (JSON estructurado → campos específicos del formulario)
- Botón "Exportar mis datos" que llama a `GET /api/v1/users/me/data-export` y descarga el fichero
- `ng test` cubre el componente con mocks HTTP

**Etiquetas :** `epic-1` `frontend` `profile` `gdpr`

---

### [STORY] FE-1.4 — Panel de administración de usuarios (ADMIN)

**Resumen :** Tabla de gestión de cuentas de usuario accesible únicamente al rol `ORGANIZATION_ADMIN`.

**DoD Frontend :**
- `UserAdminComponent` (standalone) en `users/admin/`
- `MatTable` paginada con : nombre, email, rol, estado (activo/inactivo)
- Acciones en fila : editar rol, desactivar, eliminar (con `ConfirmDialogComponent`)
- Formulario de creación de usuario (nombre, email, rol)
- Ruta protegida con `RoleGuard` (solo `ORGANIZATION_ADMIN`)
- `ng test` cubre permisos y acciones CRUD con mocks

**Etiquetas :** `epic-1` `frontend` `admin`

---

## EPIC 2 — Physical Asset Management & Map

> ⚠️ El mapa es generado dinámicamente por Angular a partir de los datos de la API — sin SVG almacenado en el backend.

---

### [STORY] FE-2.1 — Servicios y modelos del bloque Assets

**Resumen :** Modelos TypeScript y servicios HTTP para Floors, Rooms y Desks.

**DoD Frontend :**
- Interfaces TypeScript : `Floor`, `Room`, `Desk` (con `displayOrder`, `status: 'AVAILABLE' | 'UNAVAILABLE'`, etc.) en `assets-mgmt/models/`
- `FloorService`, `RoomService`, `DeskService` en `assets-mgmt/services/` : wrapping de los endpoints correspondientes, retornan `Observable`
- Exports del barrel `index.ts` para cada carpeta

**Etiquetas :** `epic-2` `frontend` `models` `services`

---

### [STORY] FE-2.2 — Panel de gestión de recursos físicos (ADMIN)

**Resumen :** CRUD de Floors, Rooms y Desks accesible únicamente al administrador.

**DoD Frontend :**
- `AssetAdminComponent` en `assets-mgmt/admin/` con tabs : Plantas / Salas / Escritorios
- Para cada entidad : listado en `MatTable`, formulario de creación/edición en `MatDialog`, botón de desactivación con confirmación
- Campos por entidad :
  - Floor : nombre, número de nivel
  - Room : nombre, tipo (`DESK_AREA` / `MEETING_ROOM`), superficie m², capacidad, indicador energético
  - Desk : nombre, lista de equipamiento (chips editables), display_order
- Errores 409 mostrados en banner (escritorio con reserva activa no desactivable)
- Ruta protegida con `RoleGuard` (solo `ORGANIZATION_ADMIN`)

**Etiquetas :** `epic-2` `frontend` `admin` `assets`

---

### [STORY] FE-2.3 — Mapa interactivo de planta (FloorMapComponent)

**Resumen :** Generación dinámica del mapa de salas a partir de los datos de la API ; color-coded por estado en tiempo real.

**DoD Frontend :**
- `FloorMapComponent` (standalone) en `assets-mgmt/map/`
- Datos cargados desde `GET /api/v1/floors/{id}/rooms` ; cada sala renderizada como tile rectangular basado en `display_order` (CSS Grid o Flexbox)
- Color-coding : disponible (verde) / reservado (naranja) / incidencia (rojo) / cerrado (gris)
- Clic en tile → drill-down : abre `RoomDetailComponent` mostrando los escritorios individuales
- Polling cada 30 segundos (configurable via `environment.pollInterval`) ; indicador visual "datos desactualizados" si falla el refresh
- Carga lazy por planta (no pre-carga todas las plantas al inicio)
- `ng test` cubre la lógica de color-coding y el polling

**Etiquetas :** `epic-2` `frontend` `map` `realtime`

---

### [STORY] FE-2.4 — Vista lista y ficha de detalle de recurso

**Resumen :** Alternativa al mapa : tabla de espacios disponibles y card de detalle de sala/escritorio.

**DoD Frontend :**
- `ResourceListComponent` en `assets-mgmt/list/` : `MatTable` filtrable (planta, tipo, disponibilidad)
- `ResourceDetailComponent` : card con equipamiento, capacidad, planta, estado actual ; botón "Reservar" que abre el flujo de reserva
- Toggle mapa/lista persistido en `sessionStorage`

**Etiquetas :** `epic-2` `frontend` `list` `detail`

---

## EPIC 3 — Reservations, Check-in & Auto-release

---

### [STORY] FE-3.1 — Flujo de creación de reserva por turno

**Resumen :** Formulario de reserva conectado a `POST /api/v1/reservations`.

**DoD Frontend :**
- `ReservationFormComponent` (standalone, abre como `MatDialog`) en `reservations/form/`
- Campos : fecha (DatePicker, máx. +7 días), turno (`Mañana 08:00–14:00` / `Tarde 14:00–20:00`), escritorio/sala preseleccionado desde el contexto (mapa o lista)
- Validación : fecha en el futuro, turno seleccionado, recurso disponible (verificado contra la API)
- Errores mapeados : 409 → "Este recurso ya está reservado en ese turno"
- Confirmación con snackbar "Reserva creada"

**Etiquetas :** `epic-3` `frontend` `reservations`

---

### [STORY] FE-3.2 — Mis reservas, cancelación y trabajo remoto

**Resumen :** Vista de reservas del empleado con posibilidad de cancelar y registrar días remotos.

**DoD Frontend :**
- `MyReservationsComponent` (standalone) en `reservations/my-reservations/`
- Tabs : Próximas / Pasadas
- Acción "Cancelar" (con confirmación) sobre reservas futuras → `DELETE /api/v1/reservations/{id}`
- Botón "Registrar día remoto" → `POST /api/v1/remote-work` con la fecha seleccionada
- Reservas pasadas en modo solo lectura
- `ng test` cubre tabs, cancelación y registro remoto

**Etiquetas :** `epic-3` `frontend` `reservations` `remote-work`

---

### [STORY] FE-3.3 — Check-in en la app (un toque)

**Resumen :** Botón de check-in en la reserva activa del empleado, conectado a `PATCH /api/v1/reservations/{id}/check-in`.

**DoD Frontend :**
- Botón "Check-in" visible en `MyReservationsComponent` únicamente para la reserva activa en el turno actual (lógica de ventana temporal)
- Un clic → `PATCH /api/v1/reservations/{id}/check-in` → snackbar de confirmación + estado actualizado en UI
- Botón deshabilitado si ya se hizo check-in o si el turno no ha comenzado
- `ng test` cubre la lógica de visibilidad del botón

**Etiquetas :** `epic-3` `frontend` `check-in`

---

## EPIC 4 — Incidents & Analytics

---

### [STORY] FE-4.1 — Formulario de reporte de incidencia

**Resumen :** Modal de reporte de incidencia conectado a `POST /api/v1/incidents`.

**DoD Frontend :**
- `IncidentFormComponent` (standalone, abre como `MatDialog`) en `analytics/incidents/form/`
- Campos : descripción (textarea, obligatorio), foto (file input opcional, ≤ 5 MB, MIME validado client-side)
- Recurso preseleccionado desde el contexto (mapa o lista)
- Envío multipart si hay foto ; confirmación inmediata sin bloquear la UI (procesamiento asíncrono en backend)
- Errores : fichero demasiado grande, tipo MIME inválido

**Etiquetas :** `epic-4` `frontend` `incidents`

---

### [STORY] FE-4.2 — Notificaciones en tiempo real para el Técnico (SSE)

**Resumen :** `SseNotificationService` conectado a `GET /api/v1/incidents/stream` ; badge de notificaciones en el toolbar.

**DoD Frontend :**
- `SseNotificationService` implementado en `core/sse/` : conecta al stream SSE, emite eventos como `Observable<IncidentNotification>`
- Reconexión automática si el stream se interrumpe (exponential backoff, máx. 3 intentos)
- Badge de contador en el toolbar visible únicamente para el rol `TECHNICIAN`
- Clic en el badge → redirige a la lista de incidencias abiertas
- Servicio desconectado automáticamente en logout
- `ng test` cubre la reconexión y la desconexión

**Etiquetas :** `epic-4` `frontend` `incidents` `sse` `realtime`

---

### [STORY] FE-4.3 — Ciclo de vida de incidencias (Técnico + Admin)

**Resumen :** Vista de lista de incidencias y control de estado para Técnico ; vista de solo lectura para Admin.

**DoD Frontend :**
- `IncidentListComponent` (standalone) en `analytics/incidents/`
- `MatTable` con : recurso, descripción, estado (chip color-coded), técnico asignado, fecha
- Técnico : selector de estado inline (`open` → `in_progress` → `resolved`) → `PATCH /api/v1/incidents/{id}/status`
- Admin : solo lectura + filtros por estado y fecha
- Al resolver → snackbar + recurso vuelve a verde en el mapa (via polling o evento SSE)

**Etiquetas :** `epic-4` `frontend` `incidents` `admin` `technician`

---

### [STORY] FE-4.4 — Dashboard de analítica (ADMIN)

**Resumen :** Panel de ocupación, sugerencias de consolidación y exportación de informes.

**DoD Frontend :**
- `AnalyticsDashboardComponent` (standalone) en `analytics/dashboard/`
- Gráfico de ocupación por sala (día / semana) → `GET /api/v1/analytics/rooms/occupancy` (usar Angular Material o Chart.js ligero)
- Tarjetas de sugerencias de consolidación → `GET /api/v1/analytics/rooms/consolidation`
- Botón "Exportar informe" → `GET /api/v1/analytics/rooms/occupancy?export=true` con date range picker y descarga directa del fichero
- Ruta protegida con `RoleGuard` (solo `ORGANIZATION_ADMIN`)

**Etiquetas :** `epic-4` `frontend` `analytics` `admin`

---

## Notas transversales

| Tema | Decisión |
|---|---|
| Auth MVP | HTTP Basic — credenciales codificadas en `auth.interceptor.ts` · *(Growth: JWT cookie HttpOnly)* |
| Polling disponibilidad | Cada 30 s via `interval()` RxJS + `switchMap` ; indicador visual si falla |
| Mapa SVG | Generado dinámicamente (CSS Grid/Flexbox + `display_order`) — sin assets SVG en disco |
| Fotos de incidencias | Validación MIME y tamaño (≤ 5 MB) en cliente ; procesamiento asíncrono en servidor |
| Tests | `ng test` obligatorio en cada story — mocks HTTP via `HttpClientTestingModule` |
| Roles | Guardias aplicados en routing — `ORGANIZATION_ADMIN`, `EMPLOYEE`, `TECHNICIAN` |
