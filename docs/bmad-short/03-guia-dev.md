# Guía de desarrollo — EcoTrack Office

---

## Todo-list por sprint

### Sprint 1 — Fundación (todos juntos)
- [ ] **0.1** Inicializar workspace Angular (routing, SCSS, strict, Material)
- [ ] **0.2** Inicializar proyecto Spring Boot (dependencias del pom.xml)
- [ ] **0.3** Configurar Docker Compose (MySQL 8 + MailHog)
- [ ] Validar que el frontend llama al backend y recibe respuesta

### Sprint 2 — Autenticación
- [ ] **1.1** Registro de usuario con consentimiento GDPR (frontend + API + Flyway V1)
- [ ] **1.2** Login, sesión JWT, logout (Spring Security + cookies HttpOnly)

### Sprint 3 — Perfil + Base de activos
- [ ] **1.3** Perfil del empleado y preferencias de búsqueda
- [ ] **1.4** Panel de administración de usuarios (Admin)
- [ ] **2.1** Gestión de plantas y zonas (Flyway V2)

### Sprint 4 — Gestión de recursos físicos
- [ ] **2.2** CRUD de escritorios y salas de reunión
- [ ] **2.3** Subida del plano SVG y asociación de posiciones

### Sprint 5 — Mapa interactivo ⚠️ SPRINT CRÍTICO
- [ ] **2.4** Visor del mapa SVG con estado en tiempo real por color
- [ ] **2.5** Capa de calor por zonas, filtros y vista lista
> Los Sprints 6 y 8 están bloqueados hasta que 2.4 esté completo.

### Sprint 6 — Reservas
- [ ] **3.1** Reserva de escritorio/sala con prevención de doble reserva (Flyway V3)
- [ ] **3.2** Mis reservas y cancelación

### Sprint 7 — Check-in y liberación automática
- [ ] **3.3** Generación QR y check-in en escritorio
- [ ] **3.4** Check-in por enlace de email y recordatorio pre-liberación
- [ ] **3.5** Scheduler de liberación automática (récupère l'état depuis la DB au redémarrage)

### Sprint 8 — Incidencias
- [ ] **4.1** Notificación de incidencia + bloqueo automático del recurso (Flyway V4)
- [ ] **4.2** Notificación en tiempo real al técnico (SSE) + ciclo de vida de incidencia
- [ ] **4.3** Vista de incidencias para el Administrador

### Sprint 9 — Analítica y Gobernanza
- [ ] **4.4** Dashboard de ocupación (reservas + check-ins por zona)
- [ ] **4.5** Sugerencias de consolidación de zonas + notificaciones a empleados
- [ ] **4.6** Exportar informes y gestión GDPR (exportación de datos, auditoría)

---

## Convenciones de nomenclatura

### Base de datos (MySQL)
```sql
-- Tablas: snake_case con prefijo de bloque
usr_users, ast_desks, rsv_reservations, anl_incidents

-- Columnas: snake_case
user_id, created_at, is_active, auto_release_minutes

-- Claves foráneas: nombre_columna (nunca fk_user, nunca userId)
zone_id, user_id

-- Índices
idx_{tabla}_{columna}  →  idx_usr_users_email
```

### API REST
```
GET    /api/v1/users
GET    /api/v1/floors/{id}/desks
POST   /api/v1/reservations
PATCH  /api/v1/reservations/{id}/check-in
POST   /api/v1/incidents/{id}/resolve
```
Regla: **plural** para colecciones · **verbo** para acciones (`/check-in`, `/resolve`, `/release`)

### Java (Spring Boot)
```java
// Clases
UserService, ReservationController, DeskRepository

// Métodos
findByEmail(), createReservation(), releaseExpiredReservations()

// Paquetes
com.ecotrack.users.controller
com.ecotrack.users.service
com.ecotrack.users.repository
com.ecotrack.users.model
com.ecotrack.users.dto
```

### TypeScript / Angular
```typescript
// Componentes (PascalCase)
FloorMapComponent, ReservationFormComponent

// Servicios
ReservationService, AuthService, FloorMapService

// Archivos (kebab-case)
floor-map.component.ts, reservation.service.ts, auth.interceptor.ts

// Interfaces (sin prefijo I)
Reservation, Desk, UserProfile

// Enums
ReservationStatus.CONFIRMED, IncidentStatus.OPEN
```

---

## Procedimientos Git

### Configuración inicial (una vez)
```bash
git clone <url-del-repo>
cd ecotrack-office
git checkout -b feature/bloque-1-auth    # adaptar al número de bloque
```

### Flujo de trabajo diario
```bash
# 1. Sincronizar con main antes de empezar
git checkout main
git pull origin main

# 2. Crear o cambiar a tu rama de feature
git checkout feature/bloque-1-auth

# 3. Hacer commits pequeños y descriptivos
git add .
git commit -m "feat: implement user registration endpoint"

# 4. Actualizar tu rama con los cambios de main
git rebase main

# 5. Push y abrir Pull Request
git push origin feature/bloque-1-auth
```

### Formato de commits (Conventional Commits)
```
feat:     nueva funcionalidad
fix:      corrección de bug
chore:    tareas de mantenimiento (dependencias, config)
docs:     documentación
refactor: refactorización sin cambio de comportamiento
test:     añadir o modificar tests
```

Ejemplos:
```
feat: add JWT authentication filter
fix: correct auto-release scheduler recovery on restart
chore: add springdoc-openapi dependency
test: add unit tests for ReservationService
```

### Reglas del equipo
- **Nunca** hacer push directamente a `main`
- Cada historia → 1 rama → 1 Pull Request
- El PR debe pasar los tests antes de hacer merge
- Coordinar los números de versión de Flyway **antes** de crear la migración
