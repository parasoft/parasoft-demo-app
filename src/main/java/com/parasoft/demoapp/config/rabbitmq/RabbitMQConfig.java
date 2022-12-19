package com.parasoft.demoapp.config.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DEFAULT_ORDER_SERVICE_REQUEST_QUEUE = "inventory.request";
    public static final String DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE = "inventory.response";
    public static final String INVENTORY_DIRECT_EXCHANGE = "inventory.direct";
    public static final String INVENTORY_QUEUE_REQUEST_ROUTING_KEY = "inventory.queue.request";
    public static final String INVENTORY_QUEUE_RESPONSE_ROUTING_KEY = "inventory.queue.response";

    private static final Queue orderServiceSendToQueue = new Queue(RabbitMQConfig.DEFAULT_ORDER_SERVICE_REQUEST_QUEUE);
    private static final Queue inventoryServiceSendToQueue = new Queue(RabbitMQConfig.DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE);
    private static final DirectExchange inventoryDirectExchange = new DirectExchange(RabbitMQConfig.INVENTORY_DIRECT_EXCHANGE);

    @Value("${spring.rabbitmq.host}")
    private String rabbitMqHost;
    @Value("${spring.rabbitmq.port}")
    private int rabbitMqPort;
    @Value("${spring.rabbitmq.username}")
    private String user;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public Connection connection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHost);
        factory.setPort(rabbitMqPort);
        factory.setUsername(user);
        factory.setPassword(password);
        return factory.newConnection();
    }

    @Bean
    public Binding inventoryServiceRequestBindingExchange() {
        return BindingBuilder.bind(orderServiceSendToQueue).to(inventoryDirectExchange).with(RabbitMQConfig.INVENTORY_QUEUE_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding inventoryServiceResponseBindingExchange() {
        return BindingBuilder.bind(inventoryServiceSendToQueue).to(inventoryDirectExchange).with(RabbitMQConfig.INVENTORY_QUEUE_RESPONSE_ROUTING_KEY);
    }
}
