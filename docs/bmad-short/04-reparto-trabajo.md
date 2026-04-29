# Reparto de trabajo — EcoTrack Office

> Basado en el sprint-calendar. 9 sprints · 23 historias · 4 estudiantes.
> Cada estudiante es propietario de un bloque completo: frontend Angular + API Spring Boot + esquema MySQL.

## Contenido

- [Responsabilidades por estudiante](#responsabilidades-por-estudiante)
- [Tabla de historias por sprint y estudiante](#tabla-de-historias-por-sprint-y-estudiante)
- [Dependencias críticas entre bloques](#dependencias-críticas-entre-bloques)
- [Leyenda de dificultad](#leyenda-de-dificultad)

---

## Responsabilidades por estudiante

| Bloque | Estudiante | Módulo Angular | Paquete Java | Tablas MySQL |
|---|---|---|---|---|
| **Bloque 1** | Estudiante 1 | `users/` | `com.ecotrack.users` | `usr_*` |
| **Bloque 2** | Estudiante 2 | `assets-mgmt/` | `com.ecotrack.assets` | `ast_*` |
| **Bloque 3** | Estudiante 3 | `reservations/` | `com.ecotrack.reservations` | `rsv_*` |
| **Bloque 4** | Estudiante 4 | `analytics/` | `com.ecotrack.analytics` | `anl_*` |

---

## Tabla de historias por sprint y estudiante

| Sprint | Objetivo | Historia | Responsable | Dificultad |
|---|---|---|---|---|
| **1** | Fundación | 0.1 — Init Angular | Todos | 🟡 |
| **1** | Fundación | 0.2 — Init Spring Boot | Todos | 🟡 |
| **1** | Fundación | 0.3 — Docker Compose local | Todos | 🟢 |
| **2** | Autenticación | 1.1 — Registro + GDPR | Est. 1 | 🟡 |
| **2** | Autenticación | 1.2 — Login + sesión JWT | Est. 1 | 🟡 |
| **3** | Perfil + Base activos | 1.3 — Perfil de empleado | Est. 1 | 🟢 |
| **3** | Perfil + Base activos | 1.4 — Admin gestión usuarios | Est. 1 | 🟡 |
| **3** | Perfil + Base activos | 2.1 — Plantas y zonas | Est. 2 | 🟡 |
| **4** | Gestión de activos | 2.2 — Escritorios y salas | Est. 2 | 🟡 |
| **4** | Gestión de activos | 2.3 — Subida SVG + posiciones | Est. 2 | 🔴 |
| **5** | Mapa interactivo ⚠️ | 2.4 — Visor mapa SVG | Est. 2 | 🔴 |
| **5** | Mapa interactivo ⚠️ | 2.5 — Capa de calor + filtros | Est. 2 | 🔴 |
| **6** | Reservas | 3.1 — Reserva + prevención doble | Est. 3 | 🔴 |
| **6** | Reservas | 3.2 — Mis reservas + cancelación | Est. 3 | 🟡 |
| **7** | Check-in | 3.3 — QR check-in | Est. 3 | 🟡 |
| **7** | Check-in | 3.4 — Check-in por email + recordatorio | Est. 3 | 🟡 |
| **7** | Auto-liberación | 3.5 — Scheduler liberación automática | Est. 3 | 🟡 |
| **8** | Incidencias | 4.1 — Reporte incidencia + bloqueo recurso | Est. 4 | 🟡 |
| **8** | Incidencias | 4.2 — Notificación técnico (SSE) + ciclo vida | Est. 4 | 🟡 |
| **8** | Incidencias | 4.3 — Vista admin incidencias | Est. 4 | 🟡 |
| **9** | Analítica | 4.4 — Dashboard ocupación | Est. 4 | 🔴 |
| **9** | Analítica | 4.5 — Consolidación zonas + notificaciones | Est. 4 | 🔴 |
| **9** | Gobernanza | 4.6 — Exportar informes + GDPR | Est. 4 | 🟡 |

---

## Dependencias críticas entre bloques

```
Bloque 1 (Auth)
    └─► Bloque 2 (Assets) ──────────────────────────────► Sprint 5 (Mapa)
                                                               ├─► Sprint 6 (Reservas)    [Bloque 3]
                                                               └─► Sprint 8 (Incidencias) [Bloque 4]
                                                                       └─► Sprint 9 (Analítica) [Bloque 4]
```

> **⚠️ Riesgo clave:** el Sprint 5 (Estudiante 2) bloquea los Sprints 6 y 8.
> Si el mapa se retrasa, Estudiantes 3 y 4 no pueden avanzar.
> **Solución:** usar mocks de la API desde la semana 2 mientras se termina el mapa.

---

## Leyenda de dificultad

| Icono | Nivel |
|---|---|
| 🟢 | Pequeño — pocas horas |
| 🟡 | Medio — 1-2 días |
| 🔴 | Grande — varios días, requiere más cuidado |
