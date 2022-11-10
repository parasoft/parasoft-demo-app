package com.parasoft.demoapp.graphql;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.execution.*;
import graphql.language.SourceLocation;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GraphQLProvider {
    private GraphQL graphQL;

    @Value("classpath:static/schema.graphql")
    protected Resource graphqlSchemaResource;

    @Autowired
    private CategoryGraphQLDataFetcher categoryDataFetcher;

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
        return builder.build();
    }

    private void categoryTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategoryById", categoryDataFetcher.getCategoryById()));
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategories", categoryDataFetcher.getCategories()));
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

}