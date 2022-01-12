package com.parasoft.demoapp.exception;

public class VirtualizeServerUrlException extends Exception{

	private static final long serialVersionUID = 2439021005481056826L;

	public VirtualizeServerUrlException(String message) {
        super(message);
    }

    public VirtualizeServerUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
