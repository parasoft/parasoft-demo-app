package com.parasoft.demoapp.exception;

public class IncorrectOperationException extends Exception {

	private static final long serialVersionUID = -6395348378133558151L;
	
	public IncorrectOperationException(String message) {
        super(message);
    }

    public IncorrectOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
