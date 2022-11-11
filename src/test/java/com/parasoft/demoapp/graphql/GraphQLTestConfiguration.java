package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@TestConfiguration
public class GraphQLTestConfiguration {

    @Bean
    public GraphQLTestTemplate graphQLTestTemplate(
            final ResourceLoader resourceLoader,
            @Autowired(required = false) final TestRestTemplate restTemplate,
            @Value("${graphql.url:graphql}") final String graphqlUrl,
            final ObjectMapper objectMapper) {
        return new GraphQLTestTemplate(resourceLoader, restTemplate, "/" + graphqlUrl, objectMapper);
    }
}
