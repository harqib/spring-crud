package com.crud.crud.web.errors;

public class UserNotFoundException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super("User not found.", "userManagement", "usernotfound");
    }
}
