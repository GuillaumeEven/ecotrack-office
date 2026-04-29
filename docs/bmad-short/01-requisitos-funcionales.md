# Requisitos Funcionales — EcoTrack Office

> 43 requisitos funcionales, organizados por bloque.

---

## Bloque 1 — Usuarios y Autenticación

| # | Requisito |
|---|---|
| FR1 | Un visitante puede registrarse con nombre, email y contraseña |
| FR2 | Un usuario registrado puede iniciar sesión y recibir un token válido por 8 horas |
| FR3 | Un usuario autenticado puede cerrar sesión e invalidar su sesión |
| FR4 | El Administrador puede crear, actualizar, desactivar y eliminar cuentas de usuario |
| FR5 | El Administrador puede asignar roles (Empleado, Administrador, Técnico) |
| FR6 | El sistema aplica el control de acceso por roles en la API (no solo en el frontend) |
| FR7 | Un Empleado puede ver y actualizar su perfil y sus preferencias de búsqueda guardadas |
| FR40 | El sistema muestra un aviso de privacidad y pide consentimiento explícito al registrarse |
| FR41 | Un Empleado puede solicitar la exportación de sus datos personales |
| FR42 | El Administrador puede configurar el período de retención de datos (por defecto: 12 meses) |
| FR43 | El sistema mantiene un registro de auditoría de todos los eventos de reserva e incidencia |

---

## Bloque 2 — Recursos Físicos y Mapa

| # | Requisito |
|---|---|
| FR8 | El Administrador puede crear, actualizar y desactivar plantas del edificio |
| FR9 | El Administrador puede crear, actualizar y desactivar zonas en una planta (con indicador energético) |
| FR10 | El Administrador puede gestionar escritorios y salas (equipamiento, capacidad, posición en el plano) |
| FR11 | El Administrador puede subir y reemplazar el plano SVG de cada planta |
| FR12 | El Administrador puede asociar escritorios y salas a sus posiciones SVG en el plano |
| FR13 | Un Empleado puede ver cualquier planta como mapa SVG interactivo |
| FR14 | El mapa muestra el estado de cada recurso en tiempo real con código de colores (disponible / reservado / avería) |
| FR15 | El mapa muestra una capa de calor por zonas para fomentar la agrupación energética |
| FR16 | Un Empleado puede filtrar el mapa por criterios (cercanía a aseos, ventana, equipamiento) |
| FR17 | Un Empleado puede ver los espacios disponibles en formato lista/tabla |
| FR18 | Un Empleado puede ver la ficha de detalle de un escritorio o sala |

---

## Bloque 3 — Reservas y Check-in

| # | Requisito |
|---|---|
| FR19 | Un Empleado puede reservar un escritorio o sala disponible para una fecha y franja horaria |
| FR20 | El sistema impide la doble reserva del mismo recurso en el mismo horario |
| FR21 | Un Empleado puede ver sus reservas futuras y pasadas |
| FR22 | Un Empleado puede cancelar su reserva antes de la hora de inicio |
| FR23 | El sistema guarda los últimos criterios de búsqueda y los rellena automáticamente |
| FR24 | Un Empleado puede confirmar el check-in escaneando el código QR del escritorio |
| FR25 | Un Empleado puede confirmar el check-in mediante un enlace de email (sin sesión activa) |
| FR26 | El sistema libera automáticamente una reserva si no se confirma el check-in en 15 minutos (configurable) |
| FR27 | El sistema envía un recordatorio al Empleado 10 minutos antes de la liberación automática |
| FR28 | Un escritorio liberado queda disponible inmediatamente para nuevas reservas |

---

## Bloque 4 — Incidencias y Analítica

| # | Requisito |
|---|---|
| FR29 | Cualquier usuario autenticado puede reportar una incidencia con descripción y foto opcional |
| FR30 | Al registrar una incidencia, el sistema bloquea automáticamente el recurso afectado |
| FR31 | El sistema envía una notificación push inmediata a todos los Técnicos cuando se registra una incidencia |
| FR32 | Un Técnico puede actualizar el estado de la incidencia (abierta → en curso → resuelta) |
| FR33 | Cuando un Técnico marca la incidencia como resuelta, el recurso vuelve a estar disponible |
| FR34 | El Administrador puede ver todas las incidencias con estado, tiempo de resolución y técnico asignado |
| FR35 | El Administrador puede ver el total de reservas por zona del día actual y de la semana |
| FR36 | El Administrador puede ver la tasa de ocupación (check-ins confirmados / total reservas) por zona |
| FR37 | El sistema genera una sugerencia de consolidación de zonas cuando la asistencia es baja |
| FR38 | El Administrador puede enviar una notificación a los Empleados en una zona recomendada para cerrar |
| FR39 | El Administrador puede exportar un informe de ocupación para un rango de fechas |

---

## Requisitos No Funcionales clave

| Categoría | Requisito |
|---|---|
| **Rendimiento** | Carga inicial ≤ 3s · Respuestas API ≤ 500ms (p95) · Polling cada 30s |
| **Seguridad** | HTTPS/TLS 1.2+ · bcrypt (factor 12) · JWT en cookies HttpOnly/Secure/SameSite=Strict |
| **Seguridad** | Roles aplicados en la API (Spring Security) · Validación MIME de fotos · Sanitización de textos |
| **Escalabilidad** | 500 usuarios concurrentes · Sin límites configurados en zonas/plantas/escritorios |
| **Fiabilidad** | 99% disponibilidad (lunes–viernes, 7-20h) · Degradación sin bloqueo si la API no responde |
| **GDPR** | Consentimiento explícito · Derecho de acceso y borrado · Retención 12 meses · Auditoría 90 días |
