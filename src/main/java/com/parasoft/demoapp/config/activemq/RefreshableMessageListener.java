package com.parasoft.demoapp.config.activemq;

import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;

public abstract class RefreshableMessageListener implements MessageListener {

    private static final String ID_PREFIX = "ID_PREFIX";

    protected final CachingConnectionFactory cachingConnectionFactory;

    protected final MessageConverter jmsMessageConverter;
    private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;
    private final DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory;

    private final Map<String, MessageListenerContainer> listenedListenerContainers = new HashMap<>();

    public RefreshableMessageListener(MessageConverter jmsMessageConverter,
                                      JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
                                      DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory,
                                      String destinationName, CachingConnectionFactory cachingConnectionFactory) {
        this.jmsMessageConverter = jmsMessageConverter;
        this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
        this.jmsQueueListenerContainerFactory = jmsQueueListenerContainerFactory;
        this.cachingConnectionFactory = cachingConnectionFactory;

        registerListener(destinationName);
    }

    private void registerListener(String destinationName) {
        SimpleJmsListenerEndpoint jmsListenerEndpoint = new SimpleJmsListenerEndpoint();
        jmsListenerEndpoint.setMessageListener(this);
        jmsListenerEndpoint.setDestination(destinationName);
        jmsListenerEndpoint.setId(ID_PREFIX + destinationName);
        jmsListenerEndpointRegistry.
                registerListenerContainer(jmsListenerEndpoint, jmsQueueListenerContainerFactory, true);
        listenedListenerContainers.put(destinationName, jmsListenerEndpointRegistry.getListenerContainer("ID_PREFIX" + destinationName));
    }

    public void refreshDestination(String destinationName) {
        for(MessageListenerContainer container : listenedListenerContainers.values()) {
            container.stop();
        }

        MessageListenerContainer wantedMessageListenerContainer = listenedListenerContainers.get(destinationName);
        if(wantedMessageListenerContainer == null) {
            registerListener(destinationName);
        } else {
            wantedMessageListenerContainer.start();
        }

        cachingConnectionFactory.resetConnection();
    }

    public abstract void onMessage(Message message);
}
