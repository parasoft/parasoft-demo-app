package com.parasoft.demoapp.exception;

public class DemoBugsIntroduceFailedException extends Exception{

	private static final long serialVersionUID = -5353652823685876731L;

	public DemoBugsIntroduceFailedException(String message) {
        super(message);
    }

    public DemoBugsIntroduceFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
