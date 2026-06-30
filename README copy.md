<h1 align="center">
  <br>
  🌿 EcoTrack Office
  <br>
</h1>

<p align="center">
  <strong>Gestión inteligente de espacios de trabajo para oficinas híbridas</strong><br>
  Reserva un escritorio en 60 segundos. Deja que el sistema haga el resto.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-in%20development-yellow?style=flat-square" alt="Status"/>
  <img src="https://img.shields.io/badge/type-Final%20Master's%20Project-blueviolet?style=flat-square" alt="Type"/>
  <img src="https://img.shields.io/badge/PRD-complete-brightgreen?style=flat-square" alt="PRD"/>
  <img src="https://img.shields.io/badge/architecture-complete-brightgreen?style=flat-square" alt="Architecture"/>
  <img src="https://img.shields.io/badge/epics%20%26%20stories-complete-brightgreen?style=flat-square" alt="Epics"/>
  <img src="https://img.shields.io/badge/UX%20design-complete-brightgreen?style=flat-square" alt="UX Design"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=flat-square&logo=angular&logoColor=white" alt="Angular"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java"/>
</p>

<p align="center">
  <a href="project/roadmap.md">🗺️ Hoja de ruta</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/prd.md">📋 PRD</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/architecture.md">🏗️ Arquitectura</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/epics.md">📚 Epics & Stories</a> &nbsp;•&nbsp;
  <a href="_bmad-output/planning-artifacts/ux-design-specification.md">🎨 Diseño UX</a>
</p>

---

## El problema

Las grandes oficinas híbridas desperdician espacio y energía cada día. Los empleados reservan escritorios y no se presentan — los escritorios quedan vacíos y "reservados" al mismo tiempo. Los administradores de la organización pasan las mañanas recuperando manualmente reservas abandonadas desde una hoja de cálculo que nadie rellena. Nadie sabe qué plantas están realmente ocupadas hasta que alguien las recorre.

## La solución

EcoTrack Office es una plataforma web que resuelve ambos lados del problema a la vez:

- **Los empleados** ven un mapa de planta interactivo en vivo formado por mosaicos rectangulares de salas, pueden entrar en una sala para ver los escritorios disponibles y reservar por turno con dos toques.
- **Los administradores** obtienen recuperación automática de escritorios fantasma, sugerencias de consolidación de zonas, estimaciones de ahorro de energía/CO₂ y paneles de ocupación — todo sin tener que perseguir a nadie.

## Qué lo diferencia

| Característica | Cómo funciona |
|---|---|
| 🗺️ **Reserva orientada al mapa** | El edificio aparece como una cuadrícula de mosaicos de salas. Haz clic en una sala para ver escritorios individuales. Pulsa Mañana o Tarde para reservar — listo. |
| 🤖 **Automatización tolerante a incumplimientos** | ¿No hizo el check-in? El escritorio se libera automáticamente tras 15 min. El check-in es un solo toque desde la app — sin lector de QR, sin correo. El sistema absorbe la falta de cumplimiento en lugar de combatirla. |
| ⚡ **Cero recuperación manual** | Escritorios fantasma, recursos defectuosos, cierres de zona — todo gestionado automáticamente. Los administradores supervisan, no intervienen. |

## Métricas clave (objetivos MVP)

| Métrica | Objetivo |
|---|---|
| Tiempo de reserva | ≤ 60 segundos de extremo a extremo |
| Tasa de escritorios fantasma | < 5% después de la liberación automática |
| Eficiencia de zona | ≥ 80% de ocupantes consolidados en días de baja asistencia |

## Stack

```
Frontend   Angular SPA + Angular Material + mapa SVG interactivo
Backend    Spring Boot REST API (Java)
Database   MySQL
Auth       JWT (sin estado, cookies HttpOnly)
```

## Instalación y desarrollo

### Configurar variables de entorno

Debes tener una base de datos MySQL dedicada al proyecto.

```yml
# .env
DB_USERNAME=yourUsername
DB_PASSWORD=YourPassword
DB_URL=jdbc:postgresql://localhost:5432/yourDatabaseName
```

### Back-end

```bash
cd back/ecotrack-office/
mvn spring-boot:run
```

### Front-end

#### Instalar

```bash
cd front/ecotrack-office/
npm install
```

#### Ejecutar servidor

```bash
# desde /front/ecotrack-office
npm run start
```


## Personas

| Nombre | Rol | Necesidad principal |
|---|---|---|
| **Guillermo** | Ingeniero I+D | Reservar un escritorio que cumpla sus criterios en segundos, desde su teléfono en el autobús |
| **Eduardo** | Comercial | No pensar nunca en la herramienta — que funcione según sus hábitos |
| **Raimundo** | Técnico del edificio | Recibir notificaciones instantáneas cuando algo se rompe, con foto y ubicación |
| **José Luis** | Administrador de la organización | Sustituir su recorrido matutino por un panel de control de 30 segundos |

## Contexto del proyecto

Proyecto académico — Trabajo Final de Máster (TFM) en Ediae.
Equipo: 4 estudiantes × 8 semanas. Cada estudiante es responsable de un vertical full-stack (módulo Angular + CRUD Spring Boot + esquema MySQL).

| Bloque | Dominio |
|---|---|
| Bloque 1 | Usuarios y Autenticación (código de invitación de empresa, JWT, RBAC) |
| Bloque 2 | Activos físicos — salas (área de escritorio / sala de reuniones, m², política apertura 80%) & mapa interactivo |
| Bloque 3 | Reservas (por turnos), check-in desde la app, liberación automática, indicador de teletrabajo |
| Bloque 4 | Analítica, estimaciones de ahorro energético/CO₂, panel e incidencias |

## Hoja de ruta

Consulta la hoja de ruta completa del proyecto → [project/roadmap.md](project/roadmap.md)

## Documentación

| Documento | Descripción |
|---|---|
| [📋 PRD](_bmad-output/planning-artifacts/prd.md) | Documento completo de Requisitos del Producto — 43 requisitos funcionales, 5 categorías NFR, recorridos de usuario, restricciones de dominio, hoja de ruta por fases |
| [🏗️ Arquitectura](_bmad-output/planning-artifacts/architecture.md) | Registro de decisiones arquitectónicas — stack, 14 decisiones arquitectónicas, patrones de implementación, estructura completa del proyecto, mapeo de FR |
| [📚 Epics & Stories](_bmad-output/planning-artifacts/epics.md) | 5 épicas, 23 historias de usuario con criterios de aceptación completos mapeados a los 43 FR |
| [🎨 Especificación de Diseño UX](_bmad-output/planning-artifacts/ux-design-specification.md) | Especificación UX en 14 secciones — sistema de diseño, estrategia de componentes, recorridos de usuario, fundación visual, accesibilidad (WCAG 2.2 AA) |
| [🗓️ Calendario de Sprint](_bmad-output/planning-artifacts/sprint-calendar.md) | 9 sprints, 23 historias organizadas por dependencias técnicas con indicadores de complejidad |
| [📊 Estado del Sprint](_bmad-output/implementation-artifacts/sprint-status.yaml) | Seguimiento del sprint en vivo — estados de historias actualizados durante el desarrollo |
| [🗺️ Hoja de ruta](project/roadmap.md) | Checklist del proyecto y próximos pasos del flujo de trabajo BMAD |

---

<p align="center">
  Construido con el método <a href="https://github.com/bmad-method/bmad-method">BMAD Method</a> v6.2.2
</p>
