package com.parasoft.demoapp.graphql;

public class ScalarUtil {
    public static String typeName(Object input) {
        if (input == null) {
            return "null";
        }

        return input.getClass().getSimpleName();
    }
}