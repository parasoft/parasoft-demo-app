package com.parasoft.demoapp.exception;

public class RestEndpointNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = -9030292244772300748L;

	public RestEndpointNotFoundException(String message) {
        super(message);
    }

    public RestEndpointNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
