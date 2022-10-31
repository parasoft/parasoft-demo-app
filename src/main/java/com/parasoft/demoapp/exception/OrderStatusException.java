package com.parasoft.demoapp.exception;

public class OrderStatusException extends Exception{
    private static final long serialVersionUID = -1930724666963320176L;

    public OrderStatusException(String message) {
        super(message);
    }

    public OrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
