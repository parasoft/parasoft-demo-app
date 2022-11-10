package com.parasoft.demoapp.graphql;

import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.language.SourceLocation;

import java.util.concurrent.CompletableFuture;

public class CustomDataFetcherExceptionHandler implements DataFetcherExceptionHandler {

    private DataFetcherExceptionHandlerResult onCustomException(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        SourceLocation sourceLocation = handlerParameters.getSourceLocation();
        CustomExceptionWhileDataFetching error = new CustomExceptionWhileDataFetching(exception, sourceLocation);
        return DataFetcherExceptionHandlerResult.newResult().error(error).build();
    }

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        return CompletableFuture.completedFuture(onCustomException(handlerParameters));
    }
}
