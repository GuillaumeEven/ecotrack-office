package com.ediae.ecotrack_office.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
         * Maneja errores de conversión de tipo en parámetros (query/path),
         * por ejemplo organizationId=undefined cuando se espera Long.
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
                        MethodArgumentTypeMismatchException ex,
                        WebRequest request) {

                ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
                String message = "Parámetro '" + ex.getName() + "' inválido";

                log.warn("Error de conversión de parámetro '{}': valor recibido='{}', tipo esperado='{}'",
                                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatus().value(),
                                errorCode.getStatus().getReasonPhrase(),
                                message
                );

                return new ResponseEntity<>(errorResponse, errorCode.getStatus());
        }

        /**
         * Maneja parámetros de request requeridos que faltan.
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
                        MissingServletRequestParameterException ex,
                        WebRequest request) {

                ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

                log.warn("Falta parámetro requerido: {}", ex.getParameterName());

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
