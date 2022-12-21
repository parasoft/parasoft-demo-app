package com.parasoft.demoapp.config.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.parasoft.demoapp.config.MQConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DEFAULT_ORDER_SERVICE_REQUEST_QUEUE = MQConfig.INVENTORY_REQUEST;
    public static final String DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE = MQConfig.INVENTORY_RESPONSE;
    public static final String INVENTORY_DIRECT_EXCHANGE = "inventory.direct";
    public static final String INVENTORY_QUEUE_REQUEST_ROUTING_KEY = "inventory.queue.request";
    public static final String INVENTORY_QUEUE_RESPONSE_ROUTING_KEY = "inventory.queue.response";

    @Getter @Setter private static String orderServiceSendToQueue = DEFAULT_ORDER_SERVICE_REQUEST_QUEUE;
    @Getter @Setter private static String orderServiceListenToQueue = DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE;

    @Value("${spring.rabbitmq.host}")
    private String rabbitMqHost;
    @Value("${spring.rabbitmq.port}")
    private int rabbitMqPort;
    @Value("${spring.rabbitmq.username}")
    private String user;
    @Value("${spring.rabbitmq.password}")
    private String password;


    @Bean
    public ConnectionFactory connection() throws Exception {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitMqHost);
        factory.setPort(rabbitMqPort);
        factory.setUsername(user);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        return simpleRabbitListenerContainerFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue orderServiceSendToQueue() {
        return new Queue(orderServiceSendToQueue);
    }

    @Bean
    public Queue orderServiceListenToQueue() {
        return new Queue(orderServiceListenToQueue);
    }

    @Bean
    public Queue inventoryServiceSendToQueue() {
        return new Queue(DEFAULT_ORDER_SERVICE_RESPONSE_QUEUE);
    }

    @Bean
    public Queue inventoryServiceListenToQueue() {
        return new Queue(DEFAULT_ORDER_SERVICE_REQUEST_QUEUE);
    }

    @Bean
    public Binding orderServiceSendToQueueBinding() {
        return BindingBuilder.bind(orderServiceSendToQueue()).to(inventoryDirectExchange()).with(RabbitMQConfig.INVENTORY_QUEUE_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding inventoryServiceSendToQueueBinding() {
        return BindingBuilder.bind(inventoryServiceSendToQueue()).to(inventoryDirectExchange()).with(RabbitMQConfig.INVENTORY_QUEUE_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public DirectExchange inventoryDirectExchange() {
        return new DirectExchange(RabbitMQConfig.INVENTORY_DIRECT_EXCHANGE);
    }
}
