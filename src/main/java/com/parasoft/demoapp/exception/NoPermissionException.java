package com.parasoft.demoapp.exception;

public class NoPermissionException extends Exception {

	private static final long serialVersionUID = -8824873739543282686L;
	
	public NoPermissionException(String message) {
        super(message);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

}
