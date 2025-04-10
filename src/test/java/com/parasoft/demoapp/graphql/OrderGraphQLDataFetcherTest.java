package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.dto.OrderDTO;
import com.parasoft.demoapp.dto.OrderStatusDTO;
import com.parasoft.demoapp.dto.UnreviewedOrderNumberResponseDTO;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.*;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import com.parasoft.demoapp.service.*;
import com.parasoft.demoapp.utilfortest.OrderUtilForTest;
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
import java.util.Objects;
import java.util.Set;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@GraphQLTest
public class OrderGraphQLDataFetcherTest {

    private static final String GET_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE = "graphql/orders/getOrder.graphql";
    private static final String GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH = DATA_PATH + ".getOrderByOrderNumber";
    private static final String CREATE_ORDER_GRAPHQL_RESOURCE = "graphql/orders/createOrder.graphql";
    private static final String CREATE_ORDER_DATA_JSON_PATH = DATA_PATH + ".createOrder";
    private static final String GET_ORDERS_GRAPHQL_RESOURCE = "graphql/orders/getOrders.graphql";
    private static final String GET_ORDERS_DATA_JSON_PATH = DATA_PATH + ".getOrders";
    private static final String UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE = "graphql/orders/updateOrderByOrderNumber.graphql";
    private static final String UPDATE_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH = DATA_PATH + ".updateOrderByOrderNumber";
    private static final String GET_UNREVIEWED_NUMBER_GRAPHQL_RESOURCE = "graphql/orders/getUnreviewedNumber.graphql";
    private static final String GET_UNREVIEWED_NUMBER_DATA_JSON_PATH = DATA_PATH + ".getUnreviewedNumber";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Rule
    public TestName testName = new TestName();
    private UserEntity purchaser;
    private UserEntity approver;

    @Before
    public void setup() {
        graphQLTestTemplate.getHeaders().clear();
        purchaser = userService.getUserByUsername(USERNAME_PURCHASER);
        approver = userService.getUserByUsername(USERNAME_APPROVER);
    }

    @Before
    public void conditionalBefore() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getOrderByOrderNumber_normal", "test_createOrder_normal", "test_updateOrderByOrderNumber_normal_purchaser", "test_updateOrderByOrderNumber_normal_approver", "test_updateOrderByOrderNumber_noPermission", "test_updateOrderByOrderNumber_changeToUnreviewed", "test_updateOrderByOrderNumber_updateSubmittedOrder", "test_updateOrderByOrderNumber_updateCanceledOrder", "test_updateOrderByOrderNumber_revertOrderStatus", "test_getUnreviewedNumber_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @After
    public void conditionalAfter() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getOrderByOrderNumber_normal", "test_createOrder_normal", "test_updateOrderByOrderNumber_normal_purchaser", "test_updateOrderByOrderNumber_normal_approver", "test_updateOrderByOrderNumber_noPermission", "test_updateOrderByOrderNumber_changeToUnreviewed", "test_updateOrderByOrderNumber_updateSubmittedOrder", "test_updateOrderByOrderNumber_updateCanceledOrder", "test_updateOrderByOrderNumber_revertOrderStatus", "test_getUnreviewedNumber_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @Test
    public void test_getOrderByOrderNumber_normal() throws Throwable {
        String orderNumber = createOrderForTest();
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variable);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                        .assertThatDataField().isNotNull()
                        .and()
                        .assertThatField(GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH)
                        .as(OrderEntity.class)
                        .hasFieldOrPropertyWithValue("orderNumber", orderNumber);
    }

    @Test
    public void test_getOrderByOrderNumber_emptyOrderNumber() throws Throwable {
        String orderNumber = "  ";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(GET_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variable);

        assertError_getOrderByOrderNumber(response, HttpStatus.BAD_REQUEST, OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK);
    }

    @Test
    public void test_getOrderByOrderNumber_noAuthentication() throws Throwable {
        String orderNumber = createOrderForTest();
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), "invalidPassword")
                .perform(GET_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variable);

        assertError_getOrderByOrderNumber(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getOrderByOrderNumber_orderNumberNotFound() throws Throwable {
        String orderNumber = "00-000-000";
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("orderNumber", orderNumber);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variable);

        assertError_getOrderByOrderNumber(response, HttpStatus.NOT_FOUND, MessageFormat.format(OrderMessages.THERE_IS_NO_ORDER_CORRESPONDING_TO, orderNumber));
    }

    @Test
    public void test_createOrder_normal() throws Exception {
        ItemEntity item = itemService.getItemById(1L);
        CartItemEntity cartItem = shoppingCartService.addCartItemInShoppingCart(purchaser.getId(), item.getId(), item.getInStock());
        OrderDTO orderDTO = createOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertThat(response).isNotNull();
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
                                Objects.equals(order.getReceiverId(), orderDTO.getShipping().getReceiverId()) &&
                                Objects.equals(order.getShippingType(), orderDTO.getShipping().getShippingType()) &&
                                Objects.equals(order.getEventId(), orderDTO.getEventId()) &&
                                Objects.equals(order.getEventNumber(), orderDTO.getEventNumber()) &&
                                order.getOrderItems().size() == 1 &&
                                Objects.equals(order.getOrderItems().get(0).getItemId(), cartItem.getItemId()) &&
                                Objects.equals(order.getOrderItems().get(0).getQuantity(), cartItem.getQuantity()));
    }

    @Test
    public void test_createOrder_regionNotFound() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        orderDTO.setRegion(RegionType.UNITED_STATES);
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.BAD_REQUEST, MessageFormat.format(OrderMessages.LOCATION_NOT_FOUND_FOR_REGION, RegionType.UNITED_STATES.toString()));
    }

    @Test
    public void test_createOrder_emptyLocation() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        orderDTO.setLocation("");
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.BAD_REQUEST, OrderMessages.LOCATION_CANNOT_BE_BLANK);
    }

    @Test
    public void test_createOrder_emptyReceiverId() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        orderDTO.getShipping().setReceiverId("");
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.BAD_REQUEST, OrderMessages.RECEIVER_ID_CANNOT_BE_BLANK);
    }

    @Test
    public void test_createOrder_emptyShippingType() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        orderDTO.getShipping().setShippingType("");
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.BAD_REQUEST, OrderMessages.SHIPPING_CANNOT_BE_BLANK);
    }

    @Test
    public void test_createOrder_emptyEventId() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        orderDTO.setEventId("");
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.BAD_REQUEST, OrderMessages.EVENT_ID_CANNOT_BE_BLANK);
    }

    @Test
    public void test_createOrder_emptyEventNumber() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        orderDTO.setEventNumber("");
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.BAD_REQUEST, OrderMessages.EVENT_NUMBER_CANNOT_BE_BLANK);
    }

    @Test
    public void test_createOrder_noAuthentication() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_createOrder_incorrectAuthentication() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), "incorrectPassword")
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_createOrder_noPermission() throws IOException {
        OrderDTO orderDTO = createOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), approver.getPassword())
                .perform(CREATE_ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertError_createOrder(response, HttpStatus.FORBIDDEN, ConfigMessages.USER_HAS_NO_PERMISSION);
    }

    @Test
    public void test_getOrders_normal() throws Throwable {
        String orderNumber = createOrderForTest();
        ObjectNode variable = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(GET_ORDERS_GRAPHQL_RESOURCE, variable);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(GET_ORDERS_DATA_JSON_PATH)
                .as(PageInfo.class)
                .hasFieldOrPropertyWithValue("totalElements", 1L)
                .hasFieldOrPropertyWithValue("totalPages", 1)
                .hasFieldOrPropertyWithValue("size", 2000)
                .hasFieldOrPropertyWithValue("sort", "orderNumber: DESC")
                .and()
                .assertThatField(GET_ORDERS_DATA_JSON_PATH + ".content")
                .asListOf(OrderEntity.class)
                .has(new Condition<>(c -> c.getRequestedBy().equals(purchaser.getUsername()), "name purchaser"), Index.atIndex(0))
                .and()
                .assertThatField(GET_ORDERS_DATA_JSON_PATH + ".content")
                .asListOf(OrderEntity.class)
                .has(new Condition<>(c -> c.getOrderItems().get(0).getName().equals("Blue Sleeping Bag"), "name Blue Sleeping Bag"), Index.atIndex(0))
                .size()
                .isEqualTo(1);
    }

    @Test
    public void test_getOrders_incorrectAuthentication() throws Throwable {
        String orderNumber = createOrderForTest();
        ObjectNode variable = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), "invalidPassword")
                .perform(GET_ORDERS_GRAPHQL_RESOURCE, variable);

        GraphQLTestUtil.assertErrorResponse(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED, GET_ORDERS_DATA_JSON_PATH);
    }

    @Test
    public void test_getOrders_noAuthentication() throws Throwable {
        String orderNumber = createOrderForTest();
        ObjectNode variable = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ORDERS_GRAPHQL_RESOURCE, variable);

        GraphQLTestUtil.assertErrorResponse(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED, GET_ORDERS_DATA_JSON_PATH);
    }

    @Test
    public void test_updateOrderByOrderNumber_normal_purchaser() throws Throwable {
        OrderEntity order = getOrderWithExpectedStatus(OrderStatus.APPROVED, "comment", false, true );
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(order.getStatus(), "changed", true, false);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", order.getOrderNumber());
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(UPDATE_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH)
                .as(OrderEntity.class)
                .matches((result) ->
                        result.getStatus() == order.getStatus() &&
                                result.getComments().equals(order.getComments()) &&
                                result.getReviewedByPRCH() &&
                                result.getReviewedByAPV()
                );
    }

    @Test
    public void test_updateOrderByOrderNumber_normal_approver() throws Throwable {
        OrderEntity order = getOrderWithExpectedStatus(OrderStatus.PROCESSED, null, true, true );
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.APPROVED, "new comment", true, true);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", order.getOrderNumber());
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), purchaser.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(UPDATE_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH)
                .as(OrderEntity.class)
                .matches((result) ->
                        result.getStatus() == orderStatusDTO.getStatus() &&
                                result.getComments().equals(orderStatusDTO.getComments()) &&
                                !result.getReviewedByPRCH() &&
                                result.getReviewedByAPV()
                );
    }

    @Test
    public void test_updateOrderByOrderNumber_orderNotFound() throws Throwable {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.APPROVED, "new comment", true, true);
        String wrongOrderNumber = "00-000-000";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", wrongOrderNumber);
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), approver.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.NOT_FOUND, MessageFormat.format(OrderMessages.THERE_IS_NO_ORDER_CORRESPONDING_TO, wrongOrderNumber));
    }

    @Test
    public void test_updateOrderByOrderNumber_noAuthentication() throws Throwable {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.APPROVED, "new comment", true, true);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", "orderNumber");
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_updateOrderByOrderNumber_incorrectAuthentication() throws Throwable {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.APPROVED, "new comment", true, true);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", "orderNumber");
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), "wrongPassword")
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_updateOrderByOrderNumber_noPermission() throws Throwable {
        OrderEntity order = getOrderWithExpectedStatus(OrderStatus.PROCESSED, null, true, false );
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.APPROVED, "new comment", true, true);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", order.getOrderNumber());
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.FORBIDDEN, MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, orderStatusDTO.getStatus()));
    }

    @Test
    public void test_updateOrderByOrderNumber_emptyOrderNumber() throws Throwable {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.APPROVED, "new comment", true, true);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", "   ");
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.BAD_REQUEST, OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK);
    }

    @Test
    public void test_updateOrderByOrderNumber_invalidOrderStatus() throws Throwable {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(null, "new comment", true, true);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", "   ");
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), purchaser.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.BAD_REQUEST, OrderMessages.STATUS_CANNOT_BE_NULL);
    }

    @Test
    public void test_updateOrderByOrderNumber_changeToUnreviewed() throws Throwable {
        OrderEntity order = getOrderWithExpectedStatus(OrderStatus.APPROVED, null, true, true );
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(order.getStatus(), null, false, false);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", order.getOrderNumber());
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), approver.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.BAD_REQUEST, OrderMessages.CANNOT_SET_TRUE_TO_FALSE);
    }

    @Test
    public void test_updateOrderByOrderNumber_updateSubmittedOrder() throws Throwable {
        OrderEntity order = getOrderWithExpectedStatus(OrderStatus.SUBMITTED, null, true, true );
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.PROCESSED, null, false, false);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", order.getOrderNumber());
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), approver.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.BAD_REQUEST, OrderMessages.ORDER_INFO_CANNOT_CHANGE_FROM_SUBMITTED);
    }

    @Test
    public void test_updateOrderByOrderNumber_updateCanceledOrder() throws Throwable {
        OrderEntity order = getOrderWithExpectedStatus(OrderStatus.CANCELED, null, true, true );
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.PROCESSED, null, false, false);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", order.getOrderNumber());
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), approver.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.BAD_REQUEST, OrderMessages.ORDER_INFO_CANNOT_CHANGE_FROM_CANCELED);
    }

    @Test
    public void test_updateOrderByOrderNumber_revertOrderStatus() throws Throwable {
        OrderEntity order = getOrderWithExpectedStatus(OrderStatus.APPROVED, null, true, true );
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatus.PROCESSED, null, false, false);
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("orderNumber", order.getOrderNumber());
        variables.putPOJO("orderStatusDTO", orderStatusDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(approver.getUsername(), approver.getPassword())
                .perform(UPDATE_ORDER_BY_ORDER_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_updateOrderByOrderNumber(response, HttpStatus.BAD_REQUEST, MessageFormat.format(OrderMessages.ORDER_STATUS_CHANGED_BACK_ERROR, order.getStatus(), orderStatusDTO.getStatus()));
    }

    @Test
    public void test_getUnreviewedNumber_normal() throws Throwable {
        createOrderForTest();
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_UNREVIEWED_NUMBER_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(GET_UNREVIEWED_NUMBER_DATA_JSON_PATH)
                .as(UnreviewedOrderNumberResponseDTO.class)
                .matches((result) ->
                        result.getUnreviewedByApprover() == 1 && result.getUnreviewedByPurchaser() == 0
                );
    }
    @Test
    public void test_getUnreviewedNumber_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_UNREVIEWED_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_getUnreviewedNumber(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getUnreviewedNumber_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(purchaser.getUsername(), "incorrectPassword")
                .perform(GET_UNREVIEWED_NUMBER_GRAPHQL_RESOURCE, variables);

        assertError_getUnreviewedNumber(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }


    private String createOrderForTest() throws Throwable {
        Long userId = purchaser.getId();
        String username = purchaser.getUsername();
        OrderDTO orderDTO = createOrderDTOInstance();
        shoppingCartService.addCartItemInShoppingCart(userId, 1L, 1);
        OrderEntity orderEntity = orderService.addNewOrder(userId, username, orderDTO.getRegion(), orderDTO.getLocation(),
                orderDTO.getShipping().getShippingType(), orderDTO.getShipping().getReceiverId(), orderDTO.getEventId(), orderDTO.getEventNumber());
        OrderUtilForTest.waitChangeForOrderStatus(orderEntity.getOrderNumber(), orderRepository, OrderStatus.SUBMITTED, 5);

        return orderEntity.getOrderNumber();
    }

    private OrderEntity getOrderWithExpectedStatus(OrderStatus expectedStatus, String expectedComments, Boolean expectedReviewedByPRCH, Boolean expectedReviewedByAPV) throws Throwable {
        String orderNumber = createOrderForTest();
        OrderEntity order = orderRepository.findOrderByOrderNumber(orderNumber);
        order.setStatus(expectedStatus);
        order.setComments(expectedComments);
        order.setReviewedByPRCH(expectedReviewedByPRCH);
        order.setReviewedByAPV(expectedReviewedByAPV);

        return orderRepository.save(order);
    }

    private OrderDTO createOrderDTOInstance() {
        String location = "location info";
        String eventId = "eventId info";
        String eventNumber = "eventNumber info";
        ShippingEntity shipping = new ShippingEntity();
        shipping.setShippingType("Next day");
        shipping.setReceiverId("receiverId info");
        return new OrderDTO(RegionType.LOCATION_1, location, shipping, eventId, eventNumber);
    }

    private void assertError_createOrder(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, CREATE_ORDER_DATA_JSON_PATH);
    }

    private void assertError_getOrderByOrderNumber(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH);
    }

    private void assertError_updateOrderByOrderNumber(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, UPDATE_ORDER_BY_ORDER_NUMBER_DATA_JSON_PATH);
    }

    private void assertError_getUnreviewedNumber(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_UNREVIEWED_NUMBER_DATA_JSON_PATH);
    }
}