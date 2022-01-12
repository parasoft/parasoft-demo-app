package com.parasoft.demoapp.exception;

public class UploadedImageCanNotDeleteException extends Exception{

	private static final long serialVersionUID = -3923711785232735734L;

	public UploadedImageCanNotDeleteException(String message) {
        super(message);
    }

    public UploadedImageCanNotDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
