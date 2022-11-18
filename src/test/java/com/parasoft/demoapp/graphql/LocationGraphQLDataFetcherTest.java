package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.industry.LocationEntity;
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
import static com.parasoft.demoapp.model.industry.RegionType.EARTH;
import static com.parasoft.demoapp.model.industry.RegionType.LOCATION_1;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@GraphQLTest
public class LocationGraphQLDataFetcherTest {

    private static final String LOCATION_GRAPHQL_RESOURCE = "graphql/locations/getLocation.graphql";
    private static final String LOCATION_DATA_JSON_PATH = DATA_PATH + ".getLocation";

    @Autowired private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        graphQLTestTemplate.getHeaders().clear();
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

    private void assertError_getLocation(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage) {
        GraphQLTestUtil.assertErrorResponse(response, expectedHttpStatus, expectedErrorMessage, LOCATION_DATA_JSON_PATH);
    }
}