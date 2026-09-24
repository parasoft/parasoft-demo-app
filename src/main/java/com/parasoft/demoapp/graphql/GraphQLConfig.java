package com.parasoft.demoapp.graphql;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GraphQLConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactoryBuilder(ClientHttpRequestFactoryBuilder.jdk())
                .build();
    }
}
