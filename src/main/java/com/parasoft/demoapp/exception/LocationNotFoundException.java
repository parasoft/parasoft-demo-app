package com.parasoft.demoapp.exception;

public class LocationNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 6680421502256233440L;

    public LocationNotFoundException(String message) {
        super(message);
    }

    public LocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
