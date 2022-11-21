package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.RegionType;
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
import java.util.Set;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.DATA_PATH;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.PASSWORD;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static com.parasoft.demoapp.model.industry.RegionType.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@GraphQLTest
public class LocationGraphQLDataFetcherTest {

    private static final String LOCATION_GRAPHQL_RESOURCE = "graphql/locations/getLocation.graphql";
    private static final String LOCATION_DATA_JSON_PATH = DATA_PATH + ".getLocation";
    private static final String GET_ALL_REGION_TYPES_GRAPHQL_RESOURCE = "graphql/locations/getAllRegionTypesOfCurrentIndustry.graphql";
    private static final String GET_ALL_REGION_TYPES_DATA_JSON_PATH = DATA_PATH + ".getAllRegionTypesOfCurrentIndustry";

    @Autowired private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
    }

    @Before
    public void conditionalBefore() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getAllRegionTypesOfCurrentIndustry_unsupportedRequest"));
        if (testNames.contains(testName.getMethodName())) {
            IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;
        }
    }

    @After
    public void conditionalAfter() {
        Set<String> testNames = new HashSet<>(Arrays.asList("test_getAllRegionTypesOfCurrentIndustry_unsupportedRequest"));
        if (testNames.contains(testName.getMethodName())) {
            IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;
        }
    }

    @Test
    public void test_getLocation_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("region", LOCATION_1.name());

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(LOCATION_GRAPHQL_RESOURCE, variables);

        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(LOCATION_DATA_JSON_PATH)
                .as(LocationEntity.class)
                .hasNoNullFieldsOrPropertiesExcept("region");
    }

    @Test
    public void test_getLocation_regionNotFound() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("region", EARTH.name());

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(LOCATION_GRAPHQL_RESOURCE, variables);

        assertError_getLocation(response, HttpStatus.NOT_FOUND, OrderMessages.LOCATION_NOT_FOUND);
    }

    @Test
    public void test_getLocation_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("region", LOCATION_1.name());

        GraphQLResponse response = graphQLTestTemplate
                .perform(LOCATION_GRAPHQL_RESOURCE, variables);

        assertError_getLocation(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getLocation_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("region", LOCATION_1.name());

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth("incorrectUsername", "incorrectPassword")
                .perform(LOCATION_GRAPHQL_RESOURCE, variables);

        assertError_getLocation(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getAllRegionTypesOfCurrentIndustry_normal() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ALL_REGION_TYPES_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatDataField().isNotNull()
                .and()
                .assertThatField(GET_ALL_REGION_TYPES_DATA_JSON_PATH)
                .asListOf(RegionType.class)
                .has(new Condition<>(c -> c.equals(LOCATION_1), "LOCATION_1"), Index.atIndex(0))
                .has(new Condition<>(c -> c.equals(LOCATION_2), "LOCATION_2"), Index.atIndex(1))
                .has(new Condition<>(c -> c.equals(LOCATION_3), "LOCATION_3"), Index.atIndex(2))
                .has(new Condition<>(c -> c.equals(LOCATION_4), "LOCATION_4"), Index.atIndex(3))
                .has(new Condition<>(c -> c.equals(LOCATION_5), "LOCATION_5"), Index.atIndex(4))
                .has(new Condition<>(c -> c.equals(LOCATION_6), "LOCATION_6"), Index.atIndex(5))
                .has(new Condition<>(c -> c.equals(LOCATION_7), "LOCATION_7"), Index.atIndex(6))
                .has(new Condition<>(c -> c.equals(LOCATION_8), "LOCATION_8"), Index.atIndex(7))
                .size()
                .isEqualTo(8);
    }

    @Test
    public void test_getAllRegionTypesOfCurrentIndustry_noAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .perform(GET_ALL_REGION_TYPES_GRAPHQL_RESOURCE, variables);

        assertError_getAllRegionTypesOfCurrentIndustry(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getAllRegionTypesOfCurrentIndustry_incorrectAuthentication() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, "incorrectPassword")
                .perform(GET_ALL_REGION_TYPES_GRAPHQL_RESOURCE, variables);

        assertError_getAllRegionTypesOfCurrentIndustry(response, HttpStatus.UNAUTHORIZED, ConfigMessages.USER_IS_NOT_AUTHORIZED);
    }

    @Test
    public void test_getAllRegionTypesOfCurrentIndustry_unsupportedRequest() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        IndustryType currentIndustry = IndustryType.GOVERNMENT;
        IndustryRoutingDataSource.currentIndustry = currentIndustry;

        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(GET_ALL_REGION_TYPES_GRAPHQL_RESOURCE, variables);

        assertError_getAllRegionTypesOfCurrentIndustry(response, HttpStatus.BAD_REQUEST, MessageFormat.format(OrderMessages.UNSUPPORTED_OPERATION_IN_CURRENT_INDUSTRY, currentIndustry));
    }

    private void assertError_getLocation(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, LOCATION_DATA_JSON_PATH);
    }

    private void assertError_getAllRegionTypesOfCurrentIndustry(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, GET_ALL_REGION_TYPES_DATA_JSON_PATH);
    }
}