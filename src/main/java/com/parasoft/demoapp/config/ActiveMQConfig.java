package com.parasoft.demoapp.config;

import java.net.URI;

import lombok.Getter;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
@Getter
public class ActiveMQConfig {

    public static final String TOPIC_ORDER_APPROVER = "order.approver";
    public static final String TOPIC_ORDER_PURCHASER = "order.purchaser";
    public static final String TOPIC_INDUSTRY_CHANGE = "globalPreferences.industryChange";
    
    @Value("${spring.activemq.broker-url}")
    private String embeddedBrokerUrl;
    
    @Value("${spring.activemq.embedded-broker-name}")
    private String embeddedBrokerName;
    
    @Value("${spring.activemq.ws-transport-connector-url}")
    private String wsUrl;
    
    @Value("${spring.activemq.stomp-transport-connector-url}")
    private String stompUrl;

    @Bean
    public ActiveMQTopic approverTopic() {
        return new ActiveMQTopic(TOPIC_ORDER_APPROVER);
    }

    @Bean
    public ActiveMQTopic purchaserTopic() {
        return new ActiveMQTopic(TOPIC_ORDER_PURCHASER);
    }

    @Bean
    public ActiveMQTopic industryChangeTopic() {
        return new ActiveMQTopic(TOPIC_INDUSTRY_CHANGE);
    }

    // Create a embedded ActiveMQ Broker
    @Bean(initMethod = "start", destroyMethod = "stop")
    public BrokerService brokerService() throws Exception{
    	final BrokerService broker = new BrokerService();
    	broker.setBrokerName(embeddedBrokerName);
	    broker.setPersistent(false);
	    // Add Topic destinations
	    broker.setDestinations(new ActiveMQDestination[]{approverTopic(), purchaserTopic()});
	    
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
