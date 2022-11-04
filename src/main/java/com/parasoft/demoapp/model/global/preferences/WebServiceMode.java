package com.parasoft.demoapp.model.global.preferences;

public enum WebServiceMode {
    REST("REST"),
    GRAPHQL("graphql");

    private String value;

    private WebServiceMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
