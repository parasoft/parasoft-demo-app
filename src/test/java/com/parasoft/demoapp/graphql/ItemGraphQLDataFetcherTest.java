package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static com.parasoft.demoapp.messages.AssetMessages.ITEM_NAME_CANNOT_BE_BLANK;
import static com.parasoft.demoapp.model.industry.RegionType.LOCATION_1;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class ItemGraphQLDataFetcherTest {
    private static final String ITEM_GRAPHQL_RESOURCE = "graphql/items/getItems.graphql";
    private static final String ITEM_DATA_JSON_PATH = DATA_PATH + ".getItems";

    private static final String UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/updateItemInStockByItemId.graphql";
    private static final String UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH = DATA_PATH + ".updateItemInStockByItemId";

    private static final String DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE = "graphql/items/deleteItemByName.graphql";
    private static final String DELETE_ITEM_BY_NAME_DATA_JSON_PATH = DATA_PATH + ".deleteItemByName";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private GlobalPreferencesService globalPreferencesService;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
    }

    @Before
    public void conditionalBefore() {
        if ("test_deleteItemByName_normal".equals(testName.getMethodName())) {
            resetDatabase();
        }
    }

    @After
    public void conditionalAfter() {
        if ("test_deleteItemByName_normal".equals(testName.getMethodName())) {
            resetDatabase();
        }
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
                .perform(ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH)
                .as(PageInfo.class)
                .hasFieldOrPropertyWithValue("totalElements", 1L)
                .hasFieldOrPropertyWithValue("sort", "name: ASC")
                .hasFieldOrPropertyWithValue("totalPages", 1)
                .hasFieldOrPropertyWithValue("size", 10)
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH + ".content")
                .asListOf(ItemEntity.class)
                .has(new Condition<>(c -> c.getName().equals("Blue Sleeping Bag"), "name Blue Sleeping Bag"), Index.atIndex(0))
                .size()
                .isEqualTo(1);
    }

    @Test
    public void test_getItems_illegalSort() throws IOException {
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("sort", "sort");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ITEM_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("No property sort found for type ItemEntity!");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getItems_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH).isNull();
    }

    private ObjectNode getVariablesForUpdateItemInStockByItemId() {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1);
        variables.put("newInStock", 100000);
        return variables;
    }

    @Test
    public void test_updateItemInStockByItemId_normal() throws IOException {
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, getVariablesForUpdateItemInStockByItemId());
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH)
                .as(ItemEntity.class)
                .hasNoNullFieldsOrPropertiesExcept()
                .matches((item) -> item.getId() == 1 && item.getInStock() == 100000);
    }

    @Test
    public void test_updateItemInStockByItemId_invalidNewInStockValue() throws IOException {
        ObjectNode variables = getVariablesForUpdateItemInStockByItemId();
        variables.put("newInStock", (Integer) null);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getExtensions().get("classification")).isEqualTo("ValidationError");
                    assertThat(error.getExtensions().get("statusCode")).isNull();
                })
                .and()
                .assertThatField(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH).isNotPresent();
    }

    @Test
    public void test_updateItemInStockByItemId_invalidItemIdValue() throws IOException {
        ObjectNode variables = getVariablesForUpdateItemInStockByItemId();
        variables.put("itemId", (Integer) null);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getExtensions().get("classification")).isEqualTo("ValidationError");
                    assertThat(error.getExtensions().get("statusCode")).isNull();
                })
                .and()
                .assertThatField(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH).isNotPresent();
    }

    @Test
    public void test_updateItemInStockByItemId_incorrectAuthentication() throws IOException {
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, getVariablesForUpdateItemInStockByItemId());
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemInStockByItemId_noAuthentication() throws IOException {
        GraphQLResponse response = graphQLTestTemplate
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, getVariablesForUpdateItemInStockByItemId());
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteItemByName_normal() throws IOException {
        final String itemName = "3 Person Tent";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", itemName);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        response.assertThatNoErrorsArePresent()
                .assertThatField(DELETE_ITEM_BY_NAME_DATA_JSON_PATH)
                .as(String.class)
                .isEqualTo(itemName);
    }

    @Test
    public void test_deleteItemByName_400_emptyName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", " ");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, ITEM_NAME_CANNOT_BE_BLANK,
                HttpStatus.BAD_REQUEST.value(), DELETE_ITEM_BY_NAME_DATA_JSON_PATH);
    }

    @Test
    public void test_deleteItemByName_401_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "3 Person Tent");

        GraphQLResponse response = graphQLTestTemplate
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assert401NotAuthorizedError(response, DELETE_ITEM_BY_NAME_DATA_JSON_PATH);
    }

    @Test
    public void test_deleteItemByName_401_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "3 Person Tent");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth("incorrectUsername", "incorrectPassword")
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assert401NotAuthorizedError(response, DELETE_ITEM_BY_NAME_DATA_JSON_PATH);
    }

    @Test
    public void test_deleteItemByName_404_itemNotFoundException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "foo");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, "Item with name foo is not found.",
                HttpStatus.NOT_FOUND.value(), DELETE_ITEM_BY_NAME_DATA_JSON_PATH);
    }

    private void assertResponseOk(GraphQLResponse response) {
        assertThat(response).isNotNull();
        log.info("{} response:\n{}", testName.getMethodName(), response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
    }

    private static void assertErrorWithNullData(GraphQLResponse response, String errorMessage,
                                                int errorExtensionStatusCode, String dataJsonPath) {
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(errorMessage);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(errorExtensionStatusCode);
                })
                .and()
                .assertThatField(dataJsonPath).isNull();
    }

    private static void assert401NotAuthorizedError(GraphQLResponse response, String dataJsonPath) {
        assertErrorWithNullData(response, GraphQLTestErrorType.UNAUTHORIZED.toString(),
                HttpStatus.UNAUTHORIZED.value(), dataJsonPath);
    }

    private void resetDatabase() {
        log.info("Reset database...");
        globalPreferencesService.resetAllIndustriesDatabase();
    }
}
