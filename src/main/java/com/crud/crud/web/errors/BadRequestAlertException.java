package com.crud.crud.web.errors;

public class BadRequestAlertException extends Throwable {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;

    public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
        this.entityName = entityName;
        this.errorKey = errorKey;
    }
}
