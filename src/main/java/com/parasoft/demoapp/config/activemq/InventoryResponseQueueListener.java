package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.OrderMQService;
import com.parasoft.demoapp.service.OrderService;
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
public class InventoryResponseQueueListener extends ActiveMQRefreshableMessageListener {
    private final OrderService orderService;
    private final OrderMQService orderMQService;

    public InventoryResponseQueueListener(MessageConverter jmsMessageConverter,
                                          JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
                                          DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory,
                                          OrderService orderService,
                                          OrderMQService orderMQService,
                                          CachingConnectionFactory cachingConnectionFactory) {
        super(jmsMessageConverter, jmsListenerEndpointRegistry, jmsQueueListenerContainerFactory,
                ActiveMQConfig.getOrderServiceListenToQueue(), cachingConnectionFactory);

        this.orderService = orderService;
        this.orderMQService = orderMQService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            Object object = jmsMessageConverter.fromMessage(message);

            try {
                log.info("Order service receives a message from {} \n Message content: {}", message.getJMSDestination(), object);
            } catch (JMSException e) {
                // do nothing
            }

            InventoryOperationRequestMessageDTO messageToReply =
                    orderService.handleMessageFromResponseQueue((InventoryOperationResultMessageDTO) object);
            if(messageToReply == null) {
                log.info("Order service has no response message to reply.");
                return;
            }

            boolean useDefaultJmsReplyToDestination = false;
            Destination replyToDestination = message.getJMSReplyTo();
            if(replyToDestination == null) {
                useDefaultJmsReplyToDestination = true;
                replyToDestination = ActiveMQConfig.getOrderServiceSendToQueue();
            }
            orderMQService.send(replyToDestination, messageToReply);

            log.info("Order service replied a message to {}(default JMSReplyTo destination: {}) \n Message content: {}",
                    replyToDestination, useDefaultJmsReplyToDestination, messageToReply);
        } catch (MessageConversionException e) {
            log.error("Invalid message from inventory response queue:", e);
        } catch (JMSException e) {
            log.error("JMS Exception:", e);
        }
    }
}
