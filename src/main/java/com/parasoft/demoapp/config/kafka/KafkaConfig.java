package com.parasoft.demoapp.config.kafka;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
@Getter
@Slf4j
public class KafkaConfig {
    public static final String ORDER_SERVICE_REQUEST = "inventory.request";
    public static final String ORDER_SERVICE_RESPONSE = "inventory.response";

    @Getter private static final String inventoryServiceListenToQueue = ORDER_SERVICE_REQUEST;
    @Getter private static ActiveMQQueue inventoryServiceSendToQueue = new ActiveMQQueue(KafkaConfig.ORDER_SERVICE_RESPONSE);
    @Getter @Setter
    private static String orderServiceListenToQueue = ORDER_SERVICE_RESPONSE;
    @Getter @Setter private static ActiveMQQueue orderServiceSendToQueue = new ActiveMQQueue(KafkaConfig.ORDER_SERVICE_REQUEST);
}
