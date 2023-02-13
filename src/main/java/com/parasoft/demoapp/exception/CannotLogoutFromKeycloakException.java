package com.parasoft.demoapp.exception;

public class CannotLogoutFromKeycloakException extends Exception{

    private static final long serialVersionUID = -759649018832388077L;

    public CannotLogoutFromKeycloakException(String message) {
        super(message);
    }

    public CannotLogoutFromKeycloakException(String message, Throwable cause) {
        super(message, cause);
    }
}
