package com.ediae.ecotrack_office.shared.exception;

public class NotFoundException extends ApplicationException {

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND, "Recurso no encontrado");
    }

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}