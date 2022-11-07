package com.parasoft.demoapp.model.global.preferences;

public enum WebServiceMode {
    REST_API("REST API"), GRAPHQL("GraphQL");

    private String value;

    private WebServiceMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
