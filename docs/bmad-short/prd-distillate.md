---
type: bmad-distillate
sources:
  - "prd.md"
downstream_consumer: "general"
created: "2026-05-19"
token_estimate: 3420
parts: 1
---

## Identidad del Proyecto
- EcoTrack Office: gestión inteligente de espacios de trabajo para oficinas híbridas; Angular SPA + Spring Boot REST API + MySQL; greenfield; complejidad media; aplicación web / dominio general
- Autor: Sensei; PRD completado 2026-04-10; última edición 2026-05-03; entrega académica de 8 semanas (4 estudiantes × 1 bloque CRUD cada uno)
- Dos patrones distintivos: (1) reserva centrada en el mapa con empujón conductual (nudging) (el plano de planta como superficie de interacción principal; superposiciones codificadas por color que hacen que el agrupamiento eficiente en energía sea la opción obvia sin imponerlo); (2) automatización tolerante a la fricción (liberación automática, check-in desde la app, sugerencias de zonas como recomendaciones — la herramienta se adapta a la no conformidad en lugar de imponerla)

## Usuarios y Personas
- Empleado (Guillermo, 34, I+D): reserva mediante el mapa de planta con filtros; flujo ideal; necesita que la reserva tome ≤60 s; preferencias persistentes
- Empleado (Eduardo, 41, ventas): no cumple con el flujo, nunca confirma las reservas; liberación automática + check-in por correo electrónico como alternativa diseñada para él
- Técnico (Raimundo, 52, conserje/mantenimiento): recibe notificaciones push para incidentes; puede modificar/cancelar cualquier reserva; gestiona el ciclo de vida de los incidentes
- Administrador de la Organización (José Luis, 48, jefe de servicios generales): panel de control, consolidación de zonas, informes de energía/CO₂, gestión de activos del edificio, gestión de usuarios, configuración del tenant

## Criterios de Éxito
- Empleado: reservar en ≤60 s; sin conflictos ocupado/reservado; la liberación automática recupera las ausencias sin intervención del personal
- Administrador de la Organización: recuperación manual de escritorios fantasma = 0; informes sin extracción manual; exclusión de incidentes en <2 min
- Negocio: tasa de escritorios fantasma <5%; eficiencia de consolidación de zonas ≥80% en días de baja asistencia; ≥40% de los días de baja asistencia resultan en ≥1 zona desactivada
- Técnico: 4 módulos CRUD entregados e integrados en 8 semanas; la autenticación basada en sesión aplica separación de roles; 4 bloques funcionales desplegables de forma independiente

## Fases
- MVP (8 semanas): autenticación de usuario + registro con código de empresa; gestión de activos físicos; mapa de planta; reserva por turnos; check-in/liberación automática; gestión de incidentes; panel analítico; consolidación de zonas
- Crecimiento (post-MVP): filtros avanzados de mapa (FR16); check-in por correo electrónico (FR25); recordatorio de pre-liberación (FR27); notificación de cierre de zona a empleados afectados (FR38); informes exportables (PDF/CSV); reservas recurrentes; sincronización de calendarios; actualizaciones del mapa en tiempo real via WebSocket
- Visión (futuro): exportación CO₂/ESG; móvil nativo (Android/iOS vía Capacitor); integración HVAC/iluminación; modelado predictivo de ocupación; soporte multi-edificio

## Bloques de Desarrollo (4 estudiantes × 8 semanas)
- Bloque 1 (fundacional, sin dependencias): Usuarios + roles + autenticación por sesión
- Bloque 2 (requiere Bloque 1): Plantas + salas (área de puestos / sala de reuniones, m²) + puestos + plano de planta SVG + política de apertura al 80% de capacidad
- Bloque 3 (requiere Bloques 1+2): Reservas (por turnos mañana/tarde) + check-in + liberación automática + indicador de teletrabajo
- Bloque 4 (requiere Bloque 3): Panel analítico + estimaciones de ahorro energético/CO₂ + ciclo de vida de incidentes
- Hito de integración: Semana 6–7; contrato OpenAPI redactado en la Semana 1; biblioteca compartida de Angular Material Semana 1; mocks de API desde Semana 2; pruebas de integración empiezan Semana 6

## FR — Gestión de Usuarios y Autenticación
- FR1: registro con nombre + correo electrónico + contraseña + código de invitación de la empresa (requerido; no hay auto-registro abierto; vincula la cuenta a una organización existente)
- FR2: inicio de sesión → token de sesión JWT válido 8 h
- FR3: cierre de sesión → invalidar sesión
- FR4: OrgAdmin CRUD de cuentas de usuario
- FR5: OrgAdmin asigna/cambia roles (Empleado / OrgAdmin / Técnico)
- FR6: RBAC aplicado a nivel de API — Empleado (solo autoservicio), OrgAdmin (administración completa), Técnico (solo incidentes)
- FR7: Empleado ve/actualiza su propio perfil + preferencias de búsqueda guardadas

## FR — Activos Físicos
- FR8: OrgAdmin CRUD de plantas; vistas administrativas dedicadas Crear Planta / Crear Sala / Crear Puesto
- FR9: OrgAdmin CRUD de salas por planta; tipos: área de puestos o sala de reuniones; cada una tiene bandera de gestión energética + superficie en m² (requerido para el cálculo de ahorros)
- FR9b: política de apertura al 80% de capacidad — una nueva sala/planta se abre solo cuando la capacidad activa actual alcanza el 80% (umbral configurable); el Técnico puede abrir/cerrar manualmente cualquier sala en cualquier momento
- FR10: OrgAdmin CRUD de puestos con atributos de equipamiento (sillas, mesas, TV, etc.) + posición en el plano de planta
- FR11: El mapa de planta para cada planta se renderiza dinámicamente en el frontend a partir de los datos almacenados de estructura planta/sala/puesto; posiciones derivadas del orden de creación; no hay carga de archivos SVG ni almacenamiento de SVG en el backend

## FR — Mapa de Planta Interactivo
- FR13: Empleado ve el edificio como mapa interactivo con mosaicos rectangulares (plantas/salas); las salas no disponibles (incidente, cerrada) son visualmente distintas y no interactivas
- FR14: estado codificado por color en tiempo real por recurso (disponible / reservado / no disponible/incidente)
- FR15: clic en mosaico de sala → vista a nivel de sala con puestos individuales; tooltip en cada puesto → reserva por turno (mañana/tarde) con un solo clic; el desglose de sala incluye detalle del puesto
- FR16: (nice-to-have post-MVP) filtrar mapa por proximidad a baños, orientación a la ventana, tipo de equipamiento
- FR17: vista alternativa lista/tabla siempre disponible (también sirve para conexiones de baja banda)
- FR18: tarjeta de detalle para cualquier puesto/sala (equipamiento, capacidad, zona, estado actual)

## FR — Reservas
- FR19: reservar puesto o sala de reuniones por fecha + turno (mañana 08:00–14:00 / tarde 14:00–20:00); granularidad por turno únicamente (no horas arbitrarias)
- FR19b: límite de reserva con 7 días de antelación (configurable por OrgAdmin; valor provisional por defecto)
- FR20: prevención de doble reserva para el mismo recurso + mismo turno
- FR21: Empleado ve reservas próximas + pasadas
- FR22: Empleado cancela su propia reserva antes de la hora de inicio
- FR22b: Técnico modifica/cancela cualquier reserva para resolver conflictos; sin notificación automática a empleados afectados en el alcance del demo
- FR23: el sistema guarda el último criterio de búsqueda usado; rellena previamente en la próxima visita
- FR23b: Empleado marca el día laboral como remoto; registrado + incluido en las estimaciones de ahorro de CO₂ (FR40c)

## FR — Check-in y Liberación Automática
- FR24: check-in desde la app en la reserva activa (sin QR, sin enlace externo); acción única
- FR25: (nice-to-have post-MVP) check-in por enlace en correo electrónico sin sesión activa
- FR26: liberación automática si el check-in no se confirma dentro de los 15 min desde el inicio (timeout configurable por OrgAdmin)
- FR27: (nice-to-have post-MVP) recordatorio al Empleado 10 min antes del plazo de liberación automática
- FR28: puesto liberado inmediatamente disponible para nuevas reservas en tiempo real

## FR — Gestión de Incidentes
- FR29: cualquier usuario autenticado puede reportar un incidente en un recurso (descripción de texto + foto opcional)
- FR30: al enviar un incidente → el recurso se marca automáticamente como no disponible; eliminado de la disponibilidad de reservas
- FR31: notificación push entregada a todos los Técnicos en un plazo de 30 segundos tras el envío de un nuevo incidente (ubicación + descripción)
- FR32: Técnico actualiza el estado del incidente (abierto → en progreso → resuelto)
- FR33: incidente resuelto → recurso reactivado automáticamente para reservas
- FR34: OrgAdmin ve todos los incidentes con estado, tiempo de resolución, asignación de técnico

## FR — Analítica y Ocupación
- FR35: OrgAdmin ve el total de reservas por zona (día actual + semana actual)
- FR36: OrgAdmin ve la tasa de ocupación (check-ins confirmados vs. total de reservas) por zona y por día
- FR37: sugerencia de consolidación de zonas cuando la asistencia diaria esté por debajo de un umbral configurable; identifica zonas para activar y zonas para apagar
- FR38: (nice-to-have post-MVP) OrgAdmin envía notificación dirigida a Empleados reservados en la zona recomendada para apagado
- FR39: OrgAdmin exporta resumen de ocupación para un rango de fechas seleccionado como CSV
- FR40b: estimación de ahorro energético por sala cerrada; fórmula: `savings = room_m2 × cost_per_m2_per_day × closure_days`; el coste por m² lo establece OrgAdmin en el onboarding (referencia: ~8.200 €/año/sala); la estructura del edificio (plantas, m² de salas) se recoge en la configuración inicial
- FR40c: estimación de ahorro de CO₂ a partir de (a) salas cerradas (energía ahorrada) + (b) días de teletrabajo de empleados (FR23b); los factores de CO₂ son constantes configurables

## FR — Datos y Privacidad
- FR40: aviso de privacidad + consentimiento explícito en el registro (GDPR UE 2016/679)
- FR41: Empleado solicita exportación de datos personales (reservas, perfil)
- FR42: OrgAdmin configura el periodo de retención de datos del historial de reservas (por defecto 12 meses; luego anonimizado/eliminado)
- FR43: registro de auditoría de todas las acciones de crear/actualizar/cancelar reserva + cambios de estado de incidentes; accesible para OrgAdmin; retención mínima 90 días

## Requisitos No Funcionales
- Rendimiento: carga inicial ≤3 s en 50 Mbps; respuestas de API principales ≤500 ms p95; sondeo de disponibilidad cada 30 s; recursos del mapa de planta cargados de forma diferida por planta navegada; carga de fotos asíncrona (la UI confirma de inmediato)
- Seguridad: HTTPS/TLS 1.2+; contraseñas almacenadas en texto plano solo para el alcance del demo MVP (bcrypt factor mínimo 12 diferido a Growth); todos los endpoints API requieren auth excepto login/registro; RBAC aplicado en el servidor (no solo en frontend); validación MIME server-side para fotos (JPEG/PNG/WebP, ≤5 MB); todo texto de usuario sanitizado server-side; expiración de token JWT + estrategia de refresh + almacenamiento en cookie HttpOnly/Secure diferidos a Growth
- Escalabilidad: ≤500 usuarios concurrentes sin degradación; el esquema de BD soporta múltiples edificios desde el día 1; sin límites codificados para plantas/zonas/puestos
- Fiabilidad: 99% de uptime 07:00–20:00 Lun–Vie hora local; degradación elegante (mapa con último estado conocido + banner "datos en vivo no disponibles"; acciones de reserva deshabilitadas en lugar de fallar silenciosamente); el programador de liberación automática se recupera del estado en la BD tras reinicio del servidor (no depende solo de memoria)
- Mantenibilidad: OpenAPI/Swagger = contrato vinculante frontend–backend; 4 bloques desplegables independientemente con prefijo de esquema BD propio; toda configuración mediante variables de entorno (sin valores hardcodeados)
- Accesibilidad: contraste de color WCAG AA (mín. 4.5:1) vía tema Angular Material; acciones principales navegables por teclado; vista lista/tabla como fallback para el mapa; aria-label en todos los iconos/indicadores de estado; el foco se devuelve programáticamente tras modales/navegación

## Arquitectura Técnica
- Stack: Angular SPA + Spring Boot REST API + MySQL
- Renderizado del mapa: el mapa de planta SVG se genera dinámicamente en Angular a partir de la estructura almacenada planta/sala/puesto (layout por orden de creación); el estado de los puestos se superpone como componentes Angular — sin librería de mapas de terceros; no se almacena SVG en el backend
- Gestión de estado: servicios Angular + RxJS observables; no se requiere NgRx para el MVP
- Auth: Autenticación HTTP Basic para el demo MVP (credenciales en claro); guards de rutas Angular aplican control de acceso por rol; JWT con cookies HttpOnly/Secure diferido a Growth
- API: Angular HttpClient + interceptores para inyección de token + manejo global de errores; OpenAPI/Swagger como contrato frontend–backend
- Soporte de navegadores (MVP): Chrome/Firefox/Safari/Edge, últimas 2 versiones estables de cada uno; totalmente responsive (desktop/tablet/mobile); sin distribución en tiendas de apps
- Visión: Android/iOS vía Angular + Capacitor/Ionic (post-MVP, sujeto a capacidad del equipo tras entrega del MVP)
- Sin integraciones de terceros en MVP; la API REST en formato JSON diseñada para futuras integraciones con calendario/HVAC; correo vía SMTP (proveedor configurable vía variable de entorno)

## Cumplimiento y Restricciones del Dominio
- GDPR (UE 2016/679): PII colectados (nombre, historial de presencia, patrones de reserva, ubicación por zona); consentimiento explícito en el registro; derecho de acceso + eliminación; minimización de datos; política de privacidad en el onboarding
- Retención de datos: historial de reservas + registros de ocupación máximo 12 meses y luego anonimizado/eliminado; configurable por OrgAdmin
- Registro de auditoría: todas las acciones de crear/actualizar/cancelar reservas + cambios de estado de incidentes registrados (timestamp + user ID); accesible para OrgAdmin; retención mínima 90 días
- Control de hardware (HVAC/iluminación) limitado a la capa Vision; API MVP solo lectura respecto a cualquier futura integración BAS

## Modo Demo SaaS
- Sin procesamiento real de pagos; facturación/facturas/impuestos diferidos a futuras versiones
- Vista de precios/planes eliminada de la navegación pública; accesible solo desde la configuración de OrgAdmin
- "Subscribe" → solo registro de demo (no facturación real); banner visible "Entorno de demo — sin pagos reales" en la app y en los ficheros exportados
- Tenants de demo poblados manualmente o vía consola de admin; segmentación lógica de datos (no aislamiento multi-tenant endurecido)
- Flujo de signup: crear org/cuenta → aceptar términos de demo → importación opcional de datos de ejemplo → activación inmediata del trial
- Rol OrgAdmin = combinación de facilities + tenant admin (gestiona activos, usuarios, configuración del tenant, plan demo)
- Telemetría: uso registrado para decisiones de producto; no se usa para facturación/enrutamiento de cargos en demo

## Decisiones Clave (historial de edición hasta 2026-05-19)
- Ronda 1 integrada: registro con código de empresa (FR1); reserva por turnos mañana/tarde (FR19); límite de 7 días (FR19b); técnico modifica/cancela reservas (FR22b); indicador de teletrabajo (FR23b); política de apertura al 80% (FR9b); UI admin para plantas/salas/puestos (FR8 actualizado); mapa con mosaicos rectangulares + tooltip de turno (FR13/FR15); fórmula de ahorro energético por m² (FR40b); estimación de CO₂ a partir de teletrabajo (FR40c); precios/planes eliminados de la navegación pública
- Ronda 2 correcciones de alcance: FR15 overlay de calor eliminado (no discutido); FR15b integrado en FR15 (desglose de sala); FR16 filtros de mapa → nice-to-have post-MVP; FR22b sin notificación a usuario en demo; FR24 check-in en app (sin QR); FR25 check-in por email → nice-to-have; FR27 recordatorio pre-liberación → nice-to-have; FR38 notificación de apagado de zona → nice-to-have
- Ronda 3 alcance del demo: JWT eliminado del MVP; autenticación simplificada a HTTP Basic Auth (credenciales en claro) para ejercicio demo; bcrypt + JWT + cookies HttpOnly diferidos a Growth; FR11 reemplazado (sin carga SVG → mapa generado dinámicamente en Angular desde la estructura almacenada, posiciones por orden de creación); FR12 eliminado (asociación manual de anclas SVG ya no necesaria)

## Riesgos y Mitigaciones
- Complejidad SVG/mapa del Bloque 2: spike en la Semana 1 para validar mosaico de sala + desglose de puestos; vista lista/tabla siempre disponible
- Integración (Semana 6–7): biblioteca compartida desde Semana 1; mocks de API desde Semana 2
- Deriva de alcance en Bloque 4: límite estricto = ahorro energético/CO₂ por sala cerrada + vista de ocupación + lista de incidentes; notificación de apagado de zona + informe exportable → Growth
- Escritorios fantasma a pesar de la liberación automática: timeout configurable (por defecto 15 min); recordatorio en T−10 min
- Datos personales en informes: los informes de ocupación agregan por zona; datos individuales visibles solo para el usuario correspondiente + OrgAdmin
- App no disponible a la llegada: fallback de check-in por enlace en correo (post-MVP, FR25)
