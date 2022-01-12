package com.parasoft.demoapp.exception;

public class GlobalPreferencesMoreThanOneException extends Exception {

    private static final long serialVersionUID = 2763651269733004828L;

    public GlobalPreferencesMoreThanOneException(String message) {
        super(message);
    }

    public GlobalPreferencesMoreThanOneException(String message, Throwable cause) {
        super(message, cause);
    }

}
