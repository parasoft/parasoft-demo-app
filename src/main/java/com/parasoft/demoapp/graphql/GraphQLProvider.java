package com.parasoft.demoapp.graphql;

import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class GraphQLProvider {
    private GraphQL graphQL;

    @Value("classpath:static/schema.graphql")
    protected Resource graphqlSchemaResource;

    private final CategoryGraphQLDataFetcher categoryDataFetcher;

    private final LocationGraphQLDataFetcher locationDataFetcher;

    private final ItemGraphQLDataFetcher itemDataFetcher;

    private final CartItemGraphQLDataFetcher cartItemGraphQLDataFetcher;

    @PostConstruct
    public void init() throws IOException {
        GraphQLSchema graphQLSchema = buildSchema(graphqlSchemaResource.getInputStream());
        this.graphQL = GraphQL.newGraphQL(graphQLSchema)
                .queryExecutionStrategy(new AsyncExecutionStrategy(new CustomDataFetcherExceptionHandler()))
                .mutationExecutionStrategy(new AsyncSerialExecutionStrategy(new CustomDataFetcherExceptionHandler()))
                .build();
    }

    private GraphQLSchema buildSchema(InputStream sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
        categoryTypeWiring(builder);
        locationTypeWiring(builder);
        itemTypeWiring(builder);
        cartItemTypeWiring(builder);
        return builder
                .scalar(DateTimeScalar.getInstance())
                .build();
    }

    private void categoryTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategoryById", categoryDataFetcher.getCategoryById()));
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategoryByName", categoryDataFetcher.getCategoryByName()));
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategories", categoryDataFetcher.getCategories()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("deleteCategoryById", categoryDataFetcher.deleteCategoryById()));
    }

    private void locationTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWiring ->
                typeWiring.dataFetcher("getLocation", locationDataFetcher.getLocation()));
    }

    private void itemTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getItems", itemDataFetcher.getItems()));
    }

    private void cartItemTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCartItems", cartItemGraphQLDataFetcher.getCartItems()));
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }
}