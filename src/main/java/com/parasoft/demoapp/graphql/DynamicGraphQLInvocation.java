package com.parasoft.demoapp.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class DynamicGraphQLInvocation {

    private final GraphQLProvider graphQLProvider;

    private final ObjectProvider<DataLoaderRegistry> dataLoaderRegistryProvider;

    @PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ExecutionResult> invoke(@RequestBody GraphQLRequest request) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(request.query())
                .operationName(request.operationName())
                .variables(request.variables() == null ? Map.of() : request.variables());

        DataLoaderRegistry dataLoaderRegistry = dataLoaderRegistryProvider.getIfAvailable();
        if (dataLoaderRegistry != null) {
            executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        }

        return graphQLProvider.getGraphQL().executeAsync(executionInputBuilder.build());
    }

    public record GraphQLRequest(String query, String operationName, Map<String, Object> variables) {
    }
}
