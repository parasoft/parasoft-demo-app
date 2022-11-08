package com.parasoft.demoapp.graphql;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
public class GraphQLException extends RuntimeException implements GraphQLError {

    private final int httpStatusCode;

    public GraphQLException(int httpStatusCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Collections.singletonMap("statusCode", httpStatusCode);
    }
}
