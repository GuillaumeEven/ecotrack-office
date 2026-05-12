# Arquitectura en capas — Spring Boot REST

## Flujo HTTP global

```
┌──────────────────────────────────────────────────────────────────────────┐
│  Cliente HTTP  (Angular, Postman...)                                     │
│                           │  JSON (Request Body / Path Variable)         │
│                           ▼                                              │
│  ┌─────────────────────────────────────┐                                 │
│  │         CONTROLLER                  │  ← RequestDTO  (validación)     │
│  │  @RestController                    │  → ResponseDTO (respuesta API)  │
│  └───────────────┬─────────────────────┘                                 │
│                  │  llama al Service                                      │
│                  ▼                                                        │
│  ┌─────────────────────────────────────┐                                 │
│  │         SERVICE                     │  ← Model (lógica de negocio)    │
│  │  @Service / @Transactional          │  → reglas, validaciones         │
│  └───────────────┬─────────────────────┘                                 │
│                  │  llama al Repository                                   │
│                  ▼                                                        │
│  ┌─────────────────────────────────────┐                                 │
│  │         REPOSITORY                  │  ← Entity (JPA)                 │
│  │  JpaRepository<Entity, ID>          │  → lecturas/escrituras BDD      │
│  └───────────────┬─────────────────────┘                                 │
│                  │  SQL                                                   │
│                  ▼                                                        │
│              BASE DE DATOS                                                │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## Rol de cada objeto

| Objeto | Capa | Función | Anotaciones JPA | Inmutable |
|---|---|---|---|---|
| `UsuarioEntity` | Repository | Representación directa de la tabla BDD | ✅ `@Entity`, `@Column`... | ❌ |
| `UsuarioModel` | Service | Lógica de negocio, transformaciones, validaciones | ❌ | opcional |
| `UsuarioGetDTO` | Controller | Respuesta API filtrada (sin contraseña, etc.) | ❌ | ✅ (`record`) |

> **Regla de oro**: nunca exponer una `Entity` directamente en la API.

---

## Flujo de mapping

### GET — lectura

```
BDD  →  Entity  →  Model  →  DTO  →  Cliente
       (repo)    (service) (controller)
```

```java
// Repository
List<UsuarioEntity> entities = usuarioRepository.findAll();

// Service : Entity → Model
List<UsuarioModel> models = entities.stream()
    .map(UsuarioModel::fromEntity)
    .toList();

// Controller : Model → DTO
List<UsuarioGetDTO> dtos = models.stream()
    .map(m -> new UsuarioGetDTO(m.getId(), m.getNombre(), ...))
    .toList();
```

> Si no hay lógica de negocio: saltarse el Model y mapear **Entity → DTO** directamente.

---

## Relaciones ManyToOne — Entity → Model → DTO (patrón y ejemplos)

Resumen rápido:
- En la capa de persistencia (`Entity`) las relaciones `ManyToOne` se modelan como referencias a objetos (`GeneroEntity genero`) y JPA crea la columna FK (`genero_id`) mediante `@JoinColumn`.
- En la capa de servicio (`Model`) es útil representar la relación como un objeto anidado (`GeneroModel`) para respuestas (`read`) que necesiten `id` + `nombre`.
- En la API (`DTO`) diferenciar entre DTOs de lectura (GET) que contienen el sub-DTO (`GeneroDTO`) y DTOs de escritura (POST/PUT) que reciben únicamente `generoId` (Integer).

Detalles y ejemplos:

- Entity (persistencia)
```java
// UsuarioEntity.java
@ManyToOne(optional = false)
@JoinColumn(name = "genero_id", nullable = false)
private GeneroEntity genero;
```
Esto define la FK `genero_id` en la tabla `usuarios`.

- Model (capa service) — lectura
Agregar en `UsuarioModel` un campo `GeneroModel` y mapear desde la entity:

```java
// UsuarioModel.java (campo + getter/setter)
private GeneroModel genero;

public GeneroModel getGenero() { return genero; }
public void setGenero(GeneroModel genero) { this.genero = genero; }

// fromEntity
public static UsuarioModel fromEntity(UsuarioEntity e) {
    UsuarioModel m = new UsuarioModel(
        e.getContrasena(),
        e.getFecha_hora_creacion(),
        e.getFecha_nacimiento(),
        e.getHora_desayuno(),
        e.getId(),
        e.getNick_usuario(),
        e.getNombre(),
        e.getPrimer_apellido(),
        e.getSegundo_apellido()
    );
    m.setGenero(GeneroModel.fromEntity(e.getGenero()));
    // m.setPuestoTrabajo(PuestoTrabajoModel.fromEntity(e.getPuesto_trabajo()));
    return m;
}
```

- DTOs — separar lectura y escritura
  - `UsuarioGetDTO` (read): incluir `GeneroDTO` para mostrar `id`+`nombre`.
  - `UsuarioPostDTO` / `UsuarioPutDTO` (write): incluir `Integer generoId` y `Integer puestoTrabajoId`.

Ejemplo de `record` para read/write:
```java
public record GeneroDTO(Integer id, String nombre) {}

public record UsuarioGetDTO(
    Integer id,
    String nickUsuario,
    LocalDateTime fechaHoraCreacion,
    GeneroDTO genero,
    String nombre,
    String primerApellido,
    String segundoApellido,
    LocalDate fechaNacimiento,
    LocalTime horaDesayuno
) {}

public record UsuarioPostDTO(
    String nickUsuario,
    String contrasena,
    Integer generoId,
    Integer puestoTrabajoId,
    String nombre,
    String primerApellido,
    String segundoApellido,
    LocalDate fechaNacimiento,
    LocalTime horaDesayuno
) {}
```

- Mapping para creación/actualización (write)
Al recibir `UsuarioPostDTO` con `generoId`, resolver la `GeneroEntity` en el servicio antes de persistir:

```java
GeneroEntity genero = generoRepository.findById(dto.generoId())
    .orElseThrow(() -> new EntityNotFoundException("Genero no encontrado"));

PuestoDeTrabajoEntity puesto = null;
if (dto.puestoTrabajoId() != null) {
    puesto = puestoRepository.findById(dto.puestoTrabajoId())
        .orElseThrow(() -> new EntityNotFoundException("Puesto no encontrado"));
}

UsuarioEntity entity = new UsuarioEntity(
    null,
    dto.nickUsuario(),
    dto.contrasena(),
    genero,
    dto.nombre(),
    dto.primerApellido(),
    dto.segundoApellido(),
    dto.fechaNacimiento(),
    dto.horaDesayuno(),
    puesto
);
usuarioRepository.save(entity);
```

- Mapping para lectura (GET)
Recuperar `UsuarioEntity` (idealmente con las relaciones necesarias ya cargadas) y usar `UsuarioModel.fromEntity(...)` → luego construir `UsuarioGetDTO` a partir del `Model`.

- Performance / N+1
Si las relaciones están LAZY, al mapear listas puede ocurrir N+1. Soluciones:
  - Usar `fetch join` en consultas cuando se necesitan las relaciones:

```java
@Query("select u from UsuarioEntity u left join fetch u.genero left join fetch u.puesto_trabajo where u.id = :id")
Optional<UsuarioEntity> findByIdWithRelations(@Param("id") Integer id);
```

  - Para listados grandes, usar proyecciones DTO en la consulta o queries que seleccionen solo los campos necesarios.
  - Evitar cargar colecciones anidadas innecesarias.

- Validación y errores
  - Validar que `generoId` esté presente (si es obligatorio) y devolver `400` o `404` si no se encuentra.
  - No aceptar objetos anidados completos en POST/PUT (evitar que el cliente envíe `GeneroDTO` completo para crear una relación); recibir sólo ids simplifica validación.

- Buenas prácticas resumidas
  - `Entity`: persistencia y relaciones JPA.
  - Nunca exponer `Entity` directamente en la API.
  - `Model`: representación interna/ lógica de negocio; puede contener sub-models.
  - `DTO read`: incluir sub-DTOs (p.ej. `GeneroDTO`).
  - `DTO write`: enviar sólo `id` para relaciones (p.ej. `generoId`).
  - Resolver ids en el servicio con repositorios y manejar errores.
  - Controlar N+1 con fetch-joins o proyecciones.

---

## DTO — usar un `record`

```java
public record UsuarioGetDTO(
    Integer id,
    String nickUsuario,
    LocalDateTime fechaHoraCreacion,
    String nombre,
    String primerApellido,
    String segundoApellido,    // puede ser null
    LocalDate fechaNacimiento,
    LocalTime horaDesayuno     // puede ser null
    // ⚠️ NO exponer la contraseña
) {}
```

| Característica | `record` Java |
|---|---|
| Campos | `final`, declarados en la cabecera |
| Constructor | generado automáticamente |
| Getters | `id()`, `nombre()`, etc. (no `getId()`) |
| Setters | ❌ ninguno — inmutable |
| `equals` / `hashCode` / `toString` | ✅ generados |

> Si Jackson no encuentra los nombres de parámetros → añadir `@JsonProperty` o compilar con `-parameters`.

---

## Anotaciones Spring utiles

| Anotación | Ubicación | Para qué sirve |
|---|---|---|
| `@PathVariable` | parámetro método | extrae `{id}` de `/api/users/{id}` |
| `@RequestParam` | parámetro método | extrae `?id=1` de la URL |
| `@RequestBody` | parámetro método | deserializa el body JSON |
| `@ResponseStatus` | método / clase | fuerza un código HTTP de respuesta |
| `@Valid` | parámetro método | activa la validación del DTO (Bean Validation) |
| `@CrossOrigin` | clase / método | permite llamadas desde otros dominios (CORS) |

---

## Estructura de respuesta API recomendada

```json
{
  "data": { ... },
  "error": "",
  "meta": {
    "sizeof": 20,
    "page": 1,
    "total": 150
  }
}
```

> Para `GET by id`: `sizeof` y `page` pueden ser `null`.

---

## ResponseEntity — ejemplos

```java
// 404 con detalle
return ResponseEntity
    .status(HttpStatus.NOT_FOUND)
    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

// 404 sin body
return ResponseEntity.notFound().build();

// 201 Created con el objeto creado
return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuarioDTO);
```

---

## Cuándo usar Model vs. DTO directo

```
¿Hay lógica de negocio?
        │
       SÍ ──→  Entity → Model → DTO
        │
        NO ──→ Entity → DTO  (servicio CRUD simple)
```

---

## TODOs pendientes

- [ ] Pasar los DTOs a `record`
- [ ] Impedir duplicación de género
- [ ] Gestionar la validación de una sola direccion principal (capa servicios)
