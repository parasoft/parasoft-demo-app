package com.parasoft.demoapp.exception;

public class MQServerIsNotAvailableException extends Exception {

    private static final long serialVersionUID = 1644601427075577892L;

    public MQServerIsNotAvailableException(String message) {
        super(message);
    }

    public MQServerIsNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
