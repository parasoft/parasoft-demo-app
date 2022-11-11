package com.parasoft.demoapp.graphql;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Configuration
public class GraphQLConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(getRequestFactory())
                .build();
    }

    private Supplier<ClientHttpRequestFactory> getRequestFactory() {
        return HttpComponentsClientHttpRequestFactory::new;
    }
}
