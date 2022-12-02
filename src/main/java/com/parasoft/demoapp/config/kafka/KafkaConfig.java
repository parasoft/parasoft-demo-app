package com.parasoft.demoapp.config.kafka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Slf4j
public class KafkaConfig {
    public static final String DEFAULT_ORDER_SERVICE_REQUEST_TOPIC = "inventory.request";
    public static final String DEFAULT_ORDER_SERVICE_RESPONSE_TOPIC = "inventory.response";
}
