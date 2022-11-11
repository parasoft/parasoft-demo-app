package com.parasoft.demoapp.graphql;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GraphQLException extends RuntimeException implements GraphQLError {

    private final int httpStatusCode;
    private final Object data;

    public GraphQLException(int httpStatusCode, Object data, String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
        this.data = data;
        log.error(cause.getMessage(), cause);
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
        Map<String, Object> extensionsMap = new HashMap<>();
        extensionsMap.put("statusCode", httpStatusCode);
        extensionsMap.put("data", data);
        return extensionsMap;
    }
}