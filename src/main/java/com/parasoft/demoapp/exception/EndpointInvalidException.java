package com.parasoft.demoapp.exception;

public class EndpointInvalidException extends Exception{

    private static final long serialVersionUID = 4641068388300973918L;

    public EndpointInvalidException(String message) {
        super(message);
    }

    public EndpointInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
