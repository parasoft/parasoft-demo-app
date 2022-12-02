package com.parasoft.demoapp.config.activemq;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import java.net.URI;

@Configuration
@EnableJms
@Getter
@Slf4j
public class ActiveMQConfig {

    public static final String TOPIC_ORDER_APPROVER = "order.approver";
    public static final String TOPIC_ORDER_PURCHASER = "order.purchaser";
    public static final String TOPIC_INDUSTRY_CHANGE = "globalPreferences.industryChange";
    public static final String DEFAULT_QUEUE_INVENTORY_REQUEST = "queue.inventory.request";
    public static final String DEFAULT_QUEUE_INVENTORY_RESPONSE = "queue.inventory.response";

    @Getter private static final String inventoryServiceListenToQueue = DEFAULT_QUEUE_INVENTORY_REQUEST;
    @Getter private static ActiveMQQueue inventoryServiceSendToQueue = new ActiveMQQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
    @Getter @Setter private static String orderServiceListenToQueue = DEFAULT_QUEUE_INVENTORY_RESPONSE;
    @Getter @Setter private static ActiveMQQueue orderServiceSendToQueue = new ActiveMQQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);

    @Value("${spring.activemq.broker-url}")
    private String embeddedBrokerUrl;

    @Value("${spring.activemq.embedded-broker-name}")
    private String embeddedBrokerName;

    @Value("${spring.activemq.ws-transport-connector-url}")
    private String wsUrl;

    @Value("${spring.activemq.stomp-transport-connector-url}")
    private String stompUrl;

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
    private String initialContextClass;

    @Value("ConnectionFactory")
    private String connectionFactory;

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("qualify_class_name");
        return converter;
    }

    @Bean
    @Qualifier("jmsQueueListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer,
                                                                               ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConnectionFactory(connectionFactory);
        factory.setErrorHandler((Throwable throwable) -> log.error("JMS error", throwable));
        factory.setPubSubDomain(false);
        return factory;
    }

    // Create an embedded ActiveMQ Broker
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(name = "spring.activemq.in-memory", havingValue = "true")
    public BrokerService brokerService() throws Exception{
        final BrokerService broker = new BrokerService();
        broker.setBrokerName(embeddedBrokerName);
        broker.setPersistent(false);

        // Set default broker url(tcp://xxx)
        TransportConnector tcpConnector = new TransportConnector();
        tcpConnector.setUri(new URI(embeddedBrokerUrl));
        broker.addConnector(tcpConnector);

        // Support STOMP, url(stomp://xxx)
        TransportConnector stompConnector = new TransportConnector();
        stompConnector.setUri(new URI(wsUrl));
        broker.addConnector(stompConnector);

        // Support WebSocket, url(ws://xxx)
        TransportConnector wsConnector = new TransportConnector();
        wsConnector.setUri(new URI(stompUrl));
        broker.addConnector(wsConnector);

        return broker;
    }
}
