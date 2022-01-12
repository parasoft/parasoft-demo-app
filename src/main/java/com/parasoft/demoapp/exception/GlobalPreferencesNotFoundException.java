package com.parasoft.demoapp.exception;

public class GlobalPreferencesNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 5446047962714538738L;

	public GlobalPreferencesNotFoundException(String message) {
        super(message);
    }

    public GlobalPreferencesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
