package com.parasoft.demoapp.graphql;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Small Boot 4-compatible replacement for the archived Kickstart test template.
 * The GraphQL integration tests use only Basic Auth, resource loading and JSON posts.
 */
public class GraphQLTestTemplate {

    private final ResourceLoader resourceLoader;
    private final TestRestTemplate restTemplate;
    private final String graphqlMapping;
    private final ObjectMapper objectMapper;
    private final HttpHeaders headers = new HttpHeaders();

    public GraphQLTestTemplate(ResourceLoader resourceLoader, TestRestTemplate restTemplate,
                               String graphqlMapping, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.restTemplate = restTemplate;
        this.graphqlMapping = graphqlMapping;
        this.objectMapper = objectMapper;
    }

    public GraphQLTestTemplate withBasicAuth(String username, String password) {
        headers.setBasicAuth(username, password);
        return this;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public GraphQLResponse perform(String graphqlResource, ObjectNode variables) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + graphqlResource);
        String query;
        try (InputStream inputStream = resource.getInputStream()) {
            query = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }

        ObjectNode request = objectMapper.createObjectNode();
        request.put("query", query);
        if (variables != null) {
            request.set("variables", variables);
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.putAll(headers);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(
                graphqlMapping, new HttpEntity<>(request, requestHeaders), String.class);
        return new Boot4GraphQLResponse(response);
    }
}
