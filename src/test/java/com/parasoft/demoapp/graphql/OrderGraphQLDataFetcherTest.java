package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.dto.OrderDTO;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.ShoppingCartService;
import com.parasoft.demoapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static com.parasoft.demoapp.model.industry.RegionType.LOCATION_1;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class OrderGraphQLDataFetcherTest {

    private static final String ORDER_GRAPHQL_RESOURCE = "graphql/orders/createOrder.graphql";
    private static final String CREATE_ORDER_DATA_JSON_PATH = DATA_PATH + ".createOrder";

    @Autowired private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private ShoppingCartService shoppingCartService;

    @Autowired private ItemService itemService;

    @Autowired private UserService userService;

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
    }

    private OrderDTO getOrderDTOInstance() {
        RegionType regionType = LOCATION_1;
        String location = "location info";
        String receiverId = "receiverId info";
        String eventId = "eventId info";
        String eventNumber = "eventNumber info";
        return new OrderDTO(regionType, location, receiverId, eventId, eventNumber);
    }

    @Test
    public void test_createOrder_normal() throws Exception {
        UserEntity user = userService.getUserByUsername("purchaser");
        ItemEntity item = itemService.getItemById(1L);
        CartItemEntity cartItem = shoppingCartService.addCartItemInShoppingCart(user.getId(), item.getId(), item.getInStock());
        OrderDTO orderDTO = getOrderDTOInstance();
        ObjectNode orderDtoObjectNode = objectMapper.createObjectNode().putPOJO("orderDTO", orderDTO);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ORDER_GRAPHQL_RESOURCE, orderDtoObjectNode);

        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(CREATE_ORDER_DATA_JSON_PATH)
                .as(OrderEntity.class);
    }

    @Test
    public void test_createOrder_regionNotExistOnCurrentIndustry() throws IOException {
    }

    @Test
    public void test_createOrder_invalidOrNullRegionValue() throws IOException {
    }

    @Test
    public void test_createOrder_notAuthenticated() throws IOException {
    }

    @Test
    public void test_createOrder_withIncorrectUsernameOrPassword() throws IOException {
    }
}