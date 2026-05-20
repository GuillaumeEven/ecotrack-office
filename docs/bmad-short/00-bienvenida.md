# EcoTrack Office — Bienvenida al equipo

## ¿Qué vamos a construir?

**EcoTrack Office** es una aplicación web para gestionar espacios de trabajo en edificios de oficinas.
Los empleados reservan escritorios y salas desde su teléfono. La app evita conflictos, libera puestos abandonados automáticamente y avisas al técnico cuando algo se rompe.

Stack: **Angular** (frontend) + **Spring Boot** (API REST) + **MySQL** (base de datos)

---

## Los 4 usuarios del sistema

| Persona | Rol | Lo que necesita |
|---|---|---|
| **Guillermo** | Empleado | Reservar un escritorio en ≤ 60s desde el mapa interactivo |
| **Eduardo** | Empleado (despistado) | Que el sistema gestione su no-presentación sin que él haga nada |
| **Raimundo** | Técnico | Recibir una alerta inmediata cuando hay un problema en una sala |
| **José Luis** | Administrador | Ver el estado del edificio en tiempo real y exportar informes |

---

## La organización del trabajo: 4 bloques

Cada miembro del equipo es **responsable de un bloque completo** (frontend + API + base de datos):

| Bloque | Dominio | Responsable |
|---|---|---|
| **Bloque 1** | Usuarios y autenticación (HTTP Basic, roles) | Estudiante 1 |
| **Bloque 2** | Recursos físicos (planos, zonas, escritorios) | Estudiante 2 |
| **Bloque 3** | Reservas y check-in | Estudiante 3 |
| **Bloque 4** | Incidencias y analítica | Estudiante 4 |

> **Regla de oro:** el Bloque 1 es la base. Todo el mundo depende de él para autenticarse.
> El Bloque 2 (mapa interactivo) es el cuello de botella del Sprint 5 — bloqueará los Sprints 6 y 8.

---

## Índice de documentos

| Archivo | Contenido |
|---|---|
| [01-requisitos-funcionales.md](01-requisitos-funcionales.md) | Lista de los 43 requisitos funcionales |
| [02-specs-tecnicas.md](02-specs-tecnicas.md) | Frameworks, dependencias y árbol de archivos |
| [03-guia-dev.md](03-guia-dev.md) | Todo-list por sprints, convenciones, git |
| [04-reparto-trabajo.md](04-reparto-trabajo.md) | Tabla de repartición de historias por estudiante |
| [05-endpoints-api.md](05-endpoints-api.md) | Endpoints REST a implementar por bloque |
| [class-diagram.png](class-diagram.png) | Diagrama de clases del modelo de datos |
