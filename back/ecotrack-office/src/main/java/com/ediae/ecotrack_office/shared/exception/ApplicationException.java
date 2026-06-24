package com.ediae.ecotrack_office.shared.exception;

/**
 * Excepción base para todas las excepciones de la aplicación.
 * Toda excepción funcional debe extender de esta clase.
 */
public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * Constructor con código de error y mensaje personalizado.
     *
     * @param errorCode código de error (enum)
     * @param message mensaje personalizado (sobrescribe el mensaje por defecto del código)
     */
    public ApplicationException(ErrorCode errorCode, String message) {
        super(message != null ? message : errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructor con código de error solamente (usa el mensaje por defecto).
     *
     * @param errorCode código de error (enum)
     */
    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode código de error (enum)
     * @param message mensaje personalizado
     * @param cause excepción que causó este error
     */
    public ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message != null ? message : errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
