package com.parasoft.demoapp.graphql;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Bridges graphql-java-kickstart 14.x test assertions to Spring Framework 7.
 * That archived library was compiled when ResponseEntity#getStatusCode()
 * returned HttpStatus; Spring 7 returns HttpStatusCode instead.
 */
final class Boot4GraphQLResponse extends GraphQLResponse {

    private final ResponseEntity<String> response;

    Boot4GraphQLResponse(ResponseEntity<String> response) {
        super(response, legacyObjectMapper());
        this.response = response;
    }

    private static ObjectMapper legacyObjectMapper() {
        return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public boolean isOk() {
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(response.getStatusCode().value());
    }

    @Override
    public ResponseEntity<String> getRawResponse() {
        return response;
    }
}
