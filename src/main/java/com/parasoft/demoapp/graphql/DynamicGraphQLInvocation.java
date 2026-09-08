package com.parasoft.demoapp.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.spring.web.servlet.ExecutionInputCustomizer;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.CompletableFuture;

@Component
@Primary
@RequiredArgsConstructor
public class DynamicGraphQLInvocation implements GraphQLInvocation {

    private final GraphQLProvider graphQLProvider;

    private final ExecutionInputCustomizer executionInputCustomizer;

    @Autowired(required = false)
    private DataLoaderRegistry dataLoaderRegistry;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables());
        if (dataLoaderRegistry != null) {
            executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        }

        return executionInputCustomizer.customizeExecutionInput(executionInputBuilder.build(), webRequest)
                .thenCompose(executionInput -> graphQLProvider.getGraphQL().executeAsync(executionInput));
    }
}
