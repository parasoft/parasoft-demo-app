package com.parasoft.demoapp.exception;

public class RabbitMQServerIsNotAvailableException extends MQServerIsNotAvailableException {

    private static final long serialVersionUID = 8477418319628215971L;

    public RabbitMQServerIsNotAvailableException(String message) {
        super(message);
    }

    public RabbitMQServerIsNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
