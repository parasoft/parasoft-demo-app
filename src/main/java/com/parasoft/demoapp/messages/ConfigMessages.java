package com.parasoft.demoapp.messages;

public class ConfigMessages extends Messages {

	public static final String USER_HAS_NO_PERMISSION = "Current user does not have permission.";
	public static final String CAN_NOT_ADD_RESOURCES_STATIC_LOCATION = "Unable to create directory for image uploads: {0}.";
	public static final String MAP_RESOURCES_STATIC_PATTERN = "Map resources static pattern [{0}] with location [file:{1}].";
	public static final String CAN_NOT_DEFINE_MULTIPART_TEMPORARY_LOCATION = "Unable to create multipart temporary directory with: {1}.";
	public static final String MULTIPART_TEMPORARY_LOCATION = "Multipart upload temporary location is: {0}.";
	
	public static final String GENERAL_API_DESCRIPTION = "general.api.description";
	public static final String REGULAR_API_DESCRIPTION = "regular.api.description";
	public static final String GATEWAY_API_DESCRIPTION = "gateway.api.description";

	public ConfigMessages() {
		super("i18n/messages");
	}

}
