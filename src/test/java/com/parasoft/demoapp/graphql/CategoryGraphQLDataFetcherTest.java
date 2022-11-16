package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.dto.CategoryDTO;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Arrays;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static com.parasoft.demoapp.messages.AssetMessages.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
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
        if ("test_addCategory_normal".equals(testName.getMethodName())
                || "test_updateCategory_normal".equals(testName.getMethodName())) {
            resetDatabase();
        }
    }

    @After
    public void conditionalAfter() {
        if ("test_addCategory_normal".equals(testName.getMethodName())
                || "test_updateCategory_normal".equals(testName.getMethodName())) {
            resetDatabase();
        }
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
    public void test_getCategoryByName_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryName", "Backpacks");
        GraphQLResponse response = graphQLTestTemplate
                .perform(CATEGORY_BY_NAME_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(CATEGORY_BY_NAME_DATA_JSON_PATH)
                .as(CategoryEntity.class)
                .hasFieldOrPropertyWithValue("name", "Backpacks");
    }

    @Test
    public void test_getCategoryByName_categoryNotFoundException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryName", "CategoryNotFound");
        GraphQLResponse response = graphQLTestTemplate
                .perform(CATEGORY_BY_NAME_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Category with name CategoryNotFound is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(CATEGORY_BY_NAME_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getCategoryByName_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryName", "Backpacks");
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
    public void test_getCategoryById_normal() throws IOException {
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
    public void test_getCategoryById_invalidId() throws IOException {
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
    public void test_getCategoryById_notAuthenticated() throws IOException {
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
    
    @SneakyThrows
    @Test
    public void test_deleteCategoryById_normal() throws IOException {
        CategoryEntity categoryEntity = categoryService.addNewCategory(
                "foo", "name foo", "/foo");
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", categoryEntity.getId());
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_CATEGORY_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(DELETE_CATEGORY_DATA_JSON_PATH)
                .as(Integer.class)
                .isEqualTo(Long.valueOf(categoryEntity.getId()).intValue());
    }

    @Test
    public void test_deleteCategoryById_categoryNotFoundException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "0");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_CATEGORY_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Category with ID 0 is not found.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
                .and()
                .assertThatField(DELETE_CATEGORY_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteCategoryById_categoryHasAtLeastOneItemException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "3");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(DELETE_CATEGORY_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Unable to delete the category with ID 3 because there are items in the category.");
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
                .and()
                .assertThatField(DELETE_CATEGORY_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_deleteCategoryById_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", "0");
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
    public void test_addCategory_normal() throws IOException {
        final String name = "New Category";
        final String description = "Description for New Category";
        final String imagePath = "/uploaded_images/outdoor/new-category.png";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(createCategoryDTO(name, description, imagePath)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
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
    public void test_addCategory_400_nameAlreadyExists() throws IOException {
        final String name = "Sleeping bags";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                createCategoryDTO(name, "Description for Sleeping bags", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, MessageFormat.format(CATEGORY_NAME_EXISTS_ALREADY, name),
                HttpStatus.BAD_REQUEST.value(), ADD_NEW_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_addCategory_400_emptyName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                createCategoryDTO("", "Description for New Category", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, CATEGORY_NAME_CANNOT_BE_BLANK,
                HttpStatus.BAD_REQUEST.value(), ADD_NEW_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_addCategory_400_emptyDescription() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                createCategoryDTO("New Category", "", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, DESCRIPTION_CANNOT_BE_BLANK,
                HttpStatus.BAD_REQUEST.value(), ADD_NEW_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_addCategory_401_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                createCategoryDTO("New Category", "Description for New Category", null)));

        GraphQLResponse response = graphQLTestTemplate
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assert401NotAuthorizedError(response, ADD_NEW_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_addCategory_401_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.set("categoryDTO", objectMapper.valueToTree(
                createCategoryDTO("New Category", "Description for New Category", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth("incorrectUsername", "incorrectPassword")
                .perform(ADD_NEW_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assert401NotAuthorizedError(response, ADD_NEW_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_updateCategory_normal() throws IOException {
        final String name = "foo";
        final String description = "name foo";
        final String imagePath = "/foo";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(createCategoryDTO(name, description, imagePath)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
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
    public void test_updateCategory_400_nameAlreadyExists() throws IOException {
        final String name = "Sleeping bags";
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 2L);
        variables.set("categoryDto", objectMapper.valueToTree(
                createCategoryDTO(name, "Description for Sleeping bags", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, MessageFormat.format(CATEGORY_NAME_EXISTS_ALREADY, name),
                HttpStatus.BAD_REQUEST.value(), UPDATE_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_updateCategory_400_emptyName() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                createCategoryDTO("", "foo name", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, CATEGORY_NAME_CANNOT_BE_BLANK,
                HttpStatus.BAD_REQUEST.value(), UPDATE_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_updateCategory_400_emptyDescription() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                createCategoryDTO("foo", "", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, DESCRIPTION_CANNOT_BE_BLANK,
                HttpStatus.BAD_REQUEST.value(), UPDATE_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_updateCategory_404_categoryNotFoundException() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 5L);
        variables.set("categoryDto", objectMapper.valueToTree(
                createCategoryDTO("foo", "name foo", "/foo")));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assertErrorWithNullData(response, "Category with ID 5 is not found.",
                HttpStatus.NOT_FOUND.value(), UPDATE_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_updateCategory_401_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                createCategoryDTO("foo", "name foo", null)));

        GraphQLResponse response = graphQLTestTemplate
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assert401NotAuthorizedError(response, UPDATE_CATEGORY_DATA_JSON_PATH);
    }

    @Test
    public void test_updateCategory_401_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("categoryId", 1L);
        variables.set("categoryDto", objectMapper.valueToTree(
                createCategoryDTO("foo", "name foo", null)));

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "invalidPass")
                .perform(UPDATE_CATEGORY_GRAPHQL_RESOURCE, variables);

        assertResponseOk(response);
        assert401NotAuthorizedError(response, UPDATE_CATEGORY_DATA_JSON_PATH);
    }

    private static CategoryDTO createCategoryDTO(String name, String description, String imagePath) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(name);
        categoryDTO.setDescription(description);
        categoryDTO.setImagePath(imagePath);
        return categoryDTO;
    }

    private void assertResponseOk(GraphQLResponse response) {
        assertThat(response).isNotNull();
        log.info("{} response:\n{}", testName.getMethodName(), response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
    }

    private static void assertErrorWithNullData(GraphQLResponse response, String errorMessage,
                                                int errorExtensionStatusCode, String dataJsonPath) {
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(errorMessage);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(errorExtensionStatusCode);
                })
                .and()
                .assertThatField(dataJsonPath).isNull();
    }

    private static void assert401NotAuthorizedError(GraphQLResponse response, String dataJsonPath) {
        assertErrorWithNullData(response, GraphQLTestErrorType.UNAUTHORIZED.toString(),
                HttpStatus.UNAUTHORIZED.value(), dataJsonPath);
    }

    private void resetDatabase() {
        log.info("Reset database...");
        globalPreferencesService.resetAllIndustriesDatabase();
    }
}