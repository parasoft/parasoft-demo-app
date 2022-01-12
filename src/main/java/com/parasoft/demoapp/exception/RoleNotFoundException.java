package com.parasoft.demoapp.exception;

public class RoleNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = -4979202685738689579L;

	public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
