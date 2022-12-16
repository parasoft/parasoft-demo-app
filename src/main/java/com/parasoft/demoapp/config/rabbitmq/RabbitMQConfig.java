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
    public Queue inventoryServiceRequestQueue() {
        return new Queue(DEFAULT_ORDER_SERVICE_REQUEST_QUEUE);
    }

    @Bean
    public Queue inventoryServiceResponseQueue() {
        return new Queue(DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("inventory.exchange");
    }

    @Bean
    public Binding inventoryServiceRequestBindingExchange() {
        return BindingBuilder.bind(inventoryServiceRequestQueue()).to(exchange()).with("request");
    }

    @Bean
    public Binding inventoryServiceResponseBindingExchange() {
        return BindingBuilder.bind(inventoryServiceResponseQueue()).to(exchange()).with("response");
    }
}
