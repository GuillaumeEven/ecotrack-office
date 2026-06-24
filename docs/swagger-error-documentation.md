# Uso de OpenAPI/Swagger para documentar excepciones

## Overview

La configuración de OpenAPI (`OpenApiConfig.java`) define esquemas reutilizables para documentar las respuestas de error. 

Todos los endpoints deben documentar las excepciones que pueden lanzar usando `@Operation` y `@ApiResponse`.

## Estructura de OpenApiConfig

- **Schemas**:
  - `ErrorResponse`: La respuesta de error estándar
  - `FieldError`: Detalles de error en validación de campos
  - `ErrorCode`: Referencia de todos los códigos de error

- **Métodos auxiliares** (static):
  - `badRequest()` - 400 Bad Request (validación)
  - `unauthorized()` - 401 Unauthorized
  - `forbidden()` - 403 Forbidden
  - `notFound()` - 404 Not Found
  - `conflict()` - 409 Conflict (duplicado)
  - `internalServerError()` - 500 Internal Server Error

## Ejemplo 1: Endpoint simple con manejo de errores

```java
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Recupera un usuario específico por su identificador",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuario encontrado",
                content = @Content(mediaType = "application/json", 
                                   schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "No autenticado",
                content = @Content(mediaType = "application/json",
                                   schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json",
                                   schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        // Lanza UserNotFoundException si no existe
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(mapToResponse(usuario));
    }
}
```

## Ejemplo 2: Endpoint con validación

```java
@PostMapping
@Operation(
    summary = "Crear nuevo usuario",
    description = "Crea un nuevo usuario con los datos proporcionados",
    responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validación fallida o violación de regla de negocio"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "El usuario ya existe"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno"
        )
    }
)
public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CreateUsuarioRequest request) {
    // Lanza MethodArgumentNotValidException si @Valid falla
    // Lanza DuplicateResourceException si el email ya existe
    Usuario usuario = usuarioService.crear(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(usuario));
}
```

## Ejemplo 3: Usando los métodos auxiliares de OpenApiConfig

```java
@PutMapping("/{id}")
@Operation(
    summary = "Actualizar usuario",
    responses = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "400", description = "Validación fallida", 
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
        @ApiResponse(responseCode = "403", description = "No tienes permiso",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
        @ApiResponse(responseCode = "500", description = "Error interno")
    }
)
public ResponseEntity<UsuarioResponse> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUsuarioRequest request,
        @AuthenticationPrincipal String usuarioActual) {
    
    // Lanza UserNotFoundException si no existe
    // Lanza AccessDeniedException si no es el dueño
    Usuario usuario = usuarioService.actualizar(id, request, usuarioActual);
    return ResponseEntity.ok(mapToResponse(usuario));
}
```

## Documentación en Swagger UI

Una vez que el backend está corriendo:

1. Accede a: `http://localhost:8080/swagger-ui.html`
2. Verás todos los endpoints documentados
3. Cada endpoint mostrará las respuestas de error posibles
4. Al expandir una respuesta de error (4xx, 5xx), verás el esquema `ErrorResponse`
5. Haz clic en "Schema" para ver la estructura completa

## Códigos de error disponibles

Todos los códigos están documentados en el enum `ErrorCode`:

### Categoría 1xxx (Validación)
- 1001 VALIDATION_ERROR → 400
- 1002 INVALID_INPUT → 400
- 1003 FIELD_REQUIRED → 400

### Categoría 2xxx (Autenticación/Autorización)
- 2001 UNAUTHORIZED → 401
- 2002 FORBIDDEN → 403
- 2003 INVALID_CREDENTIALS → 401

### Categoría 3xxx (Recurso)
- 3001 RESOURCE_NOT_FOUND → 404
- 3002 USER_NOT_FOUND → 404
- 3003 NOT_FOUND → 404

### Categoría 4xxx (Negocio)
- 4001 BUSINESS_RULE_VIOLATION → 400
- 4002 DUPLICATE_RESOURCE → 409
- 4003 INVALID_STATE → 400
- 4004 OPERATION_NOT_ALLOWED → 400

### Categoría 5xxx (Sistema)
- 5001 DATABASE_ERROR → 500
- 5002 INTERNAL_SERVER_ERROR → 500
- 5003 SERVICE_UNAVAILABLE → 503

## Tips

✅ **Siempre documenta los 4xx y 5xx que tu endpoint puede lanzar**

✅ **Usa `@Schema(ref = "#/components/schemas/ErrorResponse")` para reutilizar el esquema**

✅ **Los `@RequestBody` marcados con `@Valid` generarán automáticamente 400**

✅ **Haz `@Transactional` en servicios que lancen excepciones custom**

❌ **No documentes "400" genérico: especifica qué puede fallar (validación, duplicado, etc.)**
