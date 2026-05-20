# EcoTrack Office — Jira Kanban : Tareas Backend (Spring Boot)

> **Stack :** Spring Boot 3.5.x · MySQL 8 · Flyway · Spring Security · HTTP Basic Auth (JWT + jjwt diferido a Growth) · SpringDoc OpenAPI v2.8.x · RFC 7807 Problem Details

Cada sección corresponde a un **Epic Jira**. Los elementos indentados son **Stories** (tarjetas del board).
Los criterios bajo cada story son la **Definition of Done** exclusivamente para el backend.

---

## EPIC 0 — Foundation & Infrastructure

> Prerrequisito técnico de todos los demás epics. Debe completarse íntegramente en la semana 1.

---

### [STORY] BE-0.1 — Inicializar el proyecto Spring Boot

**Resumen :** Generar el proyecto Spring Boot 3.5.x con Spring Initializr y añadir todas las dependencias necesarias.

**DoD Backend :**
- Proyecto Maven generado con las dependencias : `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-boot-starter-mail`, `mysql-connector-j`, `flyway-core`, `lombok`, `spring-boot-devtools`
- Dependencias manuales añadidas : `springdoc-openapi-starter-webmvc-ui:2.8.x`, `flyway-mysql` *(Growth: `jjwt-api/impl/jackson` cuando se implemente JWT)*
- Estructura de paquetes creada : `com.ecotrack.users`, `com.ecotrack.assets`, `com.ecotrack.reservations`, `com.ecotrack.analytics`, `com.ecotrack.common`
- `mvn test` pasa sin errores

**Etiquetas :** `epic-0` `backend` `setup`

---

### [STORY] BE-0.2 — Configurar Flyway y la migración inicial

**Resumen :** Configurar Flyway para la gestión del esquema de base de datos con una migración de baseline vacía.

**DoD Backend :**
- Flyway configurado en `application.properties` (`spring.flyway.enabled=true`, `spring.flyway.locations=classpath:db/migration`)
- Migración `V1__baseline.sql` creada (fichero vacío que firma la baseline)
- La aplicación arranca sin errores con `mvn spring-boot:run`
- Ningún valor de configuración hardcodeado : URL de BD, credenciales y esquema provistos mediante variables de entorno (`DB_URL`, `DB_USER`, `DB_PASSWORD`)

**Etiquetas :** `epic-0` `backend` `database` `flyway`

---

### [STORY] BE-0.3 — GlobalExceptionHandler y formato RFC 7807

**Resumen :** Crear el `@ControllerAdvice` global que devuelve `ProblemDetail` de Spring 6 para todos los errores de la API.

**DoD Backend :**
- `GlobalExceptionHandler` implementado en `com.ecotrack.common.exception`
- Gestiona : `MethodArgumentNotValidException` (400 + errores de campo), `EntityNotFoundException` (404), `DataIntegrityViolationException` (409), acceso no autorizado (401/403), `Exception` genérica (500)
- Devuelve **siempre** `ProblemDetail` — nunca `Map<String, Object>`
- La respuesta incluye : `type`, `title`, `status`, `detail`, `instance`
- Tests unitarios sobre `GlobalExceptionHandler`

**Etiquetas :** `epic-0` `backend` `error-handling`

---

### [STORY] BE-0.4 — Configurar SpringDoc OpenAPI / Swagger UI

**Resumen :** Configurar Swagger UI accesible en `/swagger-ui.html`; sin esquema de seguridad Bearer en MVP (HTTP Basic).

**DoD Backend :**
- `OpenApiConfig` en `com.ecotrack.common.config`
- Swagger UI disponible en `/swagger-ui.html`
- Sin esquema `BearerAuth` en MVP; nota en la configuración que se añadirá en Growth con JWT cookie
- Todas las rutas públicas (login, register) documentadas sin autenticación requerida
- Spec OpenAPI exportable en formato JSON via `/v3/api-docs`

**Etiquetas :** `epic-0` `backend` `openapi`

---

### [STORY] BE-0.5 — Docker Compose : MySQL 8 + MailHog

**Resumen :** Proveer un `docker-compose.yml` funcional para el entorno local de desarrollo con MySQL y MailHog.

**DoD Backend :**
- `docker-compose.yml` en la raíz del proyecto arranca MySQL 8 (puerto 3306) y MailHog (SMTP 1025, UI 8025)
- Volúmenes con nombre para la persistencia de datos MySQL entre reinicios
- Fichero `.env.example` con todas las variables necesarias (sin credenciales reales)
- La aplicación Spring Boot arranca sin errores apuntando a ese MySQL
- CORS configurado mediante `CORS_ALLOWED_ORIGINS` (variable de entorno), sin valores hardcodeados

**Etiquetas :** `epic-0` `backend` `devops` `docker`

---

## EPIC 1 — User Management & Authentication

> Registro, login, RBAC, perfil de empleado, administración de cuentas.

---

### [STORY] BE-1.1 — Esquema Flyway : tablas `usr_*`

**Resumen :** Crear la migración Flyway que define todas las tablas del bloque de usuarios.

**DoD Backend :**
- Migración `V2__users.sql` crea :
  - `usr_organizations` : id, name, invitation_code (unique), created_at
  - `usr_users` : id, name, email (unique), password (plain text — demo MVP scope only), role ENUM(`EMPLOYEE`/`ORGANIZATION_ADMIN`/`TECHNICIAN`), organization_id FK, gdpr_consent BOOLEAN, consent_at, is_active BOOLEAN DEFAULT TRUE, search_preferences JSON, created_at
  *(Growth: renombrar a `hashed_password` y activar bcrypt)*
  - *(Growth: `usr_refresh_tokens` : id, token_hash (unique), user_id FK, expires_at, revoked BOOLEAN DEFAULT FALSE, created_at — no necesaria en MVP HTTP Basic)*
- Índices sobre `email`, `invitation_code`
- `mvn flyway:migrate` pasa sin errores

**Etiquetas :** `epic-1` `backend` `database` `flyway`

---

### [STORY] BE-1.2 — Entidades JPA, repositories y DTOs del bloque Users

**Resumen :** Implementar las entidades JPA, los Spring Data repositories y los DTOs de transferencia del bloque Users.

**DoD Backend :**
- Entidades : `UserEntity`, `OrganizationEntity` con anotaciones JPA correctas (prefijos `usr_`) *(Growth: añadir `RefreshTokenEntity`)*
- Repositories : `UserRepository`, `OrganizationRepository` (Spring Data JPA) *(Growth: `RefreshTokenRepository`)*
- DTOs : `UserRegistrationRequest`, `UserLoginRequest`, `UserResponse` (sin campo password), `UserUpdateRequest`, `SearchPreferencesDto`
- Ninguna entidad JPA serializada directamente en respuestas de la API

**Etiquetas :** `epic-1` `backend` `jpa`

---

### [STORY] BE-1.3 — Registro de usuario con consentimiento GDPR

**Resumen :** Endpoint `POST /api/v1/auth/register` que permite a un visitante crear una cuenta con un código de invitación válido.

**DoD Backend :**
- `POST /api/v1/auth/register` (público, sin autenticación requerida)
- Valida : código de invitación existente, email único, contraseña ≥ 8 caracteres, consentimiento marcado
- Contraseña almacenada en **texto plano** (scope demo MVP — usar `NoOpPasswordEncoder`); bcrypt cost factor ≥ 12 diferido a Growth
- Devuelve `201 Created` con `UserResponse` (sin contraseña)
- Código inválido → `400 Bad Request` ProblemDetail
- Email ya existente → `409 Conflict` ProblemDetail
- Validación Jakarta Bean Validation sobre todos los campos
- Registro GDPR : `gdpr_consent = true`, `consent_at = NOW()`
- Tests unitarios + 1 test de integración `@SpringBootTest`

**Etiquetas :** `epic-1` `backend` `auth` `gdpr`

---

### [STORY] BE-1.4 — Login y gestión de sesión HTTP Basic *(JWT + refresh token diferidos a Growth)*

**Resumen :** Endpoint `POST /api/v1/auth/login` con autenticación HTTP Basic para el scope demo MVP. JWT access token + refresh token en cookies HttpOnly están diferidos a Growth.

**DoD Backend :**
- `POST /api/v1/auth/login` (público) — HTTP Basic Auth (credenciales en claro, scope demo MVP)
- Verifica credenciales mediante `AuthenticationManager` de Spring Security con `HttpBasicAuthenticationFilter`
- Login correcto → sesión Spring Security activa, devuelve `200 OK` con `UserResponse`
- `POST /api/v1/auth/logout` → invalida la sesión del servidor → `204 No Content`
- Credenciales inválidas → `401 Unauthorized` sin indicar qué campo es incorrecto
- Cuenta desactivada (`is_active = false`) → `403 Forbidden`
- Tests unitarios sobre `AuthService` + tests de integración login/logout
- *(Growth: JWT access token expiry 8h + refresh token en `usr_refresh_tokens` + cookies `HttpOnly; Secure; SameSite=Strict`)*

**Etiquetas :** `epic-1` `backend` `auth`

---

### [STORY] BE-1.5 — Spring Security : configuración HTTP Basic y RBAC *(JwtAuthenticationFilter diferido a Growth)*

**Resumen :** Configurar Spring Security con HTTP Basic Auth para el MVP demo y aplicar RBAC a nivel de API.

**DoD Backend :**
- `SecurityConfig` (`@Configuration @EnableMethodSecurity`) : configura `HttpSecurity.httpBasic()` para MVP; Spring Security valida las credenciales en cada petición y carga `UserDetails` desde `UserDetailsService`
- Rutas públicas (`/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`), el resto protegido
- `@PreAuthorize("hasRole('ORGANIZATION_ADMIN')")` o `hasAnyRole(...)` sobre los métodos sensibles
- CORS configurado mediante `CorsConfigurationSource` (orígenes desde variable de entorno)
- CSRF desactivado (API REST stateless)
- Tests : acceso no autenticado → 401, rol insuficiente → 403
- *(Growth: reemplazar HTTP Basic con `JwtAuthenticationFilter extends OncePerRequestFilter` que extrae y valida el JWT de la cookie)*

**Etiquetas :** `epic-1` `backend` `security` `rbac`

---

### [STORY] BE-1.6 — Perfil de empleado y preferencias de búsqueda

**Resumen :** Endpoints de consulta y actualización del perfil del usuario autenticado.

**DoD Backend :**
- `GET /api/v1/users/me` → `UserResponse` (sin contraseña)
- `PUT /api/v1/users/me` → actualiza `name` y `search_preferences` JSON ; un intento de modificar el email devuelve `400 Bad Request`
- Todo el texto enviado es saneado server-side (Jsoup o `HtmlUtils.htmlEscape`)
- Tests unitarios sobre `UserService`

**Etiquetas :** `epic-1` `backend` `profile`

---

### [STORY] BE-1.7 — Administración de cuentas (ORGANIZATION_ADMIN)

**Resumen :** CRUD completo de usuarios accesible únicamente al administrador de la organización, con audit log.

**DoD Backend :**
- `GET /api/v1/users` → lista paginada (`Pageable`) de todos los usuarios (rol, estado, email)
- `POST /api/v1/users` → crea una cuenta con el rol especificado
- `PUT /api/v1/users/{id}` → modifica nombre, email, rol
- `PATCH /api/v1/users/{id}/deactivate` → `is_active = false`
- `DELETE /api/v1/users/{id}` → eliminación definitiva + borrado de datos personales (GDPR erasure)
- Todo cambio de rol escrito en `anl_audit_log` (event_type, entity_type, entity_id, actor_id, timestamp)
- EMPLOYEE o TECHNICIAN → `403 Forbidden` sobre todas estas rutas
- Tests unitarios + test de integración

**Etiquetas :** `epic-1` `backend` `admin` `audit`

---

## EPIC 2 — Physical Asset Management

> Gestión de Floors, Rooms, Desks y generación dinámica del mapa (sin almacenamiento SVG en backend).

---

### [STORY] BE-2.1 — Esquema Flyway : tablas `ast_*`

**Resumen :** Migración Flyway para todas las tablas del bloque assets.

**DoD Backend :**
- Migración `V3__assets.sql` crea :
  - `ast_floors` : id, organization_id FK, name, level_number INT, is_active, created_at
  - `ast_rooms` : id, floor_id FK, name, type ENUM(`DESK_AREA`/`MEETING_ROOM`), surface_area_m2 DECIMAL(6,2), energy_managed BOOLEAN, is_open BOOLEAN DEFAULT FALSE, capacity INT, is_active, created_at
  - `ast_desks` : id, room_id FK, name, equipment JSON, display_order INT, status ENUM(`AVAILABLE`/`UNAVAILABLE`), is_active, created_at
- Índices sobre `floor_id`, `room_id`
- `mvn flyway:migrate` pasa sin errores

**Etiquetas :** `epic-2` `backend` `database` `flyway`

---

### [STORY] BE-2.2 — Entidades, repositories y DTOs del bloque Assets

**Resumen :** Implementar las entidades JPA y los DTOs para Floors, Rooms y Desks.

**DoD Backend :**
- Entidades : `FloorEntity`, `RoomEntity`, `DeskEntity` con relaciones JPA (`@ManyToOne`)
- Repositories Spring Data JPA correspondientes
- DTOs : `FloorDto`, `RoomDto`, `DeskDto` + peticiones `CreateFloorRequest`, `CreateRoomRequest`, `CreateDeskRequest`
- Ninguna entidad JPA expuesta directamente en respuestas de la API (usar `ModelMapper` o mapeo manual)

**Etiquetas :** `epic-2` `backend` `jpa`

---

### [STORY] BE-2.3 — CRUD Floors y política de apertura al 80%

**Resumen :** Endpoints de gestión de Floors y lógica de apertura automática de Rooms.

**DoD Backend :**
- `GET /api/v1/floors` → lista paginada
- `POST /api/v1/floors` → 201 Created
- `PUT /api/v1/floors/{id}` → 200 OK
- `PATCH /api/v1/floors/{id}/deactivate` → desactiva el Floor + cascada sobre Rooms y Desks hijos → 204 No Content
- Lógica del 80% : `RoomOpeningService` calcula `reservedDesks / totalDesks` ; si ≥ 80%, la siguiente Room inactiva se abre automáticamente (`is_open = true`) al crear una reserva
- `GET /api/v1/rooms/open-next` (TECHNICIAN + ADMIN) → devuelve la siguiente Room a abrir
- `PATCH /api/v1/rooms/{id}/open` y `PATCH /api/v1/rooms/{id}/close` → override manual por TECHNICIAN
- EMPLOYEE → 403 sobre las rutas de gestión
- Tests unitarios sobre `RoomOpeningService`

**Etiquetas :** `epic-2` `backend` `floors` `rooms`

---

### [STORY] BE-2.4 — CRUD Rooms y Desks

**Resumen :** Endpoints de gestión de Rooms y Desks individuales.

**DoD Backend :**
- `GET/POST /api/v1/rooms` y `PUT/PATCH /api/v1/rooms/{id}`
- `GET/POST /api/v1/desks` y `PUT/PATCH /api/v1/desks/{id}`
- Desactivar un Desk con una reserva activa vigente → `409 Conflict` ProblemDetail
- `GET /api/v1/desks?floorId={id}&available=true` y `GET /api/v1/rooms?floorId={id}&available=true` → filtrado por disponibilidad
- Todas las respuestas son DTOs
- Códigos HTTP correctos : 201 Created, 200 OK, 204 No Content

**Etiquetas :** `epic-2` `backend` `desks` `rooms`

---

### ~~[STORY] BE-2.5 — Upload del plano SVG y asociación de anclas~~ *(ELIMINADA — Ronda 3)*

> **Decisión:** FR11 reemplazado — sin carga de SVG en el backend; el mapa de planta es generado dinámicamente en Angular desde la estructura almacenada (rooms + desks), con posiciones calculadas por `display_order`. FR12 eliminado — la asociación manual de anclas SVG ya no es necesaria.
>
> Los endpoints `GET /api/v1/floors/{id}/rooms` y `GET /api/v1/desks?floorId={id}` (cubiertos en BE-2.3 y BE-2.4) proporcionan los datos de estructura suficientes para que Angular renderice el mapa. Esta story no se implementa en MVP.

---

## EPIC 3 — Reservations, Check-in & Auto-release

> Reserva por turnos, gestión de conflictos, check-in, liberación automática.

---

### [STORY] BE-3.1 — Esquema Flyway : tablas `rsv_*`

**Resumen :** Migración Flyway para todas las tablas del bloque de reservas.

**DoD Backend :**
- Migración `V4__reservations.sql` crea :
  - `rsv_reservations` : id, user_id FK, resource_type ENUM(`DESK`/`ROOM`), resource_id BIGINT, date DATE, shift ENUM(`MORNING`/`AFTERNOON`), status ENUM(`PENDING`/`CONFIRMED`/`CANCELLED`/`RELEASED`), created_at
  - Restricción unique sobre `(resource_type, resource_id, date, shift)` para evitar la doble reserva
- Migración `V5__remote_work.sql` crea :
  - `rsv_remote_work` : id, user_id FK, date DATE, created_at
  - Restricción unique sobre `(user_id, date)`
- `mvn flyway:migrate` pasa sin errores

**Etiquetas :** `epic-3` `backend` `database` `flyway`

---

### [STORY] BE-3.2 — Reserva de Desk/Room y prevención de conflictos

**Resumen :** Endpoint de creación de reserva con todas las reglas de negocio (doble reserva, máximo 7 días, RBAC).

**DoD Backend :**
- `POST /api/v1/reservations` (EMPLOYEE + TECHNICIAN)
  - Valida : recurso disponible, fecha ≤ hoy + 7 días, `user_id` = usuario autenticado únicamente
  - Doble reserva bloqueada por restricción única en BD → `409 Conflict` ProblemDetail
  - Superar 7 días → `400 Bad Request`
  - Intentar reservar para otro usuario → `403 Forbidden`
  - Éxito → `201 Created` con `ReservationDto`
- Respuesta API ≤ 500ms p95 (validar con test de carga básico)
- Tests unitarios sobre `ReservationService` + test de integración con violación de restricción única

**Etiquetas :** `epic-3` `backend` `reservations`

---

### [STORY] BE-3.3 — Mis reservas y cancelación

**Resumen :** Consulta de las reservas del usuario autenticado y cancelación antes del inicio del turno.

**DoD Backend :**
- `GET /api/v1/reservations/me` → lista paginada, ordenada por fecha DESC, con estado
- `DELETE /api/v1/reservations/{id}` → status = `CANCELLED`, recurso status = `AVAILABLE`, en la misma transacción
  - Intentar cancelar la reserva de otro usuario → `403 Forbidden`
  - Intentar cancelar después de la hora de inicio → `409 Conflict`
  - Éxito → `204 No Content`
- Lógica de hora de inicio : MORNING = 08:00, AFTERNOON = 14:00
- Tests unitarios sobre el servicio

**Etiquetas :** `epic-3` `backend` `reservations`

---

### [STORY] BE-3.4 — Check-in desde la aplicación

**Resumen :** Endpoint de confirmación de presencia sobre una reserva activa.

**DoD Backend :**
- `PATCH /api/v1/reservations/{id}/check-in` (solo el owner)
  - Valida : reserva en status `PENDING`, usuario = owner
  - Check-in demasiado temprano (> 15 min antes del inicio) → `400 Bad Request`
  - Reserva no `PENDING` → `409 Conflict`
  - Otro usuario → `403 Forbidden`
  - Éxito → status = `CONFIRMED`, `204 No Content`
- Evento escrito en `anl_audit_log` (event_type = `CHECKIN`, entity_type = `RESERVATION`, actor_id = user_id)
- Tests unitarios sobre `ReservationService#checkIn`

**Etiquetas :** `epic-3` `backend` `check-in` `audit`

---

### [STORY] BE-3.5 — Indicador de teletrabajo y ahorro de CO₂

**Resumen :** Endpoint para marcar una jornada en teletrabajo, utilizado en el cálculo de ahorro de CO₂.

**DoD Backend :**
- `POST /api/v1/reservations/remote-work` → crea una fila en `rsv_remote_work` (user_id, date)
  - Duplicado (mismo user + misma date) → `409 Conflict`
- `GET /api/v1/reservations/remote-work/me?month={YYYY-MM}` → lista de días de teletrabajo del mes
- `DELETE /api/v1/reservations/remote-work/{id}` → elimina la entrada (solo el owner)
- Tests unitarios

**Etiquetas :** `epic-3` `backend` `remote-work`

---

### [STORY] BE-3.6 — Scheduler de auto-liberación de reservas

**Resumen :** Job Spring `@Scheduled` que libera las reservas sin check-in pasado el timeout configurable.

**DoD Backend :**
- `ReservationReleaseScheduler` anotado con `@Scheduled(fixedDelay = 60_000)` en `com.ecotrack.reservations.scheduler`
- Consulta : todas las reservas con `status = PENDING` cuya hora de inicio + `AUTO_RELEASE_MINUTES` (variable de entorno, por defecto 15) haya sido superada
- Para cada reserva elegible (en una sola transacción) : `status = RELEASED`, recurso `status = AVAILABLE`
- Al arrancar, el scheduler procesa las reservas que expiraron durante un downtime del servidor (**sin estado en memoria**)
- `PUT /api/v1/config/auto-release-minutes` (solo ORGANIZATION_ADMIN) → actualiza el valor en runtime via `@ConfigurationProperties`
- Cada liberación escrita en `anl_audit_log` (actor = `SYSTEM`)
- Tests unitarios sobre el scheduler (mock `Clock`)

**Etiquetas :** `epic-3` `backend` `scheduler` `auto-release`

---

### [STORY] BE-3.7 — Gestión de reservas por el Technician

**Resumen :** Permitir al Technician modificar o cancelar cualquier reserva para resolver conflictos.

**DoD Backend :**
- `PATCH /api/v1/reservations/{id}` (TECHNICIAN + ORGANIZATION_ADMIN) → modificación del turno o la fecha
- `DELETE /api/v1/reservations/{id}` ya existente — asegurarse de que TECHNICIAN puede cancelar sin ser el owner
- Operación registrada en `anl_audit_log` con `actor_id` = technician
- Tests unitarios

**Etiquetas :** `epic-3` `backend` `technician`

---

## EPIC 4 — Incidents, Analytics & Data Governance

> Reporte de incidencias, notificaciones SSE en tiempo real, dashboard, exportación, GDPR.

---

### [STORY] BE-4.1 — Esquema Flyway : tablas `anl_*`

**Resumen :** Migración Flyway para las tablas de Incidents, Analytics y audit log.

**DoD Backend :**
- Migración `V6__incidents.sql` crea :
  - `anl_incidents` : id, resource_type, resource_id BIGINT, reported_by FK → `usr_users`, description TEXT, photo_path VARCHAR, status ENUM(`OPEN`/`IN_PROGRESS`/`RESOLVED`), created_at, resolved_at, resolved_by FK
- Migración `V7__audit_log.sql` crea :
  - `anl_audit_log` : id, event_type VARCHAR, entity_type VARCHAR, entity_id BIGINT, actor VARCHAR (user_id o `SYSTEM`), timestamp DATETIME
  - Sin FK sobre `actor` para permitir el valor `SYSTEM`
- `mvn flyway:migrate` pasa sin errores

**Etiquetas :** `epic-4` `backend` `database` `flyway`

---

### [STORY] BE-4.2 — Reporte de incidencia y bloqueo automático del recurso

**Resumen :** Endpoint de creación de incidencia con upload de foto asíncrono y bloqueo inmediato del recurso.

**DoD Backend :**
- `POST /api/v1/incidents` (todos los roles autenticados) : multipart/form-data (description + foto opcional)
  - Validación MIME de la foto : `image/jpeg`, `image/png`, `image/webp` ; máximo 5 MB
  - Upload almacenado en `{UPLOAD_DIR}/incidents/{incidentId}` de forma **asíncrona** (`@Async` + `CompletableFuture`)
  - La API devuelve `201 Created` inmediatamente, sin esperar la escritura del fichero
  - En la misma transacción BD : incidencia creada, recurso `status = UNAVAILABLE`
- Todo el texto enviado es saneado server-side
- Incidencia escrita en `anl_audit_log`
- Tests unitarios sobre `IncidentService` (mock del almacenamiento de ficheros)

**Etiquetas :** `epic-4` `backend` `incidents` `upload` `security`

---

### [STORY] BE-4.3 — Notificación SSE en tiempo real a los Technicians

**Resumen :** Enviar una notificación SSE a todos los Technicians conectados cuando se crea una nueva incidencia.

**DoD Backend :**
- `SseEmitterRegistry` (singleton Spring) en `com.ecotrack.common.sse` : gestiona la lista de `SseEmitter` activos por rol/userId
- `GET /api/v1/sse/notifications` (solo TECHNICIAN) : crea un `SseEmitter` (timeout = 5 min), lo registra y devuelve el flujo
- Al crear una incidencia : `IncidentService` llama a `SseEmitterRegistry#broadcast(TECHNICIAN, event)`
  - Payload : `{incidentId, resourceId, resourceType, location, description, timestamp}`
  - Event name : `INCIDENT_CREATED`
- Gestión de emitters cerrados/expirados : eliminación automática en caso de excepción al enviar
- Tests unitarios sobre `SseEmitterRegistry`

**Etiquetas :** `epic-4` `backend` `sse` `notifications`

---

### [STORY] BE-4.4 — Ciclo de vida de incidencias (Technician)

**Resumen :** Endpoint para avanzar el estado de una incidencia hasta su resolución.

**DoD Backend :**
- `GET /api/v1/incidents` (TECHNICIAN + ORGANIZATION_ADMIN) → lista paginada, filtrable por `status`, `resourceId`, rango de fechas
- `PATCH /api/v1/incidents/{id}/status` (TECHNICIAN) → transiciones : `OPEN → IN_PROGRESS → RESOLVED`
  - Transición inválida → `400 Bad Request` ProblemDetail
  - Resolución : `status = RESOLVED`, recurso `status = AVAILABLE`, `resolved_at = NOW()`, `resolved_by = userId` — en una sola transacción
- Cada transición escrita en `anl_audit_log`
- EMPLOYEE → 403 sobre estos endpoints
- Tests unitarios sobre las transiciones

**Etiquetas :** `epic-4` `backend` `incidents`

---

### [STORY] BE-4.5 — Vista de administración de incidencias y foto

**Resumen :** Endpoint para servir la foto de una incidencia y acceso completo al ORGANIZATION_ADMIN.

**DoD Backend :**
- `GET /api/v1/incidents/{id}/photo` : sirve el fichero foto con el `Content-Type` header correcto
  - Si no hay foto → `404 Not Found`
- `GET /api/v1/incidents` ya implementado en la tarea 4.4, asegurarse de que ORGANIZATION_ADMIN tiene acceso
- Vista de administración incluye : nombre y ubicación del recurso, nombre del reportador, descripción, estado, created_at, resolved_at, duración de resolución, nombre del resolved_by
- Tests unitarios

**Etiquetas :** `epic-4` `backend` `incidents` `admin`

---

### [STORY] BE-4.6 — Dashboard de ocupación (Analytics)

**Resumen :** Endpoint de análisis de ocupación por zona para el día actual o la semana.

**DoD Backend :**
- `GET /api/v1/analytics/occupancy?date={YYYY-MM-DD}&granularity={day|week}` (solo ORGANIZATION_ADMIN)
  - Por zona : total de reservas, check-ins confirmados, tasa de check-in (%), hora punta
  - Tasa : `COUNT(status=CONFIRMED) / COUNT(*)` sobre el período
  - Consultas JPQL o nativas optimizadas (sin N+1)
- Tests unitarios sobre los cálculos de Analytics (datos mockeados)

**Etiquetas :** `epic-4` `backend` `analytics`

---

### [STORY] BE-4.7 — Sugerencias de consolidación de zonas

**Resumen :** Algoritmo de sugerencia de cierre de zonas con baja ocupación.

**DoD Backend :**
- `GET /api/v1/analytics/consolidation-suggestions?date={YYYY-MM-DD}` (ORGANIZATION_ADMIN)
  - Identifica las zonas con menos reservas
  - Recomienda las zonas a mantener activas (las más ocupadas)
  - Devuelve : zonas recomendadas activas, zonas a cerrar, número de empleados afectados por zona
- Umbral configurable via variable de entorno `CONSOLIDATION_THRESHOLD_PERCENT` (por defecto: 20%)
- `PUT /api/v1/config/consolidation-threshold` (ORGANIZATION_ADMIN) → actualización en runtime
- Cada consulta al panel registrada en `anl_audit_log`
- Tests unitarios sobre el algoritmo

**Etiquetas :** `epic-4` `backend` `analytics` `consolidation`

---

### [STORY] BE-4.8 — Exportación de informe CSV y datos personales GDPR

**Resumen :** Exportación CSV de ocupación y exportación JSON de datos personales según el RGPD.

**DoD Backend :**
- `GET /api/v1/analytics/export?from={date}&to={date}&format=csv` (ORGANIZATION_ADMIN)
  - Generación CSV : fecha, zona, total reservas, check-ins confirmados, tasa de check-in, incidencias abiertas
  - Header HTTP : `Content-Disposition: attachment; filename=occupancy-{from}-{to}.csv`
  - Librería : OpenCSV o `StringBuilder` (sin dependencia pesada)
- `GET /api/v1/users/me/export` (EMPLOYEE — GDPR Artículo 20)
  - JSON : perfil completo, todas las reservas, preferencias de búsqueda
  - Header HTTP : `Content-Disposition: attachment; filename=my-data.json`
- Tests unitarios sobre los servicios de exportación

**Etiquetas :** `epic-4` `backend` `analytics` `gdpr` `export`

---

### [STORY] BE-4.9 — Retención de datos y purga GDPR

**Resumen :** Configuración del período de retención y job de pseudonimización/purga automática.

**DoD Backend :**
- `PUT /api/v1/config/data-retention-months` (ORGANIZATION_ADMIN) → modifica el período (por defecto: 12 meses)
- Job `@Scheduled` (cron diario) : anonimiza los registros `rsv_reservations` con `created_at < NOW() - retention_months`
  - Pseudonimización : reemplaza `user_id` por un token determinista no reversible (hash SHA-256 + sal fija)
- `GET /api/v1/audit-log` (ORGANIZATION_ADMIN) → paginado, las entradas `anl_audit_log` se conservan ≥ 90 días independientemente del parámetro general de retención
- Toda ejecución de purga escrita en `anl_audit_log` (actor = `SYSTEM`)
- Tests unitarios sobre el job de purga

**Etiquetas :** `epic-4` `backend` `gdpr` `scheduler`

---

## Tareas transversales (Cross-cutting)

---

### [TASK] BE-X.1 — AuditLogService centralizado

**Resumen :** Crear un servicio `AuditLogService` compartido en `com.ecotrack.common` para todas las escrituras en el audit log.

**DoD Backend :**
- `AuditLogService#log(eventType, entityType, entityId, actor)` persiste en `anl_audit_log`
- Utilizado por todos los servicios que emiten eventos (User, Reservation, Incident, Scheduler)
- Método no bloqueante (llamada `@Async` para no ralentizar la transacción principal)
- Tests unitarios

**Etiquetas :** `backend` `cross-cutting` `audit`

---

### [TASK] BE-X.2 — Headers de seguridad HTTP y configuración CORS

**Resumen :** Verificar que todos los headers de seguridad HTTP estén correctamente configurados para producción.

**DoD Backend :**
- `Content-Security-Policy`, `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff` añadidos via `HeaderWriter` de Spring Security
- CORS : solo `CORS_ALLOWED_ORIGINS` (variable de entorno) está permitido — sin wildcard `*`
- TLS aplicado mediante configuración de Tomcat (certificado via variable de entorno, o documentación para reverse proxy)
- Tests : verificar los headers en los tests de integración

**Etiquetas :** `backend` `cross-cutting` `security`

---

### [TASK] BE-X.3 — Tests de integración globales y cobertura

**Resumen :** Asegurar una cobertura de tests suficiente sobre los flujos críticos end-to-end.

**DoD Backend :**
- `@SpringBootTest` con `@ActiveProfiles("test")` + H2 en memoria (o `Testcontainers` MySQL)
- Tests de integración que cubren : registro → login → reserva → check-in → cancelación
- Tests del `GlobalExceptionHandler` para cada tipo de error
- Cobertura unitaria ≥ 70% sobre las clases de servicio
- `mvn verify` pasa sin errores en el CI

**Etiquetas :** `backend` `cross-cutting` `tests` `ci`

---

## Resumen de Stories por Epic

| Epic | Story | Etiquetas clave |
|------|-------|-----------------|
| Epic 0 | BE-0.1 Inicializar Spring Boot | setup |
| Epic 0 | BE-0.2 Flyway baseline | database |
| Epic 0 | BE-0.3 GlobalExceptionHandler | error-handling |
| Epic 0 | BE-0.4 OpenAPI/Swagger | openapi |
| Epic 0 | BE-0.5 Docker Compose | devops |
| Epic 1 | BE-1.1 Esquema `usr_*` | database |
| Epic 1 | BE-1.2 Entidades & DTOs Users | jpa |
| Epic 1 | BE-1.3 Registro GDPR | auth, gdpr |
| Epic 1 | BE-1.4 Login HTTP Basic (JWT diferido a Growth) | auth |
| Epic 1 | BE-1.5 Spring Security HTTP Basic + RBAC | security |
| Epic 1 | BE-1.6 Perfil y preferencias | profile |
| Epic 1 | BE-1.7 Admin usuarios + audit | admin |
| Epic 2 | BE-2.1 Esquema `ast_*` | database |
| Epic 2 | BE-2.2 Entidades & DTOs Assets | jpa |
| Epic 2 | BE-2.3 CRUD Floors + regla 80% | floors |
| Epic 2 | BE-2.4 CRUD Rooms & Desks | desks |
| Epic 2 | ~~BE-2.5 Upload SVG + anclas~~ *(ELIMINADA — Ronda 3)* | — |
| Epic 3 | BE-3.1 Esquema `rsv_*` | database |
| Epic 3 | BE-3.2 Reserva + anti-conflicto | reservations |
| Epic 3 | BE-3.3 Mis reservas + cancelación | reservations |
| Epic 3 | BE-3.4 Check-in en app | check-in |
| Epic 3 | BE-3.5 Teletrabajo CO₂ | remote-work |
| Epic 3 | BE-3.6 Scheduler auto-release | scheduler |
| Epic 3 | BE-3.7 Gestión Technician | technician |
| Epic 4 | BE-4.1 Esquema `anl_*` | database |
| Epic 4 | BE-4.2 Reporte incidencia + upload | incidents |
| Epic 4 | BE-4.3 SSE Technicians | sse |
| Epic 4 | BE-4.4 Ciclo vida incidencia | incidents |
| Epic 4 | BE-4.5 Vista admin incidencias | admin |
| Epic 4 | BE-4.6 Dashboard ocupación | analytics |
| Epic 4 | BE-4.7 Sugerencias consolidación | analytics |
| Epic 4 | BE-4.8 Exportación CSV + GDPR | export, gdpr |
| Epic 4 | BE-4.9 Retención y purga GDPR | gdpr, scheduler |
| Cross | BE-X.1 AuditLogService | cross-cutting |
| Cross | BE-X.2 Headers de seguridad | security |
| Cross | BE-X.3 Tests de integración | tests |

---

*Generado el 7 de mayo de 2026 — EcoTrack Office*
