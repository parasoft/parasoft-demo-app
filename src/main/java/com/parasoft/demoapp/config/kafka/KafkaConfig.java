package com.parasoft.demoapp.config.kafka;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                groupId);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        return props;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public ProducerFactory<String, InventoryOperationRequestMessageDTO> operationRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public ConsumerFactory<String, InventoryOperationRequestMessageDTO> operationRequestConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(InventoryOperationRequestMessageDTO.class));
    }

    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public ProducerFactory<String, InventoryOperationResultMessageDTO> operationResultProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public ConsumerFactory<String, InventoryOperationResultMessageDTO> operationResultConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(InventoryOperationResultMessageDTO.class));
    }

    // this container factory is for deserializing consumed object data - InventoryOperationRequestMessageDTO
    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public ConcurrentKafkaListenerContainerFactory<String, InventoryOperationRequestMessageDTO> operationRequestContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryOperationRequestMessageDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(operationRequestConsumerFactory());
        return factory;
    }

    // this container factory is for deserializing consumed object data - InventoryOperationResultMessageDTO
    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public ConcurrentKafkaListenerContainerFactory<String, InventoryOperationResultMessageDTO> operationResultContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryOperationResultMessageDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(operationResultConsumerFactory());
        return factory;
    }

    // this kafka template is for serializing produced object data - InventoryOperationRequestMessageDTO
    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public KafkaTemplate<String, InventoryOperationRequestMessageDTO> operationRequestKafkaTemplate() {
        return new KafkaTemplate<>(operationRequestProducerFactory());
    }

    // this kafka template is for serializing produced object data - InventoryOperationResultMessageDTO
    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
    public KafkaTemplate<String, InventoryOperationResultMessageDTO> operationResultKafkaTemplate() {
        return new KafkaTemplate<>(operationResultProducerFactory());
    }
}