package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.dto.ItemsDTO;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@GraphQLTest
public class ItemGraphQLDataFetcherTest {

    private static final String GET_ITEMS_GRAPHQL_RESOURCE = "graphql/items/getItems.graphql";
    private static final String GET_ITEMS_DATA_JSON_PATH = DATA_PATH + ".getItems";
    private static final String GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/getItemByItemId.graphql";
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
    private static final String UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE = "graphql/items/updateItemByItemId.graphql";
    private static final String UPDATE_ITEM_BY_ITEM_ID_DATA_JSON_PATH = DATA_PATH + ".updateItemByItemId";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
    }

    @Before
    public void conditionalBefore() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_deleteItemByName_normal", "test_updateItemInStockByItemId_normal", "test_updateItemByItemId_normal", "test_addNewItem_normal", "test_deleteItemByItemId_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @After
    public void conditionalAfter() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_deleteItemByName_normal", "test_updateItemInStockByItemId_normal", "test_updateItemByItemId_normal", "test_addNewItem_normal", "test_deleteItemByItemId_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @Test
    public void test_getItems_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1);
        variables.put("regions", RegionType.LOCATION_1.name());
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
    public void test_getItems_invalidSort() throws IOException {
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("sort", "sort");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEMS_GRAPHQL_RESOURCE, variable);

        assertError_getItems(response, HttpStatus.INTERNAL_SERVER_ERROR, "No property sort found for type ItemEntity!");
    }

    @Test
    public void test_getItems_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_ITEMS_GRAPHQL_RESOURCE, variables);

        assertError_getItems(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getItemByItemId_normal() throws Throwable {
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
    public void test_getItemByItemId_itemIdNotFound() throws Throwable {
        String itemId = "0";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);

        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);

        assertError_getItemByItemId(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
    }

    @Test
    public void test_getItemByItemId_incorrectAuthentication() throws Throwable {
        String itemId = "1";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("itemId", itemId);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variable);

        assertError_getItemByItemId(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_updateItemInStockByItemId_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1);
        variables.put("newInStock", 100000);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

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
    public void test_updateItemInStockByItemId_itemIdNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", -1);
        variables.put("newInStock", 100000);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemInStockByItemId(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, -1));
    }

    @Test
    public void test_updateItemInStockByItemId_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1);
        variables.put("newInStock", 100000);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemInStockByItemId(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_updateItemInStockByItemId_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1);
        variables.put("newInStock", 100000);

        GraphQLResponse response = graphQLTestTemplate
                .perform(UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemInStockByItemId(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
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
    public void test_getItemByName_emptyItemName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", " ");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_getItemByName(response, HttpStatus.BAD_REQUEST, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);
    }

    @Test
    public void test_getItemByName_itemNameNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "Tent");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_getItemByName(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_NAME_NOT_FOUND, "Tent"));
    }

    @Test
    public void test_getItemByName_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "Tent");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_getItemByName(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_deleteItemByName_normal() throws IOException {
        final String itemName = "3 Person Tent";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", itemName);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(DELETE_ITEM_BY_NAME_DATA_JSON_PATH)
                .as(String.class)
                .isEqualTo(itemName);
    }

    @Test
    public void test_deleteItemByName_emptyName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", " ");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByName(response, HttpStatus.BAD_REQUEST, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);
    }

    @Test
    public void test_deleteItemByName_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "3 Person Tent");

        GraphQLResponse response = graphQLTestTemplate
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByName(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_deleteItemByName_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "3 Person Tent");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth("incorrectUsername", "incorrectPassword")
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByName(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_deleteItemByName_itemNameNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemName", "foo");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByName(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_NAME_NOT_FOUND, "foo"));
    }

    @Test
    public void test_addNewItem_normal() throws IOException {
        String name = "Tent";
        String description = "name Tent";
        RegionType region = RegionType.LOCATION_1;
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
    public void test_addNewItem_emptyItemName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("", "description", 1L, 10, null, RegionType.LOCATION_1));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.BAD_REQUEST, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);
    }

    @Test
    public void test_addNewItem_itemNameExist() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("6 Person Tent", "description", 1L, 10, null, RegionType.LOCATION_1));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.BAD_REQUEST, AssetMessages.ITEM_NAME_EXISTS_ALREADY);
    }

    @Test
    public void test_addNewItem_categoryIdNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("Tent", "description", 50L, 10, null, RegionType.LOCATION_1));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, 50L));
    }

    @Test
    public void test_addNewItem_regionNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "description", 1L, 10, null, RegionType.MERCURY));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.BAD_REQUEST, AssetMessages.INCORRECT_REGION_IN_CURRENT_INDUSTRY);
    }

    @Test
    public void test_addNewItem_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "description", 1L, 10, null, RegionType.LOCATION_1));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_addNewItem_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "description", 1L, 10, null, RegionType.LOCATION_1));

        GraphQLResponse response = graphQLTestTemplate
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_addNewItem_emptyDescription() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "", 1L, 10, null, RegionType.LOCATION_1));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.BAD_REQUEST, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);
    }

    @Test
    public void test_addNewItem_invalidNumberInStock() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode().putPOJO("itemsDTO",
                new ItemsDTO("ten", "name ten", 1L, -10, null, RegionType.LOCATION_1));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_addNewItem(response, HttpStatus.BAD_REQUEST, AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER);
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
    public void test_deleteItemByItemId_itemIdNotFound() throws IOException {
        Long itemId = 0L;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByItemId(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
    }

    @Test
    public void test_deleteItemByItemId_noAuthentication() throws IOException {
        Long itemId = 0L;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);

        GraphQLResponse response = graphQLTestTemplate
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByItemId(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_deleteItemByItemId_incorrectAuthentication() throws IOException {
        Long itemId = 0L;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPassword")
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByItemId(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_deleteItemByItemId_invalidItemId() throws IOException {
        String itemId = "-1";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_deleteItemByItemId(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
    }

    @Test
    public void test_updateItemByItemId_normal() throws IOException {
        ItemsDTO itemsDTO = getItemsDTOInstance();
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", itemsDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(UPDATE_ITEM_BY_ITEM_ID_DATA_JSON_PATH)
                .as(ItemEntity.class)
                .matches((item) ->
                        item.getName().equals(itemsDTO.getName()) &&
                                item.getDescription().equals(itemsDTO.getDescription()) &&
                                item.getCategoryId().equals(itemsDTO.getCategoryId()) &&
                                item.getInStock().equals(itemsDTO.getInStock()) &&
                                item.getImage().equals(itemsDTO.getImagePath()) &&
                                item.getRegion() == itemsDTO.getRegion()
                );
    }

    @Test
    public void test_updateItemByItemId_categoryNotFound() throws IOException {
        Long categoryId = 0L;
        ItemsDTO itemsDTO = getItemsDTOInstance();
        itemsDTO.setCategoryId(categoryId);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", itemsDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, categoryId));
    }

    @Test
    public void test_updateItemByItemId_itemNotFound() throws IOException {
        Long itemId = 0L;
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", itemId)
                .putPOJO("itemsDTO", getItemsDTOInstance());

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
    }

    @Test
    public void test_updateItemByItemId_regionNotFound() throws IOException {
        RegionType regionType  = RegionType.MERCURY; // this region does not belong to the current industry
        ItemsDTO itemsDTO = getItemsDTOInstance();
        itemsDTO.setRegion(regionType);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", itemsDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.BAD_REQUEST, AssetMessages.INCORRECT_REGION_IN_CURRENT_INDUSTRY);
    }

    @Test
    public void test_updateItemByItemId_emptyItemName() throws IOException {
        String itemName = "";
        ItemsDTO itemsDTO = getItemsDTOInstance();
        itemsDTO.setName(itemName);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", itemsDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.BAD_REQUEST, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);
    }

    @Test
    public void test_updateItemByItemId_emptyItemDescription() throws IOException {
        String itemDescription = "";
        ItemsDTO itemsDTO = getItemsDTOInstance();
        itemsDTO.setDescription(itemDescription);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", itemsDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.BAD_REQUEST, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);
    }

    @Test
    public void test_updateItemByItemId_invalidNumberInStock() throws IOException {
        Integer inStock  = -1;
        ItemsDTO itemsDTO = getItemsDTOInstance();
        itemsDTO.setInStock(inStock);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", itemsDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.BAD_REQUEST, AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER);
    }

    @Test
    public void test_updateItemByItemId_itemNameExist() throws IOException {
        String itemName  = "Green Sleeping Bag";
        ItemsDTO itemsDTO = getItemsDTOInstance();
        itemsDTO.setName(itemName);
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", itemsDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.BAD_REQUEST, AssetMessages.ITEM_NAME_EXISTS_ALREADY);
    }

    @Test
    public void test_updateItemByItemId_incorrectAuthentication() throws IOException {
        String invalidPassword = "invalidPass";
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", getItemsDTOInstance());

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, invalidPassword)
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_updateItemByItemId_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode()
                .put("itemId", 1L)
                .putPOJO("itemsDTO", getItemsDTOInstance());

        GraphQLResponse response = graphQLTestTemplate
                .perform(UPDATE_ITEM_BY_ITEM_ID_GRAPHQL_RESOURCE, variables);

        assertError_updateItemByItemId(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    private void assertError_getItems(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_ITEMS_DATA_JSON_PATH);
    }

    private void assertError_getItemByItemId(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_ITEM_BY_ITEM_ID_DATA_JSON_PATH);
    }

    private void assertError_updateItemInStockByItemId(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, UPDATE_ITEM_IN_STOCK_BY_ITEM_ID_DATA_JSON_PATH);
    }

    private void assertError_addNewItem(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, ADD_NEW_ITEM_DATA_JSON_PATH);
    }

    private void assertError_getItemByName(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_ITEM_BY_NAME_DATA_JSON_PATH);
    }

    private void assertError_deleteItemByName(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, DELETE_ITEM_BY_NAME_DATA_JSON_PATH);
    }

    private void assertError_deleteItemByItemId(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, DELETE_ITEM_BY_ITEM_ID_DATA_JSON_PATH);
    }

    private void assertError_updateItemByItemId(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, UPDATE_ITEM_BY_ITEM_ID_DATA_JSON_PATH);
    }

    private ItemsDTO getItemsDTOInstance() {
        ItemsDTO itemsDTO = new ItemsDTO();
        itemsDTO.setName("foo-name");
        itemsDTO.setDescription("foo-desc");
        itemsDTO.setCategoryId(1L);
        itemsDTO.setInStock(10);
        itemsDTO.setImagePath("/foo");
        itemsDTO.setRegion(RegionType.LOCATION_1);
        return itemsDTO;
    }
}
