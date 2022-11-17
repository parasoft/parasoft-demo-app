package com.parasoft.demoapp.graphql;

import lombok.Getter;

enum GraphQLTestErrorType {
    UNAUTHORIZED("Current user is not authorized."),
    FORBIDDEN("Current user does not have permission."),
    NOT_FOUND("Item with ID {0} is not found."),
    BAD_REQUEST("There is not enough inventory to fulfill your order. Edit your requisition and resubmit."),
    QUANTITY_CANNOT_BE_ZERO("Quantity cannot be zero."),
    QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO("Quantity cannot be a negative number or zero.");

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