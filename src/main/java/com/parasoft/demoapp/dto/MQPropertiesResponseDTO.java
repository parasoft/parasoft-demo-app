package com.parasoft.demoapp.dto;

import lombok.Data;

@Data
public class MQPropertiesResponseDTO {
    private ActiveMQConfigResponse activeMqConfig;
    private KafkaConfigResponse kafkaConfig;

    public MQPropertiesResponseDTO(ActiveMQConfigResponse activeMqConfig,
                                   KafkaConfigResponse kafkaConfig) {
        this.activeMqConfig = activeMqConfig;
        this.kafkaConfig = kafkaConfig;
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
}
