package com.ediae.ecotrack_office.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Enum con todos los códigos de error de la aplicación.
 * Cada código mapea a un código HTTP y un mensaje por defecto.
 *
 * Categorías:
 * - 1xxx: Errores de validación
 * - 2xxx: Errores de autenticación/autorización
 * - 3xxx: Errores de recurso
 * - 4xxx: Errores de negocio
 * - 5xxx: Errores del sistema
 */
public enum ErrorCode {
    // Validación
    VALIDATION_ERROR(1001, HttpStatus.BAD_REQUEST, "Error de validación"),
    INVALID_INPUT(1002, HttpStatus.BAD_REQUEST, "Entrada inválida"),
    FIELD_REQUIRED(1003, HttpStatus.BAD_REQUEST, "Campo requerido"),

    // Autenticación y autorización
    UNAUTHORIZED(2001, HttpStatus.UNAUTHORIZED, "No autenticado"),
    FORBIDDEN(2002, HttpStatus.FORBIDDEN, "Acceso denegado"),
    INVALID_CREDENTIALS(2003, HttpStatus.UNAUTHORIZED, "Credenciales inválidas"),

    // Recurso no encontrado
    RESOURCE_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "Recurso no encontrado"),
    USER_NOT_FOUND(3002, HttpStatus.NOT_FOUND, "Usuario no encontrado"),
    NOT_FOUND(3003, HttpStatus.NOT_FOUND, "No encontrado"),

    // Errores de negocio
    BUSINESS_RULE_VIOLATION(4001, HttpStatus.BAD_REQUEST, "Violación de regla de negocio"),
    DUPLICATE_RESOURCE(4002, HttpStatus.CONFLICT, "Recurso duplicado"),
    INVALID_STATE(4003, HttpStatus.BAD_REQUEST, "Estado inválido"),
    OPERATION_NOT_ALLOWED(4004, HttpStatus.BAD_REQUEST, "Operación no permitida"),
    METHOD_NOT_ALLOWED(4005, HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido"),

    // Errores del sistema
    DATABASE_ERROR(5001, HttpStatus.INTERNAL_SERVER_ERROR, "Error en la base de datos"),
    INTERNAL_SERVER_ERROR(5002, HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor"),
    SERVICE_UNAVAILABLE(5003, HttpStatus.SERVICE_UNAVAILABLE, "Servicio no disponible");

    private final int code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(int code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
