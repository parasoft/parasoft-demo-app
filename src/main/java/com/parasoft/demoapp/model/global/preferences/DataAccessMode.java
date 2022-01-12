package com.parasoft.demoapp.model.global.preferences;

public enum DataAccessMode {

    JDBC("JDBC"), SOAP("SOAP"), REST_JSON("REST (JSON)"), REST_XML("REST (XML)");

    private String value;

    private DataAccessMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
