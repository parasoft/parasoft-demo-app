package com.parasoft.demoapp.exception;

public class LocalizationException extends Exception{

	private static final long serialVersionUID = 4018926015763789277L;

	public LocalizationException(String message) {
        super(message);
    }

    public LocalizationException(String message, Throwable cause) {
        super(message, cause);
    }

}
