# Requisitos Funcionales — EcoTrack Office

> 43 requisitos funcionales, organizados por bloque.

## Contenido

- [Contenido](#contenido)
- [Bloque 1 — Usuarios y Autenticación](#bloque-1--usuarios-y-autenticación)
- [Bloque 2 — Recursos Físicos y Mapa](#bloque-2--recursos-físicos-y-mapa)
- [Bloque 3 — Reservas y Check-in](#bloque-3--reservas-y-check-in)
- [Bloque 4 — Incidencias y Analítica](#bloque-4--incidencias-y-analítica)
- [Requisitos No Funcionales clave](#requisitos-no-funcionales-clave)

---

## Bloque 1 — Usuarios y Autenticación

| # | Requisito |
|---|---|
| FR1 | Un visitante puede registrarse con nombre, email, contraseña y **código de invitación de empresa** |
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
| FR9 | El Administrador puede crear, actualizar y desactivar **salas** en una planta (tipo: Escritorios / Sala de reunión, superficie m², indicador energético) |
| FR9b | El sistema aplica una **política de apertura por umbral del 80%**: una sala o planta se abre automáticamente al superar el umbral; el Técnico puede abrir/cerrar manualmente |
| FR10 | El Administrador puede gestionar escritorios y salas (equipamiento, capacidad) |
| FR11 | El mapa de planta es **generado automáticamente** por Angular a partir de los datos de salas y escritorios devueltos por la API; no se sube ningún fichero SVG |
| FR12 | La posición de cada tile de sala y marcador de escritorio en el mapa es calculada automáticamente por el componente Angular según el orden devuelto por la API |
| FR13 | Un Empleado puede ver cualquier planta como **mapa de tiles rectangulares** de salas con código de colores en tiempo real (disponible / reservado / cerrado) |
| FR14 | Al hacer clic en el tile de una sala, el mapa muestra el estado en tiempo real de cada escritorio dentro de esa sala (disponible / reservado / avería) |
| FR15 | Al hacer clic en un tile de sala, el sistema muestra el **drill-down**: escritorios disponibles y botón de reserva directo |
| FR16 | *(Post-MVP — Growth)* Un Empleado puede filtrar el mapa por criterios (cercanía a aseos, ventana, equipamiento) |
| FR17 | Un Empleado puede ver los espacios disponibles en formato lista/tabla |
| FR18 | Un Empleado puede ver la ficha de detalle de un escritorio o sala |

---

## Bloque 3 — Reservas y Check-in

| # | Requisito |
|---|---|
| FR19 | Un Empleado puede reservar un escritorio o sala disponible para una fecha eligiendo **turno** (Mañana 08:00–14:00 / Tarde 14:00–20:00), con un límite de **7 días de antelación** |
| FR19b | El sistema impide reservar un recurso en un turno ya completo o en una sala/planta cerrada |
| FR20 | El sistema impide la doble reserva del mismo recurso en el mismo horario |
| FR21 | Un Empleado puede ver sus reservas futuras y pasadas |
| FR22 | Un Empleado puede cancelar su reserva antes de la hora de inicio |
| FR22b | Un **Técnico** puede modificar o cancelar cualquier reserva (sin notificación al empleado en la demo) |
| FR23 | El sistema guarda los últimos criterios de búsqueda y los rellena automáticamente |
| FR23b | Un Empleado puede indicar si trabaja en remoto ese día (no genera reserva pero registra su ausencia para el cálculo de CO₂) |
| FR24 | Un Empleado puede confirmar el check-in **confirmando su asistencia en la app** (un solo toque; sin QR ni enlace externo) |
| FR25 | *(Post-MVP — Growth)* Un Empleado puede confirmar el check-in mediante un enlace de email (sin sesión activa) |
| FR26 | El sistema libera automáticamente una reserva si no se confirma el check-in en 15 minutos (configurable) |
| FR27 | *(Post-MVP — Growth)* El sistema envía un recordatorio al Empleado 10 minutos antes de la liberación automática |
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
| FR35 | El Administrador puede ver el total de reservas por sala del día actual y de la semana |
| FR36 | El Administrador puede ver la tasa de ocupación (check-ins confirmados / total reservas) por sala |
| FR37 | El sistema genera una sugerencia de consolidación de salas cuando la asistencia es baja |
| FR38 | *(Post-MVP — Growth)* El Administrador puede enviar una notificación a los Empleados en una sala recomendada para cerrar |
| FR39 | El Administrador puede exportar un informe de ocupación para un rango de fechas |
| FR40b | El dashboard muestra el **ahorro energético estimado**: `superficie_m² × coste_por_m²_por_día × días_cierre` |
| FR40c | El dashboard muestra el **ahorro de CO₂ estimado** basado en los registros de trabajo remoto |

---

## Requisitos No Funcionales clave

| Categoría | Requisito |
|---|---|
| **Rendimiento** | Carga inicial ≤ 3s · Respuestas API ≤ 500ms (p95) · Polling cada 30s |
| **Seguridad** | HTTPS/TLS 1.2+ · bcrypt (factor 12) · JWT en cookies HttpOnly/Secure/SameSite=Strict |
| **Seguridad** | Roles aplicados en la API (Spring Security) · Validación MIME de fotos · Sanitización de textos |
| **Escalabilidad** | 500 usuarios concurrentes · Sin límites configurados en salas/plantas/escritorios |
| **Fiabilidad** | 99% disponibilidad (lunes–viernes, 7-20h) · Degradación sin bloqueo si la API no responde |
| **GDPR** | Consentimiento explícito · Derecho de acceso y borrado · Retención 12 meses · Auditoría 90 días |
