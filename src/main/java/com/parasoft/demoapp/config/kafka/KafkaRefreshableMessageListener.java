package com.parasoft.demoapp.config.kafka;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.RefreshableMessageListener;
import com.parasoft.demoapp.model.global.preferences.MqType;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

public abstract class KafkaRefreshableMessageListener<T> extends RefreshableMessageListener<MessageListenerContainer> implements MessageListener<String, T> {

    private final ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory;

    public KafkaRefreshableMessageListener(ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory,
                                            String topic) {
        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
        if(MQConfig.currentMQType == MqType.KAFKA) {
            registerListener(topic);
        }
    }

    private void registerListener(String topic) {
        ConcurrentMessageListenerContainer<String, T> container = kafkaListenerContainerFactory.createContainer(topic);
        container.getContainerProperties().setMessageListener(this);
        container.start();

        listenedListenerContainers.put(topic, container);
    }

    public void refreshDestination(String topic) {
        stopAllListenedDestinations();

        MessageListenerContainer targetMessageListenerContainer = listenedListenerContainers.get(topic);
        if(targetMessageListenerContainer == null) {
            registerListener(topic);
        } else {
            targetMessageListenerContainer.start();
        }
    }

    public void stopAllListenedDestinations() {
        for(MessageListenerContainer container : listenedListenerContainers.values()) {
            container.stop();
        }
    }
}
