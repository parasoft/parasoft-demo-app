package com.parasoft.demoapp.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 5068322901703889137L;

	public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
