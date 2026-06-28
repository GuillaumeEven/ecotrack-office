package com.ediae.ecotrack_office.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

/**
 * Configuración de OpenAPI/Swagger para la API.
 * Define los esquemas de respuesta de error, información general, etc.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EcoTrack Office API")
                        .version("1.0.0")
                        .description("API de gestión de reservas y recursos para EcoTrack Office")
                        .contact(new Contact()
                                .name("EcoTrack Team")
                                .email("team@ecotrack.es"))
                        .license(new License()
                                .name("No License")))
                .components(new Components()
                        .addSchemas("ErrorResponse", createErrorResponseSchema())
                        .addSchemas("ErrorCode", createErrorCodeSchema())
                        .addSchemas("FieldError", createFieldErrorSchema()));
    }

    /**
     * Define el esquema de la respuesta de error (ErrorResponse).
     */
    private Schema<?> createErrorResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Respuesta estándar de error")
                .addProperty("timestamp", new Schema<>()
                        .type("string")
                        .format("date-time")
                        .description("Fecha y hora del error (ISO 8601)"))
                .addProperty("status", new Schema<>()
                        .type("integer")
                        .description("Código HTTP de la respuesta (ej: 400, 404, 500)"))
                .addProperty("error", new Schema<>()
                        .type("string")
                        .description("Descripción del código HTTP (ej: 'Bad Request', 'Not Found')"))
                .addProperty("code", new Schema<>()
                        .type("integer")
                        .description("Código de error específico de la aplicación (ej: 1001, 3002)"))
                .addProperty("message", new Schema<>()
                        .type("string")
                        .description("Mensaje de error legible para el usuario"))
                .addProperty("path", new Schema<>()
                        .type("string")
                        .description("Ruta de la petición HTTP"))
                .addProperty("details", new Schema<>()
                        .type("array")
                        .items(new Schema<>().$ref("FieldError"))
                        .description("Detalles de validación (solo en errores de validación)"))
                .addProperty("traceId", new Schema<>()
                        .type("string")
                        .format("uuid")
                        .description("Identificador único de la transacción para trazabilidad"));
    }

    /**
     * Define el esquema de códigos de error (ErrorCode).
     */
    private Schema<?> createErrorCodeSchema() {
        return new Schema<>()
                .type("object")
                .description("Códigos de error de la aplicación y su mapeo HTTP")
                .example("{\n" +
                        "  \"1001\": \"VALIDATION_ERROR → 400 Bad Request\",\n" +
                        "  \"2001\": \"UNAUTHORIZED → 401 Unauthorized\",\n" +
                        "  \"2002\": \"FORBIDDEN → 403 Forbidden\",\n" +
                        "  \"3001\": \"RESOURCE_NOT_FOUND → 404 Not Found\",\n" +
                        "  \"3002\": \"USER_NOT_FOUND → 404 Not Found\",\n" +
                        "  \"4001\": \"BUSINESS_RULE_VIOLATION → 400 Bad Request\",\n" +
                        "  \"4002\": \"DUPLICATE_RESOURCE → 409 Conflict\",\n" +
                        "  \"4003\": \"INVALID_STATE → 400 Bad Request\",\n" +
                        "  \"5001\": \"DATABASE_ERROR → 500 Internal Server Error\",\n" +
                        "  \"5002\": \"INTERNAL_SERVER_ERROR → 500 Internal Server Error\"\n" +
                        "}");
    }

    /**
     * Define el esquema de error de campo (FieldError).
     */
    private Schema<?> createFieldErrorSchema() {
        return new Schema<>()
                .type("object")
                .description("Detalle de error de validación en un campo específico")
                .addProperty("field", new Schema<>()
                        .type("string")
                        .description("Nombre del campo con error (ej: 'email', 'password')"))
                .addProperty("rejectedValue", new Schema<>()
                        .type("object")
                        .description("Valor que fue rechazado por la validación"))
                .addProperty("message", new Schema<>()
                        .type("string")
                        .description("Razón del rechazo (ej: 'no es una dirección de correo válida')"));
    }

    /**
     * Respuesta 400 Bad Request (validación, regla de negocio).
     */
    public static ApiResponse badRequest(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example("{\n" +
                                                "  \"timestamp\": \"2026-06-22T14:30:00Z\",\n" +
                                                "  \"status\": 400,\n" +
                                                "  \"error\": \"Bad Request\",\n" +
                                                "  \"code\": 1001,\n" +
                                                "  \"message\": \"Error de validación\",\n" +
                                                "  \"path\": \"/api/v1/usuarios\",\n" +
                                                "  \"details\": [{\"field\": \"email\", \"rejectedValue\": \"invalid@\", \"message\": \"no es válido\"}],\n" +
                                                "  \"traceId\": \"550e8400-e29b-41d4-a716-446655440000\"\n" +
                                                "}")));
    }

    /**
     * Respuesta 401 Unauthorized.
     */
    public static ApiResponse unauthorized() {
        return new ApiResponse()
                .description("No autenticado")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example("{\n" +
                                                "  \"timestamp\": \"2026-06-22T14:30:00Z\",\n" +
                                                "  \"status\": 401,\n" +
                                                "  \"error\": \"Unauthorized\",\n" +
                                                "  \"code\": 2001,\n" +
                                                "  \"message\": \"No autenticado\",\n" +
                                                "  \"path\": \"/api/v1/usuarios\",\n" +
                                                "  \"traceId\": \"550e8400-e29b-41d4-a716-446655440000\"\n" +
                                                "}")));
    }

    /**
     * Respuesta 403 Forbidden (sin permisos).
     */
    public static ApiResponse forbidden(String message) {
        return new ApiResponse()
                .description("Acceso denegado")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example("{\n" +
                                                "  \"timestamp\": \"2026-06-22T14:30:00Z\",\n" +
                                                "  \"status\": 403,\n" +
                                                "  \"error\": \"Forbidden\",\n" +
                                                "  \"code\": 2002,\n" +
                                                "  \"message\": \"" + message + "\",\n" +
                                                "  \"path\": \"/api/v1/usuarios/999\",\n" +
                                                "  \"traceId\": \"550e8400-e29b-41d4-a716-446655440000\"\n" +
                                                "}")));
    }

    /**
     * Respuesta 404 Not Found.
     */
    public static ApiResponse notFound(String resourceType) {
        return new ApiResponse()
                .description(resourceType + " no encontrado")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example("{\n" +
                                                "  \"timestamp\": \"2026-06-22T14:30:00Z\",\n" +
                                                "  \"status\": 404,\n" +
                                                "  \"error\": \"Not Found\",\n" +
                                                "  \"code\": 3001,\n" +
                                                "  \"message\": \"" + resourceType + " con identificador '999' no encontrado\",\n" +
                                                "  \"path\": \"/api/v1/" + resourceType.toLowerCase() + "/999\",\n" +
                                                "  \"traceId\": \"550e8400-e29b-41d4-a716-446655440000\"\n" +
                                                "}")));
    }

    /**
     * Respuesta 409 Conflict (recurso duplicado).
     */
    public static ApiResponse conflict(String message) {
        return new ApiResponse()
                .description("Recurso duplicado")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example("{\n" +
                                                "  \"timestamp\": \"2026-06-22T14:30:00Z\",\n" +
                                                "  \"status\": 409,\n" +
                                                "  \"error\": \"Conflict\",\n" +
                                                "  \"code\": 4002,\n" +
                                                "  \"message\": \"" + message + "\",\n" +
                                                "  \"path\": \"/api/v1/usuarios\",\n" +
                                                "  \"traceId\": \"550e8400-e29b-41d4-a716-446655440000\"\n" +
                                                "}")));
    }

    /**
     * Respuesta 500 Internal Server Error.
     */
    public static ApiResponse internalServerError() {
        return new ApiResponse()
                .description("Error interno del servidor")
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example("{\n" +
                                                "  \"timestamp\": \"2026-06-22T14:30:00Z\",\n" +
                                                "  \"status\": 500,\n" +
                                                "  \"error\": \"Internal Server Error\",\n" +
                                                "  \"code\": 5002,\n" +
                                                "  \"message\": \"Error interno del servidor\",\n" +
                                                "  \"path\": \"/api/v1/usuarios\",\n" +
                                                "  \"traceId\": \"550e8400-e29b-41d4-a716-446655440000\"\n" +
                                                "}")));
    }
}
