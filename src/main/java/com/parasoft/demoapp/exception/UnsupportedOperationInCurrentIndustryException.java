package com.parasoft.demoapp.exception;

public class UnsupportedOperationInCurrentIndustryException extends Exception{

	private static final long serialVersionUID = -1081003139856218121L;

	public UnsupportedOperationInCurrentIndustryException(String message) {
        super(message);
    }

    public UnsupportedOperationInCurrentIndustryException(String message, Throwable cause) {
        super(message, cause);
    }
}
