package com.parasoft.demoapp.config.rabbitmq;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String DEFAULT_ORDER_SERVICE_REQUEST_QUEUE = "inventory.request";
    public static final String DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE = "inventory.response";
}
