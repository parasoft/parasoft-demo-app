package com.parasoft.demoapp.exception;

public class InventoryNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 4913153148372803947L;

	public InventoryNotFoundException(String message) {
        super(message);
    }

}
