package com.parasoft.demoapp.exception;

public class UsernameExistsAlreadyException extends ResourceExistsAlreadyException {

	private static final long serialVersionUID = -9122568982956683864L;

	public UsernameExistsAlreadyException(String message) {
        super(message);
    }

    public UsernameExistsAlreadyException(String message, Throwable cause) {
        super(message, cause);
    }

}
