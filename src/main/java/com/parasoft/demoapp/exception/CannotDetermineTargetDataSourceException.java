package com.parasoft.demoapp.exception;

public class CannotDetermineTargetDataSourceException extends RuntimeException {

	private static final long serialVersionUID = 9003691600825292357L;

    public CannotDetermineTargetDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
