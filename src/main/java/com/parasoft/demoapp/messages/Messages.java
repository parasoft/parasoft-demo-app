package com.parasoft.demoapp.messages;

import java.util.ResourceBundle;

public class Messages {
	
	private ResourceBundle resourceBundle;
	
	public Messages(String baseName) {
		this.resourceBundle = ResourceBundle.getBundle(baseName);
	}
	
	public String getString(String key) {
		return resourceBundle.getString(key);
	}
}
