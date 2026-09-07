package com.parasoft.demoapp.graphql;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GraphQLTestUtil {
    public static void resetDatabase(GlobalPreferencesService globalPreferencesService) {
        log.info("Reset database...");
        globalPreferencesService.resetAllIndustriesDatabase();
    }

    public static void assertErrorResponse(GraphQLResponse response, HttpStatus expectedHttpStatus, String expectedErrorMessage, String jsonPathToBeNull) {
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> {
                    assertThat(error.getMessage()).isEqualTo(expectedErrorMessage);
                    assertThat(error.getExtensions().get("statusCode")).isEqualTo(expectedHttpStatus.value());
                })
                .and()
                .assertThatField(jsonPathToBeNull).isNull();
    }

    public static void assertInvalidRegionTypeValue(GraphQLResponse response, RegionType regionType) throws IOException {
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        response.assertThatErrorsField().isNotNull()
                .asListOf(GraphQLTestError.class)
                .hasOnlyOneElementSatisfying(error -> assertThat(error.getMessage())
                        .contains("Invalid input for Enum 'RegionType'")
                        .contains(regionType.name()))
                .and();
        assertThat(response.readTree().has("data")).isFalse();
    }
}
