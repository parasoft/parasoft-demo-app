package com.parasoft.demoapp.exception;

public class OrderNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 2246961827092629705L;

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
