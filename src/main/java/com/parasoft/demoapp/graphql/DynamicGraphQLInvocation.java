package com.parasoft.demoapp.graphql;

import tools.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class DynamicGraphQLInvocation {

    private final GraphQLProvider graphQLProvider;

    private final ObjectProvider<DataLoaderRegistry> dataLoaderRegistryProvider;

    private final ObjectMapper objectMapper;

    @PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<String, Object>> invoke(@RequestBody GraphQLRequest request) {
        return execute(request);
    }

    @PostMapping(value = "/graphql", consumes = "application/graphql", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<String, Object>> invokeGraphQLDocument(
            @RequestBody String query,
            @RequestParam(name = "operationName", required = false) String operationName,
            @RequestParam(name = "variables", required = false) String variables) {
        return execute(new GraphQLRequest(query, operationName, parseVariables(variables)));
    }

    @GetMapping(value = "/graphql", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<String, Object>> invokeQuery(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "operationName", required = false) String operationName,
            @RequestParam(name = "variables", required = false) String variables) {
        return execute(new GraphQLRequest(query, operationName, parseVariables(variables)));
    }

    private CompletableFuture<Map<String, Object>> execute(GraphQLRequest request) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(request.query())
                .operationName(request.operationName())
                .variables(request.variables() == null ? Map.of() : request.variables());

        DataLoaderRegistry dataLoaderRegistry = dataLoaderRegistryProvider.getIfAvailable();
        if (dataLoaderRegistry != null) {
            executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        }

        return graphQLProvider.getGraphQL().executeAsync(executionInputBuilder.build())
                .thenApply(ExecutionResult::toSpecification);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseVariables(String variables) {
        if (!StringUtils.hasText(variables)) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(variables, Map.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("GraphQL variables must be a JSON object.", exception);
        }
    }

    public record GraphQLRequest(String query, String operationName, Map<String, Object> variables) {
    }
}
