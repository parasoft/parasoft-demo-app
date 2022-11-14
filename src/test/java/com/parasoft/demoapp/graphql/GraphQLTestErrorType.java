package com.parasoft.demoapp.graphql;

import lombok.Getter;

enum GraphQLTestErrorType {
    UNAUTHORIZED("Current user is not authorized."),
    FORBIDDEN("Current user does not have permission.");

    @Getter
    private String value;

    GraphQLTestErrorType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}