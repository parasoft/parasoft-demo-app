package com.parasoft.demoapp.model.global.preferences;

public enum IndustryType {

    DEFENSE("defense"), HEALTHCARE("healthcare"), GOVERNMENT("government"),
    RETAIL("retail"), AEROSPACE("aerospace"), OUTDOOR("outdoor");

    private String value;

    private IndustryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
