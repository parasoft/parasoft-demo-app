package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.dto.ShoppingCartDTO;
import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class CartItemGraphQLDataFetcherTest {
    private static final String GET_CART_ITEMS_GRAPHQL_RESOURCE = "graphql/cartItems/getCartItems.graphql";

    private static final String ADD_ITEM_IN_CART_GRAPHQL_RESOURCE = "graphql/cartItems/addItemInCart.graphql";

    private static final String GET_CART_ITEMS_DATA_JSON_PATH = DATA_PATH + ".getCartItems";

    private static final String ADD_ITEM_IN_CART_DATA_JSON_PATH = DATA_PATH + ".addItemInCart";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    private ResetEntrance resetEntrance;

    @Before
    public void setup() {
        graphQLTestTemplate.getHeaders().clear();
        resetEntrance.run();
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
        log.info(response.getRawResponse().getBody());
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
    public void test_getCartItems_401_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(GET_CART_ITEMS_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(GET_CART_ITEMS_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getCartItems_403_noPermission() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_APPROVER, PASSWORD)
                .perform(GET_CART_ITEMS_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.FORBIDDEN.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.FORBIDDEN.value());
                })
                .and()
                .assertThatField(GET_CART_ITEMS_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addItemInCart_normal() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 2;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(createShoppingCartDTO(itemId, itemQty)));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH)
                .as(CartItemEntity.class)
                .hasFieldOrPropertyWithValue("quantity", itemQty);
    }

    @Test
    public void test_addItemInCart_400_inventoryNotEnough() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 100;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(createShoppingCartDTO(itemId, itemQty)));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.BAD_REQUEST.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addItemInCart_400_quantityIsZero() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 0;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(createShoppingCartDTO(itemId, itemQty)));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.QUANTITY_CANNOT_BE_ZERO.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addItemInCart_400_quantityIsNegativeNumber() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = -1;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(createShoppingCartDTO(itemId, itemQty)));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addItemInCart_401_notAuthenticated() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 2;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(createShoppingCartDTO(itemId, itemQty)));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addItemInCart_403_noPermission() throws IOException {
        final Long itemId = 1L;
        final Integer itemQty = 2;
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(createShoppingCartDTO(itemId, itemQty)));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_APPROVER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.FORBIDDEN.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.FORBIDDEN.value());
                })
                .and()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_addItemInCart_404_itemDoesNotExist() throws IOException {
        final Long itemId = 100L;
        final Integer itemQty = 2;
        String errorMessage = MessageFormat.format(GraphQLTestErrorType.NOT_FOUND.toString(), itemId);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("shoppingCartDTO", objectMapper.valueToTree(createShoppingCartDTO(itemId, itemQty)));
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_ITEM_IN_CART_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(errorMessage);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(ADD_ITEM_IN_CART_DATA_JSON_PATH).isNull();
    }

    private static ShoppingCartDTO createShoppingCartDTO(Long itemId, Integer itemQty) {
        return new ShoppingCartDTO(itemId, itemQty);
    }
}
