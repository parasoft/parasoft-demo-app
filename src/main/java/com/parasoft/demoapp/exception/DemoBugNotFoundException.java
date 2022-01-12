package com.parasoft.demoapp.exception;

public class DemoBugNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 6478464603353134045L;

	public DemoBugNotFoundException(String message) {
        super(message);
    }

    public DemoBugNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
