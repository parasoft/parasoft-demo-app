package com.parasoft.demoapp.config.activemq;

import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.Message;
import javax.jms.MessageListener;

public abstract class RefreshableMessageListener implements MessageListener {

    private static final String ID_PREFIX = "id:";

    protected final MessageConverter jmsMessageConverter;
    private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;
    private final DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory;

    public RefreshableMessageListener(MessageConverter jmsMessageConverter,
                                      JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
                                      DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory,
                                      String destinationName) {
        this.jmsMessageConverter = jmsMessageConverter;
        this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
        this.jmsQueueListenerContainerFactory = jmsQueueListenerContainerFactory;

        registerListener(destinationName);
    }

    private void registerListener(String destinationName) {
        SimpleJmsListenerEndpoint jmsListenerEndpoint = new SimpleJmsListenerEndpoint();
        jmsListenerEndpoint.setMessageListener(this);
        jmsListenerEndpoint.setDestination(destinationName);
        jmsListenerEndpoint.setId(ID_PREFIX + destinationName);
        jmsListenerEndpointRegistry.
                registerListenerContainer(jmsListenerEndpoint, jmsQueueListenerContainerFactory, true);
    }

    public abstract void onMessage(Message message);
}
