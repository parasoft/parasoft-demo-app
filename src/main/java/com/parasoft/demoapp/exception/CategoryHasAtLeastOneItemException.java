package com.parasoft.demoapp.exception;

public class CategoryHasAtLeastOneItemException extends Exception {

    private static final long serialVersionUID = 3566516924532604546L;

    public CategoryHasAtLeastOneItemException(String message) {
        super(message);
    }

    public CategoryHasAtLeastOneItemException(String message, Throwable cause) {
        super(message, cause);
    }

}
