package com.ediae.ecotrack_office.shared.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio o de dominio.
 */
public class BusinessRuleViolationException extends ApplicationException {

    public BusinessRuleViolationException() {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, "Violación de regla de negocio");
    }

    public BusinessRuleViolationException(String message) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
