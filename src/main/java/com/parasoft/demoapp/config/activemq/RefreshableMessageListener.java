package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.model.global.preferences.MqType;
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

    private static final String ID_PREFIX = "id.";

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

        if(MQConfig.CURRENT_MQ_TYPE == MqType.ACTIVE_MQ) {
            registerListener(destinationName);
        }
    }

    private void registerListener(String destinationName) {
        SimpleJmsListenerEndpoint jmsListenerEndpoint = new SimpleJmsListenerEndpoint();
        jmsListenerEndpoint.setMessageListener(this);
        jmsListenerEndpoint.setDestination(destinationName);
        jmsListenerEndpoint.setId(ID_PREFIX + destinationName);
        jmsListenerEndpointRegistry.
                registerListenerContainer(jmsListenerEndpoint, jmsQueueListenerContainerFactory, true);
        listenedListenerContainers.put(destinationName, jmsListenerEndpointRegistry.getListenerContainer(ID_PREFIX + destinationName));
    }

    public void refreshDestination(String destinationName) {
        stopAllListenedListenerContainers();

        MessageListenerContainer targetMessageListenerContainer = listenedListenerContainers.get(destinationName);
        if(targetMessageListenerContainer == null) {
            registerListener(destinationName);
        } else {
            targetMessageListenerContainer.start();
        }

        cachingConnectionFactory.resetConnection();
    }

    public abstract void onMessage(Message message);

    public void stopAllListenedListenerContainers() {
        for(MessageListenerContainer container : listenedListenerContainers.values()) {
            System.out.println(container);
            container.stop();
        }
    }
}
