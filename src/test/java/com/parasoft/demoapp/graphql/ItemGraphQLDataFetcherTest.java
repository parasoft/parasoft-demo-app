package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.dto.ItemsDTO;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
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
    private static final String ITEM_GRAPHQL_RESOURCE = "graphql/items/getItems.graphql";
    private static final String ITEM_DATA_JSON_PATH = DATA_PATH + ".getItems";
    private static final String UPDATE_ITEM_BY_ID_GRAPHQL_RESOURCE = "graphql/items/updateItemById.graphql";
    private static final String UPDATE_ITEM_BY_ID_DATA_JSON_PATH = DATA_PATH + ".updateItemById";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
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
                .perform(ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH)
                .as(PageInfo.class)
                .hasFieldOrPropertyWithValue("totalElements", 1L)
                .hasFieldOrPropertyWithValue("sort", "name: ASC")
                .hasFieldOrPropertyWithValue("totalPages", 1)
                .hasFieldOrPropertyWithValue("size", 10)
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH + ".content")
                .asListOf(ItemEntity.class)
                .has(new Condition<ItemEntity>(c -> c.getName().equals("Blue Sleeping Bag"),"name Blue Sleeping Bag"), Index.atIndex(0))
                .size()
                .isEqualTo(1);
    }

    @Test
    public void test_getItems_illegalSort() throws IOException {
        ObjectNode variable = objectMapper.createObjectNode();
        variable.put("sort", "sort");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ITEM_GRAPHQL_RESOURCE, variable);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("No property sort found for type ItemEntity!");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getItems_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(ITEM_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(GraphQLTestErrorType.UNAUTHORIZED.toString());
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                })
                .and()
                .assertThatField(ITEM_DATA_JSON_PATH).isNull();
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
