package com.parasoft.demoapp.dto;

import lombok.Data;

import static com.parasoft.demoapp.config.rabbitmq.RabbitMQConfig.*;

@Data
public class MQPropertiesResponseDTO {
    private ActiveMQConfigResponse activeMqConfig;
    private KafkaConfigResponse kafkaConfig;
    private RabbitMQConfigResponse rabbitMQConfig;

    public MQPropertiesResponseDTO(ActiveMQConfigResponse activeMqConfig,
                                   KafkaConfigResponse kafkaConfig,
                                   RabbitMQConfigResponse rabbitMQConfig) {
        this.activeMqConfig = activeMqConfig;
        this.kafkaConfig = kafkaConfig;
        this.rabbitMQConfig = rabbitMQConfig;
    }

    @Data
    public static class ActiveMQConfigResponse {
        private String brokerUrl;
        private String username;
        private String password;
        private String initialContextClass;
        private String connectionFactory;

        public ActiveMQConfigResponse(String brokerUrl, String username, String password) {
            this.brokerUrl = brokerUrl;
            this.username = username;
            this.password = password;
            this.initialContextClass = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
            this.connectionFactory = "ConnectionFactory";
        }
    }

    @Data
    public static class KafkaConfigResponse {
        private String bootstrapServers;
        private String groupId;

        public KafkaConfigResponse(String bootstrapServers, String groupId) {
            this.bootstrapServers = bootstrapServers;
            this.groupId = groupId;
        }
    }

    @Data
    public static class RabbitMQConfigResponse  {
        private String rabbitMqHost;
        private int rabbitMqPort;
        private String username;
        private String password;
        private String requestDirectExchange;
        private String responseDirectExchange;
        private String requestRoutingKey;
        private String responseRoutingKey;

        public RabbitMQConfigResponse(String rabbitMqHost, int rabbitMqPort, String username, String password) {
            this.rabbitMqHost = rabbitMqHost;
            this.rabbitMqPort = rabbitMqPort;
            this.username = username;
            this.password = password;
            this.requestDirectExchange = INVENTORY_DIRECT_EXCHANGE;
            this.responseDirectExchange = INVENTORY_DIRECT_EXCHANGE;
            this.requestRoutingKey = INVENTORY_QUEUE_REQUEST_ROUTING_KEY;
            this.responseRoutingKey = INVENTORY_QUEUE_RESPONSE_ROUTING_KEY;
        }
    }
}
