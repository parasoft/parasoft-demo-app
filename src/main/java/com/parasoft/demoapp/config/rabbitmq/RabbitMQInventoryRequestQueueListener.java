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

import static org.springframework.amqp.core.Address.AMQ_RABBITMQ_REPLY_TO;
import static org.springframework.kafka.support.mapping.AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME;

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
        message.getMessageProperties().setHeader(DEFAULT_CLASSID_FIELD_NAME, InventoryOperationRequestMessageDTO.class.getName());
        Object object = rabbitMqMessageConverter.fromMessage(message);

        log.info("Inventory service receives a message from RabbitMQ queue: {} \n Message content: {}", message.getMessageProperties().getConsumerQueue(), object);

        InventoryOperationResultMessageDTO messageToReply = itemInventoryService.handleMessageFromRequest((InventoryOperationRequestMessageDTO)object);

        if(messageToReply == null) {
            log.info("Inventory service has no response message to reply.");
            return;
        }

        String replyTo = message.getMessageProperties().getReplyTo();
        if (replyTo != null) {
            itemInventoryMQService.sendToAmqRabbitMqReplyToQueue(messageToReply, replyTo);
        } else {
            itemInventoryMQService.sendToInventoryResponseDestination(messageToReply);
        }
    }
}
