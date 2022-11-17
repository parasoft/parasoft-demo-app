package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.dto.ItemsDTO;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.ItemService;
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
import java.text.MessageFormat;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static com.parasoft.demoapp.messages.AssetMessages.ITEM_NAME_CANNOT_BE_BLANK;
import static com.parasoft.demoapp.model.industry.RegionType.LOCATION_1;
import static com.parasoft.demoapp.model.industry.RegionType.MERCURY;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class ItemGraphQLDataFetcherTest {

    private static final String GET_ITEMS_GRAPHQL_RESOURCE = "graphql/items/getItems.graphql";
    private static final String GET_ITEMS_DATA_JSON_PATH = DATA_PATH + ".getItems";
    private static final String GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/getItem.graphql";
    private static final String GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH = DATA_PATH + ".getItemByItemId";

    private static final String UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/updateItemInStockByItemId.graphql";
    private static final String UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH = DATA_PATH + ".updateItemInStockByItemId";

    private static final String ADD_NEW_ITEM_GRAPHQL_RESOURCE = "graphql/items/addNewItem.graphql";
    private static final String ADD_NEW_ITEM_DATA_JSON_PATH = DATA_PATH + ".addNewItem";

    private static final String GET_ITEM_BY_NAME_GRAPHQL_RESOURCE="graphql/items/getItemByName.graphql";
    private static final String GET_ITEM_BY_NAME_DATA_JSON_PATH = DATA_PATH + ".getItemByName";

    private static final String DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE = "graphql/items/deleteItemByName.graphql";
    private static final String DELETE_ITEM_BY_NAME_DATA_JSON_PATH = DATA_PATH + ".deleteItemByName";

    private static final String DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/deleteItemByItemId.graphql";
    private static final String DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH = DATA_PATH + ".deleteItemByItemId";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResetEntrance resetEntrance;

    @Autowired
    private ItemService itemService;

    @Autowired private GlobalPreferencesService globalPreferencesService;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
        resetEntrance.run();
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
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", (String)null);
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> assertThat(error.getExtensions().get("classification")).isEqualTo("ValidationError"));
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
    public void test_getItemByName_normal() throws Throwable {
        String itemName = "3 Person Tent";
        ItemEntity item = itemService.getItemByName(itemName);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", itemName);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(GET_ITEM_BY_NAME_DATA_JSON_PATH)
                .as(ItemEntity.class)
                .matches((itemEntity) ->
                    itemEntity.getName().equals(itemName) && itemEntity.getDescription().equals(item.getDescription())
                        && itemEntity.getId().equals(item.getId()) && itemEntity.getRegion().equals(item.getRegion())
                        && itemEntity.getInStock().equals(item.getInStock()) && itemEntity.getCategoryId().equals(item.getCategoryId())
                        && itemEntity.getImage().equals(item.getImage()) && itemEntity.getLastAccessedDate().equals(item.getLastAccessedDate()));
    }

    @Test
    public void test_getItemByName_invalidOrNullItemNameValue() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Map has no value for 'itemName'");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(GET_ITEM_BY_NAME_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getItemByName_itemNotFoundException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "Tent");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Item with name Tent is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(GET_ITEM_BY_NAME_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getItemByName_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "Tent");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(GET_ITEM_BY_NAME_DATA_JSON_PATH).isNull();
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

    @Test
    public void test_addNewItem_normal() throws IOException {
        String name = "Tent";
        String description = "name Tent";
        RegionType region = LOCATION_1;
        String imagePath = "/outdoor/image.png";
        ItemsDTO itemsDTO = new ItemsDTO(name, description, 1L, 10, imagePath, region);
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO", itemsDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH)
                .as(ItemEntity.class)
                .matches(itemEntity ->
                        itemEntity.getRegion().equals(region) && itemEntity.getDescription().equals(description)
                        && itemEntity.getName().equals(name) && itemEntity.getCategoryId().equals(1L)
                        && itemEntity.getInStock().equals(10) && itemEntity.getImage().equals(imagePath)
                );
    }

    @Test
    public void test_addNewItem_itemNameExistsAlreadyException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("6 Person Tent", "description", 1L, 10, null, LOCATION_1));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(AssetMessages.ITEM_NAME_EXISTS_ALREADY);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addNewItem_categoryNotFoundException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("Tent", "description", 50L, 10, null, LOCATION_1));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, 50L));
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addNewItem_invalidOrNullItemNameValue() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("", "description", 1L, 10, null, LOCATION_1));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addNewItem_regionNotExistOnCurrentIndustry() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "description", 1L, 10, null, MERCURY));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(AssetMessages.INCORRECT_REGION_IN_CURRENT_INDUSTRY);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addNewItem_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "description", 1L, 10, null, LOCATION_1));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addNewItem_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "description", 1L, 10, null, LOCATION_1));
        GraphQLResponse response = graphQLTestTemplate
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addNewItem_invalidOrNullDescriptionValue() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "", 1L, 10, null, LOCATION_1));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addNewItem_invalidOrNegativeNumberInStockValue() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "name ten", 1L, -10, null, LOCATION_1));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_NEW_ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteItemByItemId_normal() throws IOException {
        Long itemId = 1L;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH)
                .as(Long.class)
                .isEqualTo(itemId);
    }

    @Test
    public void test_deleteItemByItemId_notFound() throws IOException {
        Long itemId = 0L;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Item with ID 0 is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteItemByItemId_notAuthorized() throws IOException {
        Long itemId = 0L;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Current user is not authorized.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteItemByItemId_incorrectAuthorized() throws IOException {
        Long itemId = 0L;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPassword")
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Current user is not authorized.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteItemByItemId_nullItemId() throws IOException {
        Long itemId = null;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> assertThat(error.getExtensions().get("classification")).isEqualTo("ValidationError"));
    }

    @Test
    public void test_deleteItemByItemId_emptyItemId() throws IOException {
        String itemId = "  ";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Map has no value for 'itemId'");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteItemByItemId_invalidItemId() throws IOException {
        String itemId = "invalidId";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();

        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: For input string: \"invalidId\"");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH).isNull();
    }
}
