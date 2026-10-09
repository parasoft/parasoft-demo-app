package com.parasoft.demoapp.config.endpoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.time.Duration;

/**
 * Supplies the request factory selected by Spring Cloud Gateway MVC for route
 * forwarding.
 */
@Configuration(proxyBeanMethods = false)
public class GatewayHttpClientConfig {

    @Bean
    public ClientHttpRequestFactory gatewayClientHttpRequestFactory(
            @Value("${demoapp.gateway.http-client.connect-timeout:10s}") Duration connectTimeout,
            @Value("${demoapp.gateway.http-client.read-timeout:15s}") Duration readTimeout) {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(connectTimeout)
                .withReadTimeout(readTimeout);
        return ClientHttpRequestFactoryBuilder.detect().build(settings);
    }
}
