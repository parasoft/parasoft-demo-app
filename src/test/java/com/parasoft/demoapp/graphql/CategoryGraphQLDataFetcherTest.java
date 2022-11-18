package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.dto.CategoryDTO;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import lombok.SneakyThrows;
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
import java.util.*;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@GraphQLTest
public class CategoryGraphQLDataFetcherTest {

    private static final String CATEGORIES_GRAPHQL_RESOURCE = "graphql/categories/getCategories.graphql";
    private static final String CATEGORIES_DATA_JSON_PATH = DATA_PATH + ".getCategories";
    private static final String CATEGORY_BY_ID_GRAPHQL_RESOURCE = "graphql/categories/getCategoryById.graphql";
    private static final String CATEGORY_BY_ID_DATA_JSON_PATH = DATA_PATH + ".getCategoryById";
    private static final String CATEGORY_BY_NAME_GRAPHQL_RESOURCE = "graphql/categories/getCategoryByName.graphql";
    private static final String CATEGORY_BY_NAME_DATA_JSON_PATH = DATA_PATH + ".getCategoryByName";
    private static final String DELETE_CATEGORY_GRAPHQL_RESOURCE = "graphql/categories/deleteCategoryById.graphql";
    private static final String DELETE_CATEGORY_DATA_JSON_PATH = DATA_PATH + ".deleteCategoryById";
    private static final String ADD_NEW_CATEGORY_GRAPHQL_RESOURCE = "graphql/categories/addCategory.graphql";
    private static final String ADD_NEW_CATEGORY_DATA_JSON_PATH = DATA_PATH + ".addCategory";
    private static final String UPDATE_CATEGORY_GRAPHQL_RESOURCE = "graphql/categories/updateCategory.graphql";
    private static final String UPDATE_CATEGORY_DATA_JSON_PATH = DATA_PATH + ".updateCategory";

    @Autowired private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private CategoryService categoryService;

    @Autowired private GlobalPreferencesService globalPreferencesService;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
    }

    @Before
    public void conditionalBefore() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_addCategory_normal", "test_deleteCategoryById_normal", "test_updateCategory_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @After
    public void conditionalAfter() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_addCategory_normal", "test_deleteCategoryById_normal", "test_updateCategory_normal"));
        if (testNames.contains(testName.getMethodName())) {
            GraphQLTestUtil.resetDatabase(globalPreferencesService);
        }
    }

    @Test
    public void test_getCategories_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("searchString", "e");
        variables.put("page", 0);
        variables.put("size", 100);
        ArrayNode sortArray = objectMapper.valueToTree(new ArrayList<>(Collections.singletonList("id,desc")));
        variables.putArray("sort").addAll(sortArray);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORIES_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
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
                .has(new Condition<>(c -> c.getName().equals("Tents"), "name Tents"), Index.atIndex(0))
                .has(new Condition<>(c -> c.getName().equals("Sleeping bags"), "name Sleeping bags"), Index.atIndex(1))
                .size()
                .isEqualTo(2);
    }

    @Test
    public void test_getCategories_invalidSort() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        ArrayNode sortArray = objectMapper.valueToTree(new ArrayList<>(Collections.singletonList("id,invalid")));
        variables.putArray("sort").addAll(sortArray);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORIES_GRAPHQL_RESOURCE, variables);

        assertError_getCategories(response, HttpStatus.INTERNAL_SERVER_ERROR, "No property invalid found for type CategoryEntity!");
    }

    @Test
    public void test_getCategories_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(CATEGORIES_GRAPHQL_RESOURCE, variables);

        assertError_getCategories(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getCategoryByName_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryName", "Backpacks");

        GraphQLResponse response = graphQLTestTemplate
                .perform(CATEGORY_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(CATEGORY_BY_NAME_DATA_JSON_PATH)
                .as(CategoryEntity.class)
                .hasFieldOrPropertyWithValue("name", "Backpacks");
    }

    @Test
    public void test_getCategoryByName_categoryNameNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryName", "CategoryNotFound");

        GraphQLResponse response = graphQLTestTemplate
                .perform(CATEGORY_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_getCategoryByName(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.CATEGORY_NAME_NOT_FOUND, "CategoryNotFound"));
    }

    @Test
    public void test_getCategoryByName_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryName", "Backpacks");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(CATEGORY_BY_NAME_GRAPHQL_RESOURCE, variables);

        assertError_getCategoryByName(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getCategoryById_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 3);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORY_BY_ID_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(CATEGORY_BY_ID_DATA_JSON_PATH)
                .as(CategoryEntity.class)
                .has(new Condition<>(c -> c.getName().equals("Tents"), "name Tents"));
    }

    @Test
    public void test_getCategoryById_invalidCategoryId() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 77);

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(CATEGORY_BY_ID_GRAPHQL_RESOURCE, variables);

        assertError_getCategoryById(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, 77));
    }

    @Test
    public void test_getCategoryById_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "3");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(CATEGORY_BY_ID_GRAPHQL_RESOURCE, variables);

        assertError_getCategoryById(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }
    
    @SneakyThrows
    @Test
    public void test_deleteCategoryById_normal() {
        CategoryEntity categoryEntity = categoryService.addNewCategory(
                "foo", "name foo", "/foo");

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", categoryEntity.getId());

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(DELETE_CATEGORY_DATA_JSON_PATH)
                .as(Long.class)
                .isEqualTo(categoryEntity.getId());
    }

    @Test
    public void test_deleteCategoryById_categoryIdNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "0");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_deleteCategoryById(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, 0));
    }

    @Test
    public void test_deleteCategoryById_invalidCategoryId() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "3");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_deleteCategoryById(response, HttpStatus.BAD_REQUEST, MessageFormat.format(AssetMessages.THERE_IS_AT_LEAST_ONE_ITEM_IN_THE_CATEGORY, 3));
    }

    @Test
    public void test_deleteCategoryById_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "0");

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(DELETE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_deleteCategoryById(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_addCategory_normal() throws IOException {
        final String name = "New Category";
        final String description = "Description for New Category";
        final String imagePath = "/uploaded_images/outdoor/new-category.png";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(new CategoryDTO(name, description, imagePath)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(ADD_NEW_CATEGORY_DATA_JSON_PATH)
                .as(CategoryEntity.class).satisfies( categoryEntity -> {
                    assertThat(categoryEntity.getId()).isNotNull();
                    assertThat(categoryEntity.getName()).isEqualTo(name);
                    assertThat(categoryEntity.getDescription()).isEqualTo(description);
                    assertThat(categoryEntity.getImage()).isEqualTo(imagePath);
                });
    }

    @Test
    public void test_addCategory_nameExist() throws IOException {
        final String name = "Sleeping bags";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                new CategoryDTO(name, "Description for Sleeping bags", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_addCategory(response, HttpStatus.BAD_REQUEST, MessageFormat.format(AssetMessages.CATEGORY_NAME_EXISTS_ALREADY, name));
    }

    @Test
    public void test_addCategory_emptyName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                new CategoryDTO("", "Description for New Category", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_addCategory(response, HttpStatus.BAD_REQUEST, AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK);
    }

    @Test
    public void test_addCategory_emptyDescription() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                new CategoryDTO("New Category", "", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_addCategory(response, HttpStatus.BAD_REQUEST, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);
    }

    @Test
    public void test_addCategory_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                new CategoryDTO("New Category", "Description for New Category", null)));

        GraphQLResponse response = graphQLTestTemplate
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_addCategory(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_addCategory_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                new CategoryDTO("New Category", "Description for New Category", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth("incorrectUsername", "incorrectPassword")
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_addCategory(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_updateCategory_normal() throws IOException {
        final String name = "foo";
        final String description = "name foo";
        final String imagePath = "/foo";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(new CategoryDTO(name, description, imagePath)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(UPDATE_CATEGORY_DATA_JSON_PATH)
                .as(CategoryEntity.class).satisfies( categoryEntity -> {
                    assertThat(categoryEntity.getId()).isEqualTo(1L);
                    assertThat(categoryEntity.getName()).isEqualTo(name);
                    assertThat(categoryEntity.getDescription()).isEqualTo(description);
                    assertThat(categoryEntity.getImage()).isEqualTo(imagePath);
                });
    }

    @Test
    public void test_updateCategory_nameExist() throws IOException {
        final String name = "Sleeping bags";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 2L);
        variables.set("categoryDto", objectMapper.valueToTree(
                new CategoryDTO(name, "Description for Sleeping bags", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_updateCategory(response, HttpStatus.BAD_REQUEST, MessageFormat.format(AssetMessages.CATEGORY_NAME_EXISTS_ALREADY, name));
    }

    @Test
    public void test_updateCategory_emptyName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                new CategoryDTO("", "foo name", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_updateCategory(response, HttpStatus.BAD_REQUEST, AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK);
    }

    @Test
    public void test_updateCategory_emptyDescription() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                new CategoryDTO("foo", "", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_updateCategory(response, HttpStatus.BAD_REQUEST, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);
    }

    @Test
    public void test_updateCategory_categoryIdNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 5L);
        variables.set("categoryDto", objectMapper.valueToTree(
                new CategoryDTO("foo", "name foo", "/foo")));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_updateCategory(response, HttpStatus.NOT_FOUND, MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, 5L));
    }

    @Test
    public void test_updateCategory_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                new CategoryDTO("foo", "name foo", null)));

        GraphQLResponse response = graphQLTestTemplate
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_updateCategory(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_updateCategory_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                new CategoryDTO("foo", "name foo", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertError_updateCategory(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    private void assertError_getCategories(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, CATEGORIES_DATA_JSON_PATH);
    }

    private void assertError_getCategoryById(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, CATEGORY_BY_ID_DATA_JSON_PATH);
    }

    private void assertError_getCategoryByName(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, CATEGORY_BY_NAME_DATA_JSON_PATH);
    }

    private void assertError_deleteCategoryById(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, DELETE_CATEGORY_DATA_JSON_PATH);
    }

    private void assertError_addCategory(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, ADD_NEW_CATEGORY_DATA_JSON_PATH);
    }

    private void assertError_updateCategory(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, UPDATE_CATEGORY_DATA_JSON_PATH);
    }
}