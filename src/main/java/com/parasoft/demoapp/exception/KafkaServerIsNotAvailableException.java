package com.parasoft.demoapp.exception;

public class KafkaServerIsNotAvailableException extends Exception {

    private static final long serialVersionUID = 1744501427075570091L;

    public KafkaServerIsNotAvailableException(String message) {
        super(message);
    }

    public KafkaServerIsNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
