package com.ediae.ecotrack_office.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;

/**
 * Handler global centralizado para todas las excepciones de la aplicación.
 *
 * Responsabilidades:
 * - Capturar todas las excepciones (propias y estándar de Spring)
 * - Convertirlas a ErrorResponse estándar
 * - Generar traceId para trazabilidad
 * - Loguear errores adecuadamente
 *
 * Nota: No extendemos ResponseEntityExceptionHandler para evitar conflictos
 * de mapping con sus handlers internos.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de negocio (ApplicationException y subclases).
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex,
            WebRequest request) {

        ErrorCode errorCode = ex.getErrorCode();

        log.warn("Excepción de aplicación ({}): {}", errorCode.name(), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    /**
     * Maneja errores de validación en @RequestBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        log.warn("Error de validación en RequestBody: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                "Error de validación"
        );

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    /**
     * Maneja errores de validación de parámetros, path variables, etc.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        log.warn("Error de validación (constraint violation) en parámetros: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                "Error de validación"
        );

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    /**
     * Maneja excepciones no previstas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("Excepción no prevista: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                "Error interno del servidor"
        );

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
