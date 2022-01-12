package com.parasoft.demoapp.exception;

public class CartItemNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 350174431032862268L;

    public CartItemNotFoundException(String message) {
        super(message);
    }

    public CartItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
