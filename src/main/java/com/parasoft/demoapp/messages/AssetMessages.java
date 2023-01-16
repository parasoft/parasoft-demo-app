package com.parasoft.demoapp.messages;

public class AssetMessages extends Messages {

	public static final String NO_CATEGORIES = "No categories.";
	public static final String CATEGORY_ID_NOT_FOUND = "Category with ID {0} is not found.";
	public static final String CATEGORY_NAME_NOT_FOUND = "Category with name {0} is not found.";
	public static final String CATEGORY_ID_CANNOT_BE_NULL = "Category ID cannot be null.";
	public static final String CATEGORY_NAME_CANNOT_BE_BLANK = "Category name cannot be an empty string(null, '' or '  ').";
	public static final String CATEGORY_NAME_EXISTS_ALREADY = "Category with name {0} already exists.";
	public static final String THERE_IS_AT_LEAST_ONE_ITEM_IN_THE_CATEGORY =
			"Unable to delete the category with ID {0} because there are items in the category.";
	public static final String DESCRIPTION_CANNOT_BE_BLANK = "Description cannot be an empty string(null, '' or '  ').";
	public static final String IMAGE_PATH_CANNOT_BE_BLANK = "Image path cannot be an empty string(null, '' or '  ').";
	public static final String NO_ITEMS = "No items.";
	public static final String ITEM_ID_NOT_FOUND = "Item with ID {0} is not found.";
	public static final String ITEM_ID_CANNOT_BE_NULL = "Item ID cannot be null.";
	public static final String ITEM_NAME_NOT_FOUND = "Item with name {0} is not found.";
	public static final String ITEM_NAME_CANNOT_BE_BLANK = "Item name cannot be an empty string(null, '' or '  ').";
	public static final String ITEM_NAME_EXISTS_ALREADY = "Item name already exists.";
	public static final String IN_STOCK_OF_ITEM_IS_INSUFFICIENT = "In stock of item {0} is insufficient.";
	public static final String IN_STOCK_CANNOT_BE_NULL = "In stock cannot be null.";
	public static final String IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER = "In stock cannot be a negative number.";
	public static final String REGION_CANNOT_BE_NULL = "Region cannot be null.";
	public static final String INCORRECT_REGION_IN_CURRENT_INDUSTRY = "The region does not belong to the current industry.";
	public static final String USER_ID_CANNOT_BE_NULL = "User ID cannot be null.";
	public static final String QUANTITY_CANNOT_BE_NULL = "Quantity cannot be null.";
	public static final String QUANTITY_CANNOT_BE_ZERO = "Quantity cannot be zero.";
	public static final String QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO = "Quantity cannot be a negative number or zero.";
	public static final String INCLUDES_SHOPPING_CART_IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT =
			"There is not enough inventory to fulfill your order. Edit your requisition and resubmit.";
	public static final String INVENTORY_IS_NOT_ENOUGH = "Inventory is not enough.";
	public static final String THIS_ITEM_IS_NOT_IN_THE_SHOPPING_CART = "This item is not in the shopping cart.";
	public static final String NO_CART_ITEMS = "No cart items.";
	public static final String THERE_IS_NO_CART_ITEM_CORRESPONDING_TO = "There is no cart item corresponding to {0}.";
	public static final String SEARCH_FIELD_CANNOT_BE_BLANK = "Search field cannot be an empty string(null, '' or '  ').";
	public static final String INVENTORY_NOT_FOUND_WITH_ITEM_ID = "Inventory with item ID {0} is not found.";
	public static final String INCORRECT_OPERATION = "Incorrect operation";
	public static final String REQUEST_PARAMETER_CANNOT_BE_NULL="The request parameter cannot be null.";
	public static final String OPERATION_QUANTITY_CANNOT_BE_NULL = "The quantity for item inventory deduction or addition can not be null.";
	public static final String INVALID_REQUEST = "Invalid request body.";

    public AssetMessages() {
		super("i18n/messages");
	}

}
