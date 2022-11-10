package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.parasoft.demoapp.model.industry.LocationEntity;
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
import static com.parasoft.demoapp.model.industry.RegionType.EARTH;
import static com.parasoft.demoapp.model.industry.RegionType.LOCATION_1;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@GraphQLTest
public class LocationGraphQLDataFetcherTest {

    private static final String LOCATION_GRAPHQL_RESOURCE = "graphql/locations/location.graphql";
    private static final String UNAUTHORIZED_ERR = "Current user is not authorized.";
    private static final String LOCATION_DATA_JSON_PATH = DATA_PATH + ".getLocation";
    private static final String STATUS_CODE_FIELD = "statusCode";

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
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatNoErrorsArePresent()
                .assertThatField(LOCATION_DATA_JSON_PATH)
                .as(LocationEntity.class)
                .hasNoNullFieldsOrPropertiesExcept("region");
    }

    @Test
    public void test_getLocation_regionNotExistOnCurrentIndustry() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("region", EARTH.name());
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(LOCATION_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo("Location not found.");
                   assertThat(error.getExtensions().get("statusCode")).isEqualTo(404);
                })
                .and()
                .assertThatField(LOCATION_DATA_JSON_PATH).isNull();
    }

    @Test
    public void test_getLocation_invalidOrNullRegionValue() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("region", "INVALID_REGION");
        GraphQLResponse response = graphQLTestTemplate
                .withBasicAuth(USERNAME_PURCHASER, PASSWORD)
                .perform(LOCATION_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getExtensions().get("classification"))
                            .asString().isEqualTo("ValidationError");
                })
                .and()
                .assertThatDataField().isNotPresent();
    }

    @Test
    public void test_getLocation_notAuthenticated() throws IOException {
        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("region", LOCATION_1.name());
        GraphQLResponse response = graphQLTestTemplate
                .perform(LOCATION_GRAPHQL_RESOURCE, variables);
        assertThat(response).isNotNull();
        log.info(response.getRawResponse().getBody());
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(UNAUTHORIZED_ERR);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(401);
                })
                .and()
                .assertThatField(LOCATION_DATA_JSON_PATH).isNull();
    }
}