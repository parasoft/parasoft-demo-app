package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.dto.ShoppingCartDTO;
import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ShoppingCartService;
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
import java.util.List;
import java.util.Set;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@GraphQLTest
public class CartItemGraphQLDataFetcherTest {
    private static final String GET_CART_ITEMS_GRAPHQL_RESOURCE = "graphql/cartItems/getCartItems.graphql";
    private static final String GET_CART_ITEMS_DATA_JSON_PATH = DATA_PATH + ".getCartItems";
    private static final String ADD_ITEM_IN_CART_GRAPHQL_RESOURCE = "graphql/cartItems/addItemInCart.graphql";
    private static final String ADD_ITEM_IN_CART_DATA_JSON_PATH = DATA_PATH + ".addItemInCart";
    private static final String REMOVE_CART_ITEM_GRAPHQL_RESOURCE = "graphql/cartItems/removeCartItem.graphql";
    private static final String REMOVE_CART_ITEM_DATA_JSON_PATH = DATA_PATH + ".removeCartItem";
    private static final String REMOVE_ALL_CART_ITEMS_GRAPHQL_RESOURCE = "graphql/cartItems/removeAllCartItems.graphql";
    private static final String REMOVE_ALL_CART_ITEMS_DATA_JSON_PATH = DATA_PATH + ".removeAllCartItems";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setup() {
        graphQLTestTemplate.getHeaders().clear();
    }

    @Before
    public void conditionalBefore() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getCartItems_normal", "test_addItemInCart_normal",
                "test_removeCartItem_normal", "test_removeAllCartItems_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @After
    public void conditionalAfter() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getCartItems_normal", "test_addItemInCart_normal",
                "test_removeCartItem_normal", "test_removeAllCartItems_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @Test
    public void test_getCartItems_normal() throws IOException, ParameterException, ItemNotFoundException, InventoryNotFoundException {
        ObjectNode variables = objectMapper.createObjectNode();
        shoppingCartService.addCartItemInShoppingCart(1L, 1L, 1);
        List<CartItemEntity> cartItemEntities = shoppingCartService.getCartItemsByUserId(1L);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(GET_CART_ITEMS_DATA_JSON_PATH)
                .asListOf(CartItemEntity.class)
                .element(0)
                .hasFieldOrPropertyWithValue("id", cartItemEntities.get(0).getId())
                .hasFieldOrPropertyWithValue("userId", cartItemEntities.get(0).getUserId())
                .hasFieldOrPropertyWithValue("itemId", cartItemEntities.get(0).getItemId())
                .hasFieldOrPropertyWithValue("name", cartItemEntities.get(0).getName())
                .hasFieldOrPropertyWithValue("description", cartItemEntities.get(0).getDescription())
                .hasFieldOrPropertyWithValue("image", cartItemEntities.get(0).getImage())
                .hasFieldOrPropertyWithValue("realInStock", cartItemEntities.get(0).getRealInStock())
                .hasFieldOrPropertyWithValue("quantity", cartItemEntities.get(0).getQuantity());
    }

    @Test
    public void test_getCartItems_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertError_getCartItems(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getCartItems_noPermission() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_APPROVER, PASSWORD)
                .perform(GET_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertError_getCartItems(response, HttpStatus.FORBIDDEN, ConfigMessages.USER_HAS_NO_PERMISSION);
    }

    @Test
    public void test_addItemInCart_normal() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 2;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(new ShoppingCartDTO(itemId, itemQty)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH)
                .as(CartItemEntity.class)
                .hasFieldOrPropertyWithValue("quantity", itemQty);
    }

    @Test
    public void test_addItemInCart_invalidItemQty() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 100;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(new ShoppingCartDTO(itemId, itemQty)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);

        assertError_addItemInCart(response, HttpStatus.BAD_REQUEST, AssetMessages.INCLUDES_SHOPPING_CART_IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT);
    }

    @Test
    public void test_addItemInCart_invalidItemQty_IsZero() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 0;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(new ShoppingCartDTO(itemId, itemQty)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);

        assertError_addItemInCart(response, HttpStatus.BAD_REQUEST, AssetMessages.QUANTITY_CANNOT_BE_ZERO);
    }

    @Test
    public void test_addItemInCart_invalidItemQty_IsNegative() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = -1;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(new ShoppingCartDTO(itemId, itemQty)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);

        assertError_addItemInCart(response, HttpStatus.BAD_REQUEST, AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO);
    }

    @Test
    public void test_addItemInCart_noAuthentication() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 2;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(new ShoppingCartDTO(itemId, itemQty)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);

        assertError_addItemInCart(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_addItemInCart_noPermission() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 2;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(new ShoppingCartDTO(itemId, itemQty)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_APPROVER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);

        assertError_addItemInCart(response, HttpStatus.FORBIDDEN, ConfigMessages.USER_HAS_NO_PERMISSION);
    }

    @Test
    public void test_addItemInCart_itemIdNotFound() throws IOException {
        final Long itemId = 100L;
        final Integer itemQty = 2;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(new ShoppingCartDTO(itemId, itemQty)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);

        assertError_addItemInCart(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
    }

@Test
    public void test_removeCartItem_normal() throws InventoryNotFoundException, ParameterException, ItemNotFoundException, IOException {
        Long itemId = 1L;
        shoppingCartService.addCartItemInShoppingCart(1L, itemId, 1);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", itemId);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(REMOVE_CART_ITEM_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(REMOVE_CART_ITEM_DATA_JSON_PATH)
                .asLong()
                .isEqualTo(itemId);
    }

    @Test
    public void test_removeCartItem_notFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1L);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(REMOVE_CART_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_removeCartItem(response, HttpStatus.NOT_FOUND, AssetMessages.THIS_ITEM_IS_NOT_IN_THE_SHOPPING_CART);
    }

    @Test
    public void test_removeCartItem_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1L);

        GraphQLResponse response = graphQLTestTemplate
                .perform(REMOVE_CART_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_removeCartItem(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_removeCartItem_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1L);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "incorrectPassword")
                .perform(REMOVE_CART_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_removeCartItem(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_removeCartItem_noPermission() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("itemId", 1L);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_APPROVER, PASSWORD)
                .perform(REMOVE_CART_ITEM_GRAPHQL_RESOURCE, variables);

        assertError_removeCartItem(response, HttpStatus.FORBIDDEN, ConfigMessages.USER_HAS_NO_PERMISSION);
    }
    
    @Test
    public void test_removeAllCartItems_normal() throws Throwable {
        ObjectNode variables = objectMapper.createObjectNode();
        shoppingCartService.addCartItemInShoppingCart(1L, 1L, 1);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(REMOVE_ALL_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(REMOVE_ALL_CART_ITEMS_DATA_JSON_PATH)
                .asBoolean()
                .isTrue();
    }

    @Test
    public void test_removeAllCartItems_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(REMOVE_ALL_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertError_removeAllCartItems(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_removeAllCartItems_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .perform(REMOVE_ALL_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertError_removeAllCartItems(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_removeAllCartItems_noPermission() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_APPROVER, PASSWORD)
                .perform(REMOVE_ALL_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertError_removeAllCartItems(response, HttpStatus.FORBIDDEN, ConfigMessages.USER_HAS_NO_PERMISSION);
    }

    @Test
    public void test_removeAllCartItems_notFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(REMOVE_ALL_CART_ITEMS_GRAPHQL_RESOURCE, variables);

        assertError_removeAllCartItems(response, HttpStatus.NOT_FOUND, AssetMessages.NO_CART_ITEMS);
    }

    private void assertError_getCartItems(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_CART_ITEMS_DATA_JSON_PATH);
    }

    private void assertError_addItemInCart(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, ADD_ITEM_IN_CART_DATA_JSON_PATH);
    }

    private void assertError_removeCartItem(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, REMOVE_CART_ITEM_DATA_JSON_PATH);
    }
    private void assertError_removeAllCartItems(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, REMOVE_ALL_CART_ITEMS_DATA_JSON_PATH);
    }
}
