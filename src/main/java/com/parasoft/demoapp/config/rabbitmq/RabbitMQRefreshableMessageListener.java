package com.parasoft.demoapp.config.rabbitmq;


import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.RefreshableMessageListener;
import com.parasoft.demoapp.model.global.preferences.MqType;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;


public abstract class RabbitMQRefreshableMessageListener extends RefreshableMessageListener<MessageListenerContainer> implements MessageListener {

    private static final String ID_PREFIX = "id.";

    private final SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory;

    public RabbitMQRefreshableMessageListener(SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory,
                                              String destinationName) {
        this.simpleRabbitListenerContainerFactory = simpleRabbitListenerContainerFactory;

        if(MQConfig.currentMQType == MqType.RABBIT_MQ) {
            registerListener(destinationName);
        }
    }

    protected void registerListener(String destinationName) {
        SimpleRabbitListenerEndpoint rabbitListenerEndpoint = new SimpleRabbitListenerEndpoint();
        rabbitListenerEndpoint.setMessageListener(this);
        rabbitListenerEndpoint.setId(ID_PREFIX + destinationName);
        rabbitListenerEndpoint.setQueueNames(destinationName);
        SimpleMessageListenerContainer messageListenerContainer = simpleRabbitListenerContainerFactory.createListenerContainer(rabbitListenerEndpoint);
        messageListenerContainer.setConsumerStartTimeout(5000);
        messageListenerContainer.start();
        listenedListenerContainers.put(destinationName, messageListenerContainer);
    }

    @Override
    public void refreshDestination(String destinationName) {
        stopAllListenedDestinations();

       MessageListenerContainer targetMessageListenerContainer = listenedListenerContainers.get(destinationName);
        if(targetMessageListenerContainer == null) {
            registerListener(destinationName);
        } else {
            targetMessageListenerContainer.start();
        }
    }

    @Override
    public void stopAllListenedDestinations() {
        for(MessageListenerContainer container : listenedListenerContainers.values()) {
            container.stop();
        }
    }
}
