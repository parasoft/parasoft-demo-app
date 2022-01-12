package com.parasoft.demoapp.messages;

public class DatabaseOperationMessages extends Messages {

	public static final String INITIALIZE_DATABASES = "initialize.databases";
	public static final String DATABASES_INITIALIZED_COMPLETELY = "databases.initialized.completely";
	public static final String DATABASES_INITIALIZED_ALREADY = "databases.initialized.already";
	public static final String SWITCH_INDUSTRY_DATABASE_TO = "switch.industry.database.to";
	public static final String CURRENT_INDUSTRY = "current.industry.is";
	public static final String WRITE_DEFAULT_USERS = "write.default.users";
	public static final String WRITE_DEFAULT_CATEGORIES_ITEMS = "write.default.categories.items";
	public static final String WRITE_DONE = "write.done";
	public static final String WRITE_GLOBAL_PREFERENCES = "write.global.preferences";
	public static final String DATABASES_NOT_CLEAR = "database.not.clear";
	public static final String WRITE_DEFAULT_LOCATIONS = "write.default.locations";
	public static final String WRITE_DEFAULT_ORDERS = "write.default.orders";
	public static final String FAILED_TO_WRITE_DEFAULT_ORDERS = "failed.to.write.default.orders";
	public static final String WRITE_DEFAULT_OVERRIDED_LABELS = "write.default.labels";
	public static final String FAILED_TO_WRITE_DEFAULT_OVERRIDED_LABELS = "failed.to.write.default.labels";

	public static final String RECREATE_DEFAULT_DATA = "recreate.default.data";
	public static final String DEFAULT_DATA_RECREATED = "default.data.recreated";
	public static final String PREPARE_TO_REMOVE_DATA = "prepare.to.remove.data";
	public static final String REMOVING_DATA_OF = "removing.data.of";
	public static final String DATA_REMOVED = "data.removed";

    public DatabaseOperationMessages() {
		super("i18n/messages");
	}
    
}
