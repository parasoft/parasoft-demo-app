package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class CategoryGraphQLDataFetcherTest {

    private static final String CATEGORIES_GRAPHQL_RESOURCE = "graphql/categories/getCategories.graphql";
    private static final String CATEGORIES_DATA_JSON_PATH = DATA_PATH + ".getCategories";
    private static final String CATEGORY_BY_ID_GRAPHQL_RESOURCE = "graphql/categories/getCategoryById.graphql";
    private static final String CATEGORY_BY_ID_DATA_JSON_PATH = DATA_PATH + ".getCategoryById";

    @Autowired private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
    }

    @Test
    public void test_getCategories_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("searchString", "e");
        variables.put("page", 0);
        variables.put("size", 100);
        ArrayNode sortArray = objectMapper.valueToTree(new ArrayList<String>(Arrays.asList("id,desc")));
        variables.putArray("sort").addAll(sortArray);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORIES_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(CATEGORIES_DATA_JSON_PATH)
                .as(PageInfo.class)
                .hasFieldOrPropertyWithValue("totalElements", 2L)
                .hasFieldOrPropertyWithValue("totalPages", 1)
                .hasFieldOrPropertyWithValue("size", 100)
                .hasFieldOrPropertyWithValue("sort", "id: DESC")
                .and()
                .assertThatField(CATEGORIES_DATA_JSON_PATH + ".content")
                .asListOf(CategoryEntity.class)
                .has(new Condition<CategoryEntity>(c -> c.getName().equals("Tents"), "name Tents"), Index.atIndex(0))
                .has(new Condition<CategoryEntity>(c -> c.getName().equals("Sleeping bags"), "name Sleeping bags"), Index.atIndex(1))
                .size()
                .isEqualTo(2);
    }

    @Test
    public void test_getCategories_invalidSort() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        ArrayNode sortArray = objectMapper.valueToTree(new ArrayList<String>(Arrays.asList("id,invalid")));
        variables.putArray("sort").addAll(sortArray);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORIES_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("No property invalid found for type CategoryEntity!");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
                .and()
                .assertThatField(CATEGORIES_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getCategories_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(CATEGORIES_GRAPHQL_RESOURCE, variables);
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
                .assertThatField(CATEGORIES_DATA_JSON_PATH).isNull();
    }

    @Test
    public void testGetCategoryById_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 3);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORY_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(CATEGORY_BY_ID_DATA_JSON_PATH)
                .as(CategoryEntity.class)
                .has(new Condition<CategoryEntity>(c -> c.getName().equals("Tents"), "name Tents"));;
    }

    @Test
    public void testGetCategoryById_invalidId() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 77);
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORY_BY_ID_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Category with ID 77 is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(CATEGORY_BY_ID_DATA_JSON_PATH).isNull();
    }

    @Test
    public void testGetCategoryById_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "3");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(CATEGORY_BY_ID_GRAPHQL_RESOURCE, variables);
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
                .assertThatField(CATEGORY_BY_ID_DATA_JSON_PATH).isNull();
    }
}