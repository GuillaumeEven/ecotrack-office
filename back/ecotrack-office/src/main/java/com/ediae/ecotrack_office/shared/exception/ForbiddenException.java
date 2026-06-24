package com.ediae.ecotrack_office.shared.exception;

/**
 * Excepción lanzada cuando se deniega el acceso a un recurso (403 Forbidden).
 */
public class ForbiddenException extends ApplicationException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN, "Acceso denegado");
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
