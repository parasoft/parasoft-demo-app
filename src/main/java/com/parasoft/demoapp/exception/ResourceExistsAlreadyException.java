package com.parasoft.demoapp.exception;

public class ResourceExistsAlreadyException extends Exception{

	private static final long serialVersionUID = -6706697660751216077L;

	public ResourceExistsAlreadyException(String message) {
        super(message);
    }

    public ResourceExistsAlreadyException(String message, Throwable cause) {
        super(message, cause);
    }

}
