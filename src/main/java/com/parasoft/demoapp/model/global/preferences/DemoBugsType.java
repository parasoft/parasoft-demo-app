package com.parasoft.demoapp.model.global.preferences;

public enum DemoBugsType {

    INCORRECT_LOCATION_FOR_APPROVED_ORDERS("Incorrect location for approved orders"),
    INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER("Incorrect number of items (0) in summary of pending order"),
    REVERSE_ORDER_OF_ORDERS("Inconsistent order of orders between API and UI");

    private String value;

    private DemoBugsType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
