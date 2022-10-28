package com.parasoft.demoapp.messages;

public class OrderMessages extends Messages {

	public static final String LOCATION_NOT_FOUND = "Location not found.";
	public static final String LOCATION_CANNOT_BE_BLANK = "Location should not be blank(null, '' or '  ').";
	public static final String RECEIVER_ID_CANNOT_BE_BLANK = "Receiver ID should not be blank(null, '' or '  ').";
	public static final String EVENT_ID_CANNOT_BE_BLANK = "Event ID should not be blank(null, '' or '  ').";
	public static final String EVENT_NUMBER_CANNOT_BE_BLANK = "Event number should not be blank(null, '' or '  ').";
	public static final String UNSUPPORTED_OPERATION_IN_CURRENT_INDUSTRY = "Current industry {0} does not support this operation.";
	public static final String USER_ID_CANNOT_BE_NULL = "User ID should not be null.";
	public static final String ORDER_NUMBER_CANNOT_BE_BLANK = "Order number should not be blank(null, '' or '  ').";
	public static final String USER_ROLE_NAME_CANNOT_BE_BLANK = "User role name should not be blank(null, '' or '  ').";
	public static final String ORDER_REVIEW_STATUS_OF_PURCHASER_SHOULD_NOT_BE_NULL = "The order review status of purchaser should not be null.";
	public static final String ORDER_REVIEW_STATUS_OF_APPROVER_SHOULD_NOT_BE_NULL = "The order review status of approver should not be null.";
	public static final String STATUS_CANNOT_BE_NULL = "Status should not be null.";
	public static final String REGION_CANNOT_BE_NULL = "Region should not be null.";
	public static final String THERE_IS_NO_ORDER_CORRESPONDING_TO = "There is no order corresponding to {0}.";
	public static final String ORDER_STATUS_CHANGED = "Order status is changed.";
	public static final String NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS = "You do not have permission to change the status of order to {0}.";
	public static final String CANNOT_SET_TRUE_TO_FALSE = "Cannot set the review status from true to false.";
	public static final String ALREADY_MODIFIED_THIS_ORDER = "You have already modified this order status and cannot operate it again.";
	public static final String ITEM_HAS_ALREADY_BEEN_REMOVED = "The item no longer exists and cannot be operated.";
	public static final String LOCATION_NOT_FOUND_FOR_REGION = "Location not found for region {0}.";
	public static final String FAILED_TO_INTRODUCES_INCORRECT_LOCATION_BUG = "Failed to introduce demo bug: {0}.";
    public static final String HAVE_NOT_IMPLEMENTED_BUG_FOR_CURRENT_INDUSTRY = "Have not implemented bug {0} for current industry.";
    public static final String USERNAME_CANNOT_BE_NULL = "Username should not be null.";
    public static final String THE_ORDER_IS_PROCESSED = "The order is processed.";

    public OrderMessages() {
		super("i18n/messages");
	}
    
}
