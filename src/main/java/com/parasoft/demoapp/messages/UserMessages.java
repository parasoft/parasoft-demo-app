package com.parasoft.demoapp.messages;

public class UserMessages extends Messages {
	
	public static final String USERNAME_CANNOT_NULL = "Username should not be null.";
	public static final String USERNAME_EXISTS_ALREADY = "Username {0} already exists.";
	public static final String PASSWORD_CANNOT_NULL = "Password should not be null.";
	public static final String USER_CANNOT_NULL = "User should not be null.";
	public static final String USERNAME_NOT_FOUND = "User with name {0} is not found.";
	public static final String USER_ID_NOT_FOUND = "User with ID {0} is not found.";
	public static final String USER_ID_CANNOT_NULL = "User ID should not be null.";
	public static final String ROLE_NAME_EXISTS_ALREADY = "Role name {0} already exists.";
	public static final String ROLE_NAME_NOT_FOUND = "Role with name {0} is not found.";

	public UserMessages() {
		super("i18n/messages");
	}
    
}
