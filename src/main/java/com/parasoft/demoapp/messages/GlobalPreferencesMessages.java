package com.parasoft.demoapp.messages;

public class GlobalPreferencesMessages extends Messages {

    public static final String THERE_ARE_MORE_THAN_ONE_PREFERENCES = "There are {0} global preferences. The system only supports 1.";
    public static final String THERE_IS_NO_CURRENT_GLOBAL_PREFERENCES = "Global preferences are not configured.";
    public static final String GLOBAL_PREFERENCES_ID_CANNOT_BE_NULL = "Global preferences ID should not be null.";
    public static final String INDUSTRY_CANNOT_BE_NULL = "Industry should not be null.";
    public static final String ADVERTISING_ENABLED_CANNOT_BE_NULL = "advertisingEnabled should not be null.";
    public static final String SOME_UPLOADED_IMAGES_FAILED_TO_REMOVE = "Failed to remove some uploaded images of {0} industry.";
    public static final String REST_ENDPOINT_ENTITY_ID_CAN_NOT_BE_NULL = "ID of rest endpoint entity can not be null.";
    public static final String ID_OF_REST_ENDPOINT_ENTITY_NOT_FOUND = "ID {0} of rest endpoint entity not found.";
    public static final String INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_URL = "Parasoft Virtualize server URL error: {0} -- can not establish connection with Virtualize server  -- {1}";
    public static final String INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH = "Invalid Virtualize server path: {0}";
    public static final String INVALIDATE_PARASOFT_VIRTUALIZE_GROUP_ID = "Invalid Virtualize group ID: {0}";
    public static final String INVALID_URL = "Rest endpoint error: {0} -- invalid URL.";
    public static final String INVALID_CATEGORIES_URL = "Rest endpoint error: {0} -- invalid categories URL.";
    public static final String INVALID_ITEMS_URL = "Rest endpoint error: {0} -- invalid items URL.";
    public static final String INVALID_CART_ITEMS_URL = "Rest endpoint error: {0} -- invalid cart items URL.";
    public static final String INVALID_ORDERS_URL = "Rest endpoint error: {0} -- invalid orders URL.";
    public static final String INVALID_LOCATIONS_URL = "Rest endpoint error: {0} -- invalid locations URL.";
    public static final String INVALID_GRAPHQL_URL = "Endpoint error: {0} -- invalid graphql URL.";
    public static final String BLANK_URL = "Url should not be blank";
    public static final String INDUSTRY_HAS_NOT_IMPLEMENTED = "Industry {0} has not implemented yet.";
    public static final String LABELS_CANNOT_BE_NULL = "Labels cannot be null.";
    public static final String LOCALIZATION_LANGUAGE_TYPE_CANNOT_BE_NULL = "Localization language type cannot be null.";
    public static final String ILLEGAL_LABEL_NAME = "Illegal label name: {0}.";
    public static final String LABEL_OVERRIDED_CANNOT_BE_NULL = "LabelOverrided cannot be null.";
    public static final String ORDER_SERVICE_SEND_TO_CANNOT_BE_NULL = "orderServiceSendTo cannot be null or empty.";
    public static final String ORDER_SERVICE_LISTEN_ON_CANNOT_BE_NULL = "orderServiceListenOn cannot be null or empty.";
    public static final String MQTYPE_MUST_NOT_BE_NULL = "MqType must not be null.";
    public static final String WEBSERVICEMODE_MUST_NOT_BE_NULL = "WebServiceMode must not be null.";
    public static final String LABEL_CANNOT_BE_FOUND = "Label key {0} cannot be found.";
    public static final String KAFKA_SERVER_IS_NOT_AVAILABLE = "Can not establish connection with Kafka broker - {0}";
    public static final String RABBITMQ_SERVER_IS_NOT_AVAILABLE = "Can not establish connection with RabbitMQ server - {0}";

    public GlobalPreferencesMessages() {
        super("i18n/messages");
    }
}
