package com.crud.crud.web.errors;

public class EmailAlreadyExistsException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException() {
        super("Email is already in use!", "userManagement", "emailexists");
    }
}
