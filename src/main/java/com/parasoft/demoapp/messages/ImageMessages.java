package com.parasoft.demoapp.messages;

public class ImageMessages extends Messages {

	public static final String IMAGE_FILE_IS_EMPTY_OR_NOT_EXISTING = "Image file is empty or not exist.";
	public static final String IMAGE_WITH_NO_SUFFIX_NAME = "Image filename has no suffix.";
	public static final String IMAGE_SUFFIX_NAME_IS_NOT_SUPPORTED = "Images of type {0} are not supported.";
	public static final String IMAGE_FAILED_TO_DELETE = "Failed to delete image {0}.";
	public static final String IMAGE_NOT_FOUND = "Image {0} not found.";

	public ImageMessages() {
		super("i18n/messages");
	}
    
}
