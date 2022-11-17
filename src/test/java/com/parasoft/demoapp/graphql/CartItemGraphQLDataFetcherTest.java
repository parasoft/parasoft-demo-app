package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
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

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private ResetEntrance resetEntrance;
    @Rule
    public TestName testName = new TestName();

    @Before
    public void setup() {
        graphQLTestTemplate.getHeaders().clear();
        resetEntrance.run();
    }

    @Before
    public void conditionalBefore() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getCartItems_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @After
    public void conditionalAfter() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getCartItems_normal"));
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

    private void assertError_getCartItems(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_CART_ITEMS_DATA_JSON_PATH);
    }
}
