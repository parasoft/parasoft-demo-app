package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;

import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.OrderService;
import com.parasoft.demoapp.service.ShoppingCartService;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;


import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class OrderGraphQLDataFetcherTest {

    private static final String Order_GRAPHQL_RESOURCE = "graphql/orders/getOrder.graphql";
    private static final String GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH = DATA_PATH + ".getOrderByOrderNumber";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    GlobalPreferencesService globalPreferencesService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    OrderService orderService;

    @Autowired
    ResetEntrance resetEntrance;

    @Before
    public void setup() {
        graphQLTestTemplate.getHeaders().clear();
        resetEntrance.run();
    }

    private String createOrderForTest() throws Throwable {
        Long userId = 1L;
        String username = "purchaser";
        RegionType region = RegionType.getRegionsByIndustryType(globalPreferencesService.getCurrentIndustry()).get(0);
        String location = "testLocation";
        String receiverId = "2";
        String eventId = "3";
        String eventNumber= "4";
        shoppingCartService.addCartItemInShoppingCart(userId, 1L, 1);
        OrderEntity orderEntity = orderService.addNewOrder(userId, username, region, location, receiverId, eventId, eventNumber);

        return orderEntity.getOrderNumber();
    }

    @Test
    public void getOrderByOrderNumber_normal() throws Throwable {
        String orderNumber = createOrderForTest();
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);
        GraphQLResponse graphQLResponse = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(Order_GRAPHQL_RESOURCE, variable);
        assertThat(graphQLResponse).isNotNull();
        log.info(graphQLResponse.getRawResponse().getBody());
        assertThat(graphQLResponse.isOk()).isTrue();
        graphQLResponse.assertThatNoErrorsArePresent()
                        .assertThatDataField().isNotNull()
                        .and()
                        .assertThatField(GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH)
                        .as(OrderEntity.class)
                        .hasFieldOrPropertyWithValue("orderNumber", orderNumber);
    }

    @Test
    public void getOrderByOrderNumber_InvalidParameter() throws Throwable {
        String orderNumber = "  ";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);
        GraphQLResponse graphQLResponse = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(Order_GRAPHQL_RESOURCE, variable);
        assertThat(graphQLResponse).isNotNull();
        log.info(graphQLResponse.getRawResponse().getBody());
        assertThat(graphQLResponse.isOk()).isTrue();
        graphQLResponse.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Order number should not be blank(null, '' or '  ').");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH).isNull();
    }

    @Test
    public void getOrderByOrderNumber_notAuthenticated() throws Throwable {
        String orderNumber = createOrderForTest();
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);
        GraphQLResponse graphQLResponse = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPassword")
                .perform(Order_GRAPHQL_RESOURCE, variable);
        assertThat(graphQLResponse).isNotNull();
        log.info(graphQLResponse.getRawResponse().getBody());
        assertThat(graphQLResponse.isOk()).isTrue();
        graphQLResponse.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH).isNull();
    }

    @Test
    public void getOrderByOrderNumber_notFound() throws Throwable {
        String orderNumber = "00-000-000";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);
        GraphQLResponse graphQLResponse = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(Order_GRAPHQL_RESOURCE, variable);
        assertThat(graphQLResponse).isNotNull();
        log.info(graphQLResponse.getRawResponse().getBody());
        assertThat(graphQLResponse.isOk()).isTrue();
        graphQLResponse.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("There is no order corresponding to 00-000-000.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH).isNull();
    }
}