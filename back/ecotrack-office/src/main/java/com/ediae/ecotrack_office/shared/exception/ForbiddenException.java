package com.ediae.ecotrack_office.shared.exception;

public class ForbiddenException extends ApplicationException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN, "Acceso denegado");
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
