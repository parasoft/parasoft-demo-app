package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.RegionType;
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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class GraphQLProvider {
    // The SDL and executable schema must change together when the active skin changes.
    private final AtomicReference<GraphQLSchemaSnapshot> schemaSnapshot = new AtomicReference<>();

    private static final String REGION_TYPE_VALUES_PLACEHOLDER = "__REGION_TYPE_VALUES__";

    private static final String REGION_TYPE_VALUE_INDENT = "    ";

    @Value("classpath:static/schema.graphqls")
    protected Resource graphqlSchemaResource;

    private String schemaTemplate;

    private final CategoryGraphQLDataFetcher categoryDataFetcher;

    private final LocationGraphQLDataFetcher locationDataFetcher;

    private final OrderGraphQLDataFetcher orderGraphQLDataFetcher;

    private final ItemGraphQLDataFetcher itemDataFetcher;

    private final CartItemGraphQLDataFetcher cartItemGraphQLDataFetcher;

    @PostConstruct
    public void init() throws IOException {
        try (InputStream schemaInput = graphqlSchemaResource.getInputStream()) {
            schemaTemplate = new String(schemaInput.readAllBytes(), StandardCharsets.UTF_8);
        }
        onIndustryChange(IndustryRoutingDataSource.currentIndustry);
    }

    public void onIndustryChange(IndustryType industryType) {
        String schemaDefinition = renderSchemaDefinition(industryType);
        GraphQLSchema graphQLSchema = buildSchema(schemaDefinition);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema)
                .queryExecutionStrategy(new AsyncExecutionStrategy(new CustomDataFetcherExceptionHandler()))
                .mutationExecutionStrategy(new AsyncSerialExecutionStrategy(new CustomDataFetcherExceptionHandler()))
                .build();
        schemaSnapshot.set(new GraphQLSchemaSnapshot(graphQL, schemaDefinition));
        log.info("Refreshed GraphQL schema for industry {}", industryType);
    }

    public GraphQL getGraphQL() {
        return getSchemaSnapshot().graphQL;
    }

    public String getSchemaDefinition() {
        return getSchemaSnapshot().schemaDefinition;
    }

    private GraphQLSchemaSnapshot getSchemaSnapshot() {
        GraphQLSchemaSnapshot snapshot = schemaSnapshot.get();
        if (snapshot == null) {
            throw new IllegalStateException("GraphQL schema has not been initialized yet.");
        }
        return snapshot;
    }

    private String renderSchemaDefinition(IndustryType industryType) {
        String regionTypes = RegionType.getRegionsByIndustryType(industryType).stream()
                .map(RegionType::name)
                .collect(Collectors.joining("\n" + REGION_TYPE_VALUE_INDENT));
        return schemaTemplate.replace(REGION_TYPE_VALUES_PLACEHOLDER, regionTypes);
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
        categoryTypeWiring(builder);
        locationTypeWiring(builder);
        orderTypeWiring(builder);
        itemTypeWiring(builder);
        cartItemTypeWiring(builder);
        return builder
                .scalar(DateTimeScalar.getInstance())
                .scalar(LongScalar.getInstance())
                .build();
    }

    private void categoryTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategoryById", categoryDataFetcher.getCategoryById()));
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategoryByName", categoryDataFetcher.getCategoryByName()));
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCategories", categoryDataFetcher.getCategories()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("deleteCategoryById", categoryDataFetcher.deleteCategoryById()));
        builder.type("Mutation", typeWiring ->
                typeWiring.dataFetcher("addCategory", categoryDataFetcher.addCategory()));
        builder.type("Mutation", typeWriting ->
                typeWriting.dataFetcher("updateCategory", categoryDataFetcher.updateCategory()));
    }

    private void locationTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWiring ->
                typeWiring.dataFetcher("getLocation", locationDataFetcher.getLocation()));
        builder.type("Query", typeWiring ->
                typeWiring.dataFetcher("getAllRegionTypesOfCurrentIndustry", locationDataFetcher.getAllRegionTypesOfCurrentIndustry()));
    }

    private void orderTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getOrderByOrderNumber", orderGraphQLDataFetcher.getOrderByOrderNumber()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("createOrder", orderGraphQLDataFetcher.createOrder()));
        builder.type("Query", typeWriting-> typeWriting.dataFetcher("getOrders", orderGraphQLDataFetcher.getOrders()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("updateOrderByOrderNumber", orderGraphQLDataFetcher.updateOrderByOrderNumber()));
        builder.type("Query", typeWriting-> typeWriting.dataFetcher("getUnreviewedNumber", orderGraphQLDataFetcher.getUnreviewedNumber()));
    }

    private void itemTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getItems", itemDataFetcher.getItems()));
        builder.type("Query", typeWiring ->
                typeWiring.dataFetcher("getItemByItemId", itemDataFetcher.getItemByItemId()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("updateItemInStockByItemId", itemDataFetcher.updateItemInStockByItemId()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("deleteItemByName", itemDataFetcher.deleteItemByName()));
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getItemByName", itemDataFetcher.getItemByName()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("addNewItem", itemDataFetcher.addNewItem()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("deleteItemByItemId", itemDataFetcher.deleteItemByItemId()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("updateItemByItemId", itemDataFetcher.updateItemByItemId()));
    }

    private void cartItemTypeWiring(RuntimeWiring.Builder builder) {
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCartItems", cartItemGraphQLDataFetcher.getCartItems()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("addItemInCart", cartItemGraphQLDataFetcher.addItemInCart()));
        builder.type("Mutation", typeWiring ->
                typeWiring.dataFetcher("removeCartItem", cartItemGraphQLDataFetcher.removeCartItem()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("removeAllCartItems", cartItemGraphQLDataFetcher.removeAllCartItems()));
        builder.type("Query", typeWriting -> typeWriting.dataFetcher("getCartItemByItemId", cartItemGraphQLDataFetcher.getCartItemByItemId()));
        builder.type("Mutation", typeWriting -> typeWriting.dataFetcher("updateItemInCart", cartItemGraphQLDataFetcher.updateItemInCart()));
    }

    @Bean
    public GraphQL graphQL() {
        return getGraphQL();
    }

    private static class GraphQLSchemaSnapshot {
        private final GraphQL graphQL;
        private final String schemaDefinition;

        private GraphQLSchemaSnapshot(GraphQL graphQL, String schemaDefinition) {
            this.graphQL = graphQL;
            this.schemaDefinition = schemaDefinition;
        }
    }
}
