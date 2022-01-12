package com.parasoft.demoapp.exception;

public class CategoryNameExistsAlreadyException extends ResourceExistsAlreadyException {

	private static final long serialVersionUID = -4002766204957183507L;

	public CategoryNameExistsAlreadyException(String message) {
        super(message);
    }

    public CategoryNameExistsAlreadyException(String message, Throwable cause) {
        super(message, cause);
    }

}
