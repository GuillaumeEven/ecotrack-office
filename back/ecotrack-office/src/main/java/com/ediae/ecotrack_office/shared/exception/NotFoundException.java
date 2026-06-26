package com.ediae.ecotrack_office.shared.exception;

/**
 * Excepción lanzada cuando un recurso no es encontrado.
 */
public class NotFoundException extends ApplicationException {

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND, "Recurso no encontrado");
    }

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}