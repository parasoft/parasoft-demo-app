package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.ItemInventoryMQService;
import com.parasoft.demoapp.service.ItemInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
@Component
public class InventoryRequestQueueListener extends RefreshableMessageListener {
    private final ItemInventoryService itemInventoryService;
    private final ItemInventoryMQService itemInventoryMQService;
    protected final CachingConnectionFactory cachingConnectionFactory;

    public InventoryRequestQueueListener(MessageConverter jmsMessageConverter,
                                         JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
                                         DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory,
                                         ItemInventoryService itemInventoryService,
                                         ItemInventoryMQService itemInventoryMQService,
                                         CachingConnectionFactory cachingConnectionFactory) {
        super(jmsMessageConverter, jmsListenerEndpointRegistry, jmsQueueListenerContainerFactory,
                ActiveMQConfig.getInventoryServiceListenToQueue(), cachingConnectionFactory);

        this.itemInventoryService = itemInventoryService;
        this.itemInventoryMQService = itemInventoryMQService;
        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    @Override
    public void onMessage(Message message) {
        try {
            Object object = jmsMessageConverter.fromMessage(message);

            try {
                log.info("Inventory service receives a message from {} \n Message content: {}", message.getJMSDestination(), object);
            } catch (JMSException e) {
                // do nothing
            }

            InventoryOperationResultMessageDTO messageToReply = itemInventoryService.
                    handleMessageFromRequestQueue((InventoryOperationRequestMessageDTO) object);
            if(messageToReply == null) {
                log.info("Inventory service has no response message to reply.");
                return;
            }

            boolean useDefaultJmsReplyToDestination = false;
            Destination replyToDestination = message.getJMSReplyTo();
            if(replyToDestination == null) {
                useDefaultJmsReplyToDestination = true;
                replyToDestination = ActiveMQConfig.getInventoryResponseActiveMqQueue();
            }
            itemInventoryMQService.send(replyToDestination, messageToReply);

            log.info("Inventory service replied a message to {}(default JMSReplyTo destination: {}) \n Message content: {}",
                    replyToDestination, useDefaultJmsReplyToDestination, messageToReply);
        } catch (MessageConversionException e) {
            log.error("Invalid message from inventory request queue:", e);
        } catch (JMSException e) {
            log.error("JMS Exception:", e);
        }
    }
}
