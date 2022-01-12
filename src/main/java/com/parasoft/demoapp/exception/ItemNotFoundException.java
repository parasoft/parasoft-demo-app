package com.parasoft.demoapp.exception;

public class ItemNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 5524907132150511697L;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
