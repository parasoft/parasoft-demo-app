package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MQPropertiesResponseDTO {
    @Autowired
    private ActiveMQConfigResponse activeMqConfig;
    @Autowired
    private KafkaConfigResponse kafkaConfig;

    @Component
    class ActiveMQConfigResponse {
        @Autowired
        private ActiveMQConfig activeMQConfig;

        private String brokerUrl;
        private String username;
        private String password;
        private String initialContextClass;
        private String connectionFactory;

        public String getBrokerUrl() {
            return activeMQConfig.getBrokerUrl();
        }

        public String getUsername() {
            return activeMQConfig.getUsername();
        }

        public String getPassword() {
            return activeMQConfig.getPassword();
        }

        public String getInitialContextClass() {
            return activeMQConfig.getInitialContextClass();
        }

        public String getConnectionFactory() {
            return activeMQConfig.getConnectionFactory();
        }
    }

    @Component
    class KafkaConfigResponse {
        @Autowired
        private KafkaConfig kafkaConfig;

        private String bootstrapServers;
        private String groupId;

        public String getBootstrapServers() {
            return kafkaConfig.getBootstrapServers();
        }

        public String getGroupId() {
            return kafkaConfig.getGroupId();
        }
    }
}
