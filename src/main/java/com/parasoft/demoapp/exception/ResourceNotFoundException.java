package com.parasoft.demoapp.exception;

public class ResourceNotFoundException extends Exception{

	private static final long serialVersionUID = -912282230393816134L;

	public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
