package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.ItemInventoryMQService;
import com.parasoft.demoapp.service.ItemInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
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

    public InventoryRequestQueueListener(MessageConverter jmsMessageConverter,
                                         JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
                                         DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory,
                                         ItemInventoryService itemInventoryService,
                                         ItemInventoryMQService itemInventoryMQService) {
        super(jmsMessageConverter, jmsListenerEndpointRegistry, jmsQueueListenerContainerFactory,
                ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);

        this.itemInventoryService = itemInventoryService;
        this.itemInventoryMQService = itemInventoryMQService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            Object object = jmsMessageConverter.fromMessage(message);
            InventoryOperationResultMessageDTO messageToReply = itemInventoryService.
                    handleMessageFromRequestQueue((InventoryOperationRequestMessageDTO) object);
            if(messageToReply == null) {
                return;
            }

            Destination replyToDestination = message.getJMSReplyTo();
            if(replyToDestination != null) {
                itemInventoryMQService.send(replyToDestination, messageToReply);
            } else {
                itemInventoryMQService.sendToResponseQueue(messageToReply);
            }
        } catch (MessageConversionException e) {
            log.error("Invalid message from inventory request queue:", e);
        } catch (JMSException e) {
            log.error("JMS Exception:", e);
        }
    }
}
