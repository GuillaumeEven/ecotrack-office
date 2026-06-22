package com.ediae.ecotrack_office.shared.exception;

/**
 * Excepción lanzada cuando se viola una restricción de validación personalizada.
 */
public class ConstraintViolationException extends ApplicationException {
    public ConstraintViolationException() {
        super(ErrorCode.VALIDATION_ERROR, "Error de validación");
    }

    public ConstraintViolationException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message);
    }

    public ConstraintViolationException(String message, Throwable cause) {
        super(ErrorCode.VALIDATION_ERROR, message, cause);
    }
}
