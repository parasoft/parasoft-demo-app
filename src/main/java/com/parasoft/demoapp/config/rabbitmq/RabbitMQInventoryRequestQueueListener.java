package com.parasoft.demoapp.config.rabbitmq;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.ItemInventoryMQService;
import com.parasoft.demoapp.service.ItemInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQInventoryRequestQueueListener extends RabbitMQRefreshableMessageListener {

    private final MessageConverter rabbitMqMessageConverter;
    private final ItemInventoryService itemInventoryService;
    private final ItemInventoryMQService itemInventoryMQService;

    public RabbitMQInventoryRequestQueueListener(MessageConverter rabbitMqMessageConverter,
                                                 ItemInventoryService itemInventoryService,
                                                 ItemInventoryMQService itemInventoryMQService,
                                                 SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory) {
        super(simpleRabbitListenerContainerFactory,
                RabbitMQConfig.DEFAULT_ORDER_SERVICE_REQUEST_QUEUE);

        this.rabbitMqMessageConverter = rabbitMqMessageConverter;
        this.itemInventoryService = itemInventoryService;
        this.itemInventoryMQService = itemInventoryMQService;
    }

    @Override
    public void onMessage(Message message) {
        Object object = rabbitMqMessageConverter.fromMessage(message);

        log.info("Inventory service receives a message from RabbitMQ queue: {} \n Message content: {}", message.getMessageProperties().getConsumerQueue(), object);

        InventoryOperationResultMessageDTO messageToReply = itemInventoryService.handleMessageFromRequest((InventoryOperationRequestMessageDTO)object);

        if(messageToReply == null) {
            log.info("Inventory service has no response message to reply.");
            return;
        }

        // TODO: reply message to response queue

    }
}
