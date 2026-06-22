
# Gestión de errores — implementación actual

Este documento describe la implementación real del manejo centralizado de errores
en el proyecto.

## Ubicación principal

- **Handler global**: [`src/main/java/com/ediae/ecotrack_office/shared/exception/GlobalExceptionHandler.java`](../../back/ecotrack-office/src/main/java/com/ediae/ecotrack_office/shared/exception/GlobalExceptionHandler.java)
  - Anotado con `@ControllerAdvice`
  - Extiende `ResponseEntityExceptionHandler`
  - Captura todas las excepciones y las convierte en `ErrorResponse` estándar

- **Tipos principales**:
  - [`ErrorCode`](../../back/ecotrack-office/src/main/java/com/ediae/ecotrack_office/shared/exception/ErrorCode.java): enum con todos los códigos de error
  - [`ErrorResponse`](../../back/ecotrack-office/src/main/java/com/ediae/ecotrack_office/shared/exception/ErrorResponse.java): DTO estándar para respuestas de error
  - [`ApplicationException`](../../back/ecotrack-office/src/main/java/com/ediae/ecotrack_office/shared/exception/ApplicationException.java): excepción base de negocio

- **Configuración OpenAPI/Swagger**:
  - [`OpenApiConfig.java`](../../back/ecotrack-office/src/main/java/com/ediae/ecotrack_office/config/OpenApiConfig.java): define esquemas reutilizables para Swagger
  - [`ExampleController.java`](../../back/ecotrack-office/src/main/java/com/ediae/ecotrack_office/api/v1/example/ExampleController.java): ejemplo completo de endpoint documentado

## Documentación en Swagger/OpenAPI

### Acceder a Swagger UI

Una vez que el backend esté corriendo:

```
GET http://localhost:8080/index.html
```

Verás una interfaz interactiva con:
- Todos los endpoints de la API
- Respuestas de éxito (2xx) y error (4xx, 5xx)
- Esquemas de solicitud y respuesta
- Ejemplos ejecutables

### Esquemas reutilizables

Todos los errores usan el esquema `ErrorResponse` que incluye:
- Campos: timestamp, status, error, code, message, path, details, traceId
- Estructura consistente en toda la API
- Ejemplos visuales para cada tipo de error

### Documentar un endpoint

Ver la guía completa en [`swagger-error-documentation.md`](./swagger-error-documentation.md).

Resumen:
1. Anotación `@Operation` en el método
2. Anotación `@ApiResponses` con cada respuesta posible
3. Usar `@Schema(ref = "#/components/schemas/ErrorResponse")` para 4xx/5xx
4. Incluir ejemplos en la documentación

La API devuelve un objeto JSON estandarizado (`ErrorResponse`) con los siguientes campos:

- `timestamp`: fecha/hora del error (ISO 8601)
- `status`: código HTTP numérico (p. ej. 400, 404, 500)
- `error`: frase de estado HTTP (p. ej. "Bad Request", "Not Found")
- `code`: código numérico identificador del error (p. ej. 1001, 3001)
- `message`: mensaje legible (detalle específico del error)
- `path`: ruta de la petición HTTP
- `details`: lista opcional de `FieldError` para errores de validación
  - `field`: nombre del campo
  - `rejectedValue`: valor que fue rechazado
  - `message`: razón del rechazo
- `traceId`: identificador único de la transacción (generado automáticamente)

### Ejemplo 1: Error de validación en RequestBody

```json
{
  "timestamp": "2026-06-22T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": 1001,
  "message": "Error de validación",
  "path": "/api/v1/usuarios",
  "details": [
    {
      "field": "email",
      "rejectedValue": "invalid@",
      "message": "no es una dirección de correo electrónico válida"
    },
    {
      "field": "password",
      "rejectedValue": null,
      "message": "no puede estar vacío"
    }
  ],
  "traceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Ejemplo 2: Recurso no encontrado

```json
{
  "timestamp": "2026-06-22T14:30:00Z",
  "status": 404,
  "error": "Not Found",
  "code": 3002,
  "message": "Usuario con identificador '999' no encontrado",
  "path": "/api/v1/usuarios/999",
  "details": null,
  "traceId": "550e8400-e29b-41d4-a716-446655440001"
}
```

### Ejemplo 3: Violación de regla de negocio

```json
{
  "timestamp": "2026-06-22T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": 4001,
  "message": "No se puede reservar más de 3 habitaciones por usuario",
  "path": "/api/v1/reservas",
  "details": null,
  "traceId": "550e8400-e29b-41d4-a716-446655440002"
}
```

## Casos tratados por el handler

| Excepción | Manejador | HTTP | Código | Notas |
|-----------|-----------|------|--------|-------|
| `ApplicationException` y subclases | `handleApplicationException` | Según `ErrorCode` | Según `ErrorCode` | Cualquier excepción de negocio propia |
| `MethodArgumentNotValidException` | `handleMethodArgumentNotValid` | 400 | 1001 | Validación en `@RequestBody` |
| `ConstraintViolationException` | `handleConstraintViolation` | 400 | 1001 | Validación en parámetros, path vars, query params |
| `Exception` (genérica no prevista) | `handleGenericException` | 500 | 5002 | Cualquier otra excepción capturada |

### Jerarquía de excepciones de negocio

Todas las excepciones de negocio heredan de `ApplicationException`:

- **`ResourceNotFoundException`**: usado cuando no se encuentra un recurso (3001)
- **`UserNotFoundException`**: usado cuando no se encuentra un usuario (3002)
- **`BusinessRuleException`**: usado cuando se viola una regla de negocio (4001)
- **`DuplicateResourceException`**: usado cuando ya existe un recurso (4002)
- **`InvalidStateException`**: usado cuando el estado no permite la operación (4003)
- **`UnauthorizedException`**: usado cuando no está autenticado (2001)
- **`AccessDeniedException`**: usado cuando no tiene permisos (2002)

### Mapeo ErrorCode → HTTP Status

El enum `ErrorCode` define tanto el código numérico como el `HttpStatus`:

```
Categoría 1xxx (validación):
  - VALIDATION_ERROR (1001) → 400 Bad Request
  - INVALID_INPUT (1002) → 400 Bad Request

Categoría 2xxx (autenticación/autorización):
  - UNAUTHORIZED (2001) → 401 Unauthorized
  - FORBIDDEN (2002) → 403 Forbidden

Categoría 3xxx (recurso):
  - RESOURCE_NOT_FOUND (3001) → 404 Not Found
  - USER_NOT_FOUND (3002) → 404 Not Found

Categoría 4xxx (negocio):
  - BUSINESS_RULE_VIOLATION (4001) → 400 Bad Request
  - DUPLICATE_RESOURCE (4002) → 409 Conflict
  - INVALID_STATE (4003) → 400 Bad Request

Categoría 5xxx (sistema):
  - DATABASE_ERROR (5001) → 500 Internal Server Error
  - INTERNAL_SERVER_ERROR (5002) → 500 Internal Server Error
  - SERVICE_UNAVAILABLE (5003) → 503 Service Unavailable
```

## Buenas prácticas para lanzar errores

### Caso 1: Recurso no encontrado

```java
// En el servicio
public Usuario obtenerPorId(Long id) {
    return usuarioRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
}
```

**Respuesta HTTP**:
```
404 Not Found
{
  "status": 404,
  "code": 3002,
  "message": "Usuario con identificador '123' no encontrado",
  "traceId": "..."
}
```

### Caso 2: Validación de regla de negocio

```java
// En el servicio
public void reservarHabitacion(Long usuarioId, Habitacion habitacion) {
    long reservasActuales = reservaRepository.countByUsuarioId(usuarioId);
    if (reservasActuales >= 3) {
        throw new BusinessRuleException(
            "No se puede reservar más de 3 habitaciones por usuario"
        );
    }
    // ... continuar con la lógica
}
```

**Respuesta HTTP**:
```
400 Bad Request
{
  "status": 400,
  "code": 4001,
  "message": "No se puede reservar más de 3 habitaciones por usuario",
  "traceId": "..."
}
```

### Caso 3: Recurso duplicado

```java
// En el servicio
public Usuario crearUsuario(CreateUsuarioRequest request) {
    boolean existe = usuarioRepository.existsByEmail(request.getEmail());
    if (existe) {
        throw new DuplicateResourceException(
            "Usuario",
            "email",
            request.getEmail()
        );
    }
    // ... crear el usuario
}
```

**Respuesta HTTP**:
```
409 Conflict
{
  "status": 409,
  "code": 4002,
  "message": "Usuario con email 'user@example.com' ya existe",
  "traceId": "..."
}
```

### Caso 4: Estado inválido

```java
// En el servicio
public void cancelarReserva(Long reservaId) {
    Reserva reserva = obtenerReserva(reservaId);
    if (!reserva.puedeSerCancelada()) {
        throw new InvalidStateException(
            "Reserva",
            reserva.getEstado(),
            "cancelar"
        );
    }
    // ... continuar con la lógica
}
```

**Respuesta HTTP**:
```
400 Bad Request
{
  "status": 400,
  "code": 4003,
  "message": "No se puede cancelar una Reserva en estado 'COMPLETADA'",
  "traceId": "..."
}
```

### Caso 5: Acceso denegado

```java
// En el servicio
public void actualizarReserva(Long reservaId, UpdateReservaRequest request, String usuarioActual) {
    Reserva reserva = obtenerReserva(reservaId);
    if (!reserva.getUsuarioId().equals(usuarioActual)) {
        throw new AccessDeniedException(
            "No tienes permiso para modificar esta reserva"
        );
    }
    // ... continuar con la lógica
}
```

**Respuesta HTTP**:
```
403 Forbidden
{
  "status": 403,
  "code": 2002,
  "message": "No tienes permiso para modificar esta reserva",
  "traceId": "..."
}
```

### Caso 6: Crear excepción personalizada

Si necesitas una excepción específica de tu dominio, extiende `ApplicationException`:

```java
public class HabitacionNoDisponibleException extends ApplicationException {
    public HabitacionNoDisponibleException(Long habitacionId) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, 
              String.format("Habitación %d no disponible para las fechas solicitadas", habitacionId));
    }
}
```

Lanzar:
```java
throw new HabitacionNoDisponibleException(habitacionId);
```

## Trazabilidad y logging

- **Cada error genera automáticamente un `traceId`** (UUID): permite correlacionar logs del servidor con la respuesta del cliente.
- El cliente puede reportar el `traceId` para que el equipo de soporte investigue en los logs.
- El handler loguea todos los errores con su severidad:
  - `WARN`: excepciones de negocio (ApplicationException)
  - `ERROR`: excepciones no previstas

### Ejemplo de correlación

Cliente recibe:
```json
{ "traceId": "550e8400-e29b-41d4-a716-446655440001" }
```

Servidor tiene en logs (`/logs/application.log`):
```
2026-06-22 14:30:00 WARN  [GlobalExceptionHandler] Excepción de aplicación (USER_NOT_FOUND): Usuario con identificador '999' no encontrado
```

## Ventajas de esta arquitectura

✅ **Una única fuente de verdad**: todos los códigos en `ErrorCode` enum  
✅ **Traceabilidad**: `traceId` en cada error para debugging  
✅ **Extensible**: agregar nuevas excepciones = 2 líneas de código  
✅ **Consistente**: formato de respuesta idéntico para toda la API  
✅ **Robusto**: captura cualquier excepción no prevista sin crashes  
✅ **Mantenible**: cambiar un código HTTP no requiere buscar `@ResponseStatus` en todo el código
