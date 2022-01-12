package com.parasoft.demoapp.exception;

public class ItemNameExistsAlreadyException extends ResourceExistsAlreadyException {
    private static final long serialVersionUID = -519642960759975231L;

    public ItemNameExistsAlreadyException(String message) {
        super(message);
    }

    public ItemNameExistsAlreadyException(String message, Throwable cause) {
        super(message, cause);
    }
}
