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

import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.dto.OrderDTO;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.*;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.ShoppingCartService;
import com.parasoft.demoapp.service.UserService;
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
import java.io.IOException;
import java.util.Objects;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.*;
import static com.parasoft.demoapp.model.industry.RegionType.LOCATION_1;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class OrderGraphQLDataFetcherTest {

    private static final String Order_GRAPHQL_RESOURCE = "graphql/orders/getOrder.graphql";
    private static final String GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH = DATA_PATH + ".getOrderByOrderNumber";
    private static final String ORDER_GRAPHQL_RESOURCE = "graphql/orders/createOrder.graphql";
    private static final String CREATE_ORDER_DATA_JSON_PATH = DATA_PATH + ".createOrder";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ResetEntrance resetEntrance;

    private UserEntity purchaser;
    private UserEntity apporover;

    @Before
    public void setup() {
        graphQLTestTemplate.getHeaders().clear();
        // Reset database
        resetEntrance.run();
        purchaser = userService.getUserByUsername(USERNAME_PURCHASER);
        apporover = userService.getUserByUsername(USERNAME_APPROVER);
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

    private OrderDTO getOrderDTOInstance() {
        String location = "location info";
        String receiverId = "receiverId info";
        String eventId = "eventId info";
        String eventNumber = "eventNumber info";
        return new OrderDTO(LOCATION_1, location, receiverId, eventId, eventNumber);
    }

    @Test
    public void test_createOrder_normal() throws Exception {
        ItemEntity item = itemService.getItemById(1L);
        CartItemEntity cartItem = shoppingCartService.addCartItemInShoppingCart(purchaser.getId(), item.getId(), item.getInStock());
        OrderDTO orderDTO = getOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(CREATE_ORDER_DATA_JSON_PATH)
                .as(OrderEntity.class)
                .hasNoNullFieldsOrPropertiesExcept("respondedBy", "approverReplyDate", "comments")
                .matches((order) ->
                        order.getStatus() == OrderStatus.SUBMITTED &&
                                order.getRequestedBy().equals(purchaser.getUsername()) &&
                                order.getRegion() == orderDTO.getRegion() &&
                                Objects.equals(order.getLocation(), orderDTO.getLocation()) &&
                                Objects.equals(order.getReceiverId(), orderDTO.getReceiverId()) &&
                                Objects.equals(order.getEventId(), orderDTO.getEventId()) &&
                                Objects.equals(order.getEventNumber(), orderDTO.getEventNumber()) &&
                                order.getOrderItems().size() == 1 &&
                                Objects.equals(order.getOrderItems().get(0).getItemId(), cartItem.getItemId()) &&
                                Objects.equals(order.getOrderItems().get(0).getQuantity(), cartItem.getQuantity()));
    }

    @Test
    public void test_createOrder_regionNotExistOnCurrentIndustry() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        orderDTO.setRegion(RegionType.UNITED_STATES);
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.BAD_REQUEST, "Location not found for region UNITED_STATES.");
    }

    @Test
    public void test_createOrder_invalidOrNullRegionValue() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        orderDTO.setRegion(null);
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.BAD_REQUEST, "Region should not be null.");
    }

    @Test
    public void test_createOrder_nullLocationValue() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        orderDTO.setLocation(null);
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.BAD_REQUEST, "Location should not be blank(null, '' or '  ').");
    }

    @Test
    public void test_createOrder_nullReceiverIdValue() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        orderDTO.setReceiverId(null);
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.BAD_REQUEST, "Receiver ID should not be blank(null, '' or '  ').");
    }

    @Test
    public void test_createOrder_nullEventIdValue() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        orderDTO.setEventId(null);
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.BAD_REQUEST, "Event ID should not be blank(null, '' or '  ').");
    }

    @Test
    public void test_createOrder_nullEventNumberValue() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        orderDTO.setEventNumber(null);
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.BAD_REQUEST, "Event number should not be blank(null, '' or '  ').");
    }

    @Test
    public void test_createOrder_notAuthenticated() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.UNAUTHORIZED, "Current user is not authorized.");
    }

    @Test
    public void test_createOrder_withIncorrectUsernameOrPassword() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), "incorrectPassword")
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.UNAUTHORIZED, "Current user is not authorized.");
    }

    @Test
    public void test_createOrder_withIncorrectRole() throws IOException {
        OrderDTO orderDTO = getOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(apporover.getUsername(), apporover.getPassword())
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertErrorForCreateOrder(response, HttpStatus.FORBIDDEN, "Current user does not have permission.");
    }

    private void assertErrorForCreateOrder(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(expectedErrorMessage);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(expectedHttpStatus.value());
                })
                .and()
                .assertThatField(CREATE_ORDER_DATA_JSON_PATH).isNull();
    }
}