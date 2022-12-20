package com.parasoft.demoapp.config.rabbitmq;

import com.parasoft.demoapp.config.MQConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String DEFAULT_ORDER_SERVICE_REQUEST_QUEUE = MQConfig.INVENTORY_REQUEST;
    public static final String DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE = MQConfig.INVENTORY_RESPONSE;
}
