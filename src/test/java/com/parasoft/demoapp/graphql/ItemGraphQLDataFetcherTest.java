package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.model.industry.ItemEntity;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static com.parasoft.demoapp.model.industry.RegionType.LOCATION_1;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@GraphQLTest
public class ItemGraphQLDataFetcherTest {

    private static final String GET_ITEMS_GRAPHQL_RESOURCE = "graphql/items/getItems.graphql";
    private static final String GET_ITEMS_DATA_JSON_PATH = DATA_PATH + ".getItems";
    private static final String GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/getItem.graphql";
    private static final String GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH = DATA_PATH + ".getItemByItemId";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ResetEntrance resetEntrance;

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
        resetEntrance.run();
    }

    @Test
    public void test_getItems_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1);
        variables.put("regions", LOCATION_1.name());
        variables.put("searchString","Blue");
        variables.put("page", 0);
        variables.put("size", 10);
        variables.put("sort", "name");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEMS_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(GET_ITEMS_DATA_JSON_PATH)
                .as(PageInfo.class)
                .hasFieldOrPropertyWithValue("totalElements", 1L)
                .hasFieldOrPropertyWithValue("sort", "name: ASC")
                .hasFieldOrPropertyWithValue("totalPages", 1)
                .hasFieldOrPropertyWithValue("size", 10)
                .and()
                .assertThatField(GET_ITEMS_DATA_JSON_PATH + ".content")
                .asListOf(ItemEntity.class)
                .has(new Condition<ItemEntity>(c -> c.getName().equals("Blue Sleeping Bag"),"name Blue Sleeping Bag"), Index.atIndex(0))
                .size()
                .isEqualTo(1);
    }

    @Test
    public void test_getItems_illegalSort() throws IOException {
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("sort", "sort");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEMS_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("No property sort found for type ItemEntity!");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(GET_ITEMS_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getItems_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_ITEMS_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(GET_ITEMS_DATA_JSON_PATH).isNull();
    }

    @Test
    public void getItemByItemId_normal() throws Throwable {
        String itemId = "1";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH)
                .as(ItemEntity.class)
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void getItemByItemId_notFound() throws Throwable {
        String itemId = "0";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Item with ID 0 is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void getItemByItemId_incorrectAuthentication() throws Throwable {
        String itemId = "1";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void getItemByItemId_invalidItemId() throws Throwable {
        String itemId = "notNumber";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: For input string: \"notNumber\"");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void getItemByItemId_nullItemId() throws Throwable {
        String itemId = null;
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getExtensions().get("classification")).isEqualTo("ValidationError");
                });
    }

    @Test
    public void getItemByItemId_emptyItemId() throws Throwable {
        String itemId = "";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Map has no value for 'itemId'");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }
}
