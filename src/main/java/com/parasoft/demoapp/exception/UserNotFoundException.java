package com.parasoft.demoapp.exception;

public class UserNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 3513153849472806194L;

	public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
