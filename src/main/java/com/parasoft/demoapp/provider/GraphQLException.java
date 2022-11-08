package com.parasoft.demoapp.provider;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import java.util.List;
import java.util.Map;
public class GraphQLException extends RuntimeException implements GraphQLError {

    public GraphQLException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }
}
