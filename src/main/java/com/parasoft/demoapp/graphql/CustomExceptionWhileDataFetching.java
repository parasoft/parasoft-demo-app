package com.parasoft.demoapp.graphql;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomExceptionWhileDataFetching implements GraphQLError {
    private final String message;
    private final List<SourceLocation> locations;

    private Map<String, Object> extensions;

    public CustomExceptionWhileDataFetching(Throwable exception, SourceLocation sourceLocation) {
        this.locations = Collections.singletonList(sourceLocation);
        this.message = exception.getMessage();
        if (exception instanceof GraphQLError) {
            extensions =  ((GraphQLError) exception).getExtensions();
        }
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return this.locations;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.DataFetchingException;
    }
}
