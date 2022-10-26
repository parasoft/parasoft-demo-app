package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.ItemInventoryMQService;
import com.parasoft.demoapp.service.OrderService;
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
public class InventoryResponseQueueListener extends RefreshableMessageListener {
    private final OrderService orderService;
    private final ItemInventoryMQService itemInventoryMQService;

    public InventoryResponseQueueListener(MessageConverter jmsMessageConverter,
                                          JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
                                          DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory,
                                          OrderService orderService,
                                          ItemInventoryMQService itemInventoryMQService) {
        super(jmsMessageConverter, jmsListenerEndpointRegistry, jmsQueueListenerContainerFactory,
                ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);

        this.orderService = orderService;
        this.itemInventoryMQService = itemInventoryMQService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            Object object = jmsMessageConverter.fromMessage(message);
            InventoryOperationRequestMessageDTO messageToReply =
                    orderService.handleResponseQueue((InventoryOperationResultMessageDTO) object);
            if(messageToReply == null) {
                return;
            }

            Destination replyToDestination = message.getJMSReplyTo();
            if(replyToDestination != null) {
                itemInventoryMQService.send(replyToDestination, messageToReply);
            } else {
                itemInventoryMQService.sendToRequestQueue(messageToReply);
            }
        } catch (MessageConversionException e) {
            log.error("Invalid message from inventory response queue:", e);
        } catch (JMSException e) {
            log.error("JMS Exception:", e);
        }

    }
}