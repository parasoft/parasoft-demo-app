package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.dto.ItemsDTO;
import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.ItemService;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
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

    private static final String UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/updateItemInStockByItemId.graphql";
    private static final String UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH = DATA_PATH + ".updateItemInStockByItemId";

    private static final String GET_ITEM_BY_NAME_GRAPHQL_RESOURCE="graphql/items/getItemByName.graphql";
    private static final String GET_ITEM_BY_NAME_DATA_JSON_PATH = DATA_PATH + ".getItemByName";

    private static final String UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE = "graphql/items/updateItemById.graphql";
    private static final String UPDATE_ITEM_BY_ID_DATA_JSON_PATH = DATA_PATH + ".updateItemById";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ResetEntrance resetEntrance;

    @Autowired
    private ItemService itemService;

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
    public void test_updateItemById_normal() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH)
                .as(ItemEntity.class)
                .matches((item) ->
                    item.getName().equals(itemDTO.getName()) &&
                    item.getDescription().equals(itemDTO.getDescription()) &&
                    item.getCategoryId().equals(itemDTO.getCategoryId()) &&
                    item.getInStock().equals(itemDTO.getInStock()) &&
                    item.getImage().equals(itemDTO.getImagePath()) &&
                    item.getRegion() == itemDTO.getRegion()
                );
    }

    @Test
    public void test_updateItemById_categoryNotFound() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        itemDTO.setCategoryId(10L);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Category with ID 10 is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_itemNotFound() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 10)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Item with ID 10 is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_emptyItemName() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        itemDTO.setName("");
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 10)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Item name cannot be an empty string(null, '' or '  ').");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_emptyItemDescription() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        itemDTO.setDescription("");
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 10)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Description cannot be an empty string(null, '' or '  ').");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_negativeInStock() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        itemDTO.setInStock(-10);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 10)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("In stock cannot be a negative number.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_itemNameExistsAlready() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        itemDTO.setName("Green Sleeping Bag");
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Item name already exists.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_invalidRegionType() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        itemDTO.setRegion(RegionType.UNITED_STATES);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("The region does not belong to the current industry.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_incorrectAuthentication() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_updateItemById_noAuthentication() throws IOException {
        ItemsDTO itemDTO = getItemDTOInstance();
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1)
                .putPOJO("itemDTO", itemDTO);
        GraphQLResponse response = graphQLTestTemplate
                .perform(UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(UPDATE_ITEM_BY_ID_DATA_JSON_PATH).isNull();
    }

    private ItemsDTO getItemDTOInstance() {
        ItemsDTO itemDTO = new ItemsDTO();
        itemDTO.setName("foo-name");
        itemDTO.setDescription("foo-desc");
        itemDTO.setCategoryId(1L);
        itemDTO.setInStock(10);
        itemDTO.setImagePath("/foo");
        itemDTO.setRegion(LOCATION_1);
        return itemDTO;
    }
}
