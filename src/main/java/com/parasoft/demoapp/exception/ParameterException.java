package com.parasoft.demoapp.exception;

public class ParameterException extends Exception{

    private static final long serialVersionUID = -3118854411402979260L;

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
