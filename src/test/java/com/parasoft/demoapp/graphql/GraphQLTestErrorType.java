package com.parasoft.demoapp.graphql;

import lombok.Getter;

enum GraphQLTestErrorType {
    UNAUTHORIZED("Current user is not authorized.");

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