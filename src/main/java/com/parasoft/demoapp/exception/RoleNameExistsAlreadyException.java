package com.parasoft.demoapp.exception;

public class RoleNameExistsAlreadyException extends ResourceExistsAlreadyException {

	private static final long serialVersionUID = 3617489274224083639L;

	public RoleNameExistsAlreadyException(String message) {
        super(message);
    }

    public RoleNameExistsAlreadyException(String message, Throwable cause) {
        super(message, cause);
    }

}
