package com.parasoft.demoapp.config.rabbitmq;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.ItemInventoryMQService;
import com.parasoft.demoapp.service.ItemInventoryService;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RabbitMQInventoryRequestQueueListenerTest {

    @Test
    public void testOnMessage_MessageToReply_NotNull() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        ItemInventoryService inventoryService = mock(ItemInventoryService.class);
        ItemInventoryMQService itemInventoryMQService = mock(ItemInventoryMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        RabbitMQInventoryRequestQueueListener rabbitMQInventoryRequestQueueListener = new RabbitMQInventoryRequestQueueListener(messageConverter, inventoryService, itemInventoryMQService, simpleRabbitListenerContainerFactory);

        Message message = mock(Message.class);
        MessageProperties messageProperties = mock(MessageProperties.class);
        doReturn(messageProperties).when(message).getMessageProperties();
        doReturn("test.consumer.queue").when(messageProperties).getConsumerQueue();
        InventoryOperationRequestMessageDTO inventoryOperationRequestMessageDTO = mock(InventoryOperationRequestMessageDTO.class);
        InventoryOperationResultMessageDTO inventoryOperationResultMessageDTO = mock(InventoryOperationResultMessageDTO.class);
        doReturn(inventoryOperationRequestMessageDTO).when(messageConverter).fromMessage(any(Message.class));
        doReturn(inventoryOperationResultMessageDTO).when(inventoryService).handleMessageFromRequest(any(InventoryOperationRequestMessageDTO.class));

        // When
        rabbitMQInventoryRequestQueueListener.onMessage(message);
        // Then
        verify(itemInventoryMQService, times(1)).sendToInventoryResponseDestination(inventoryOperationResultMessageDTO);
    }

    @Test
    public void testOnMessage_MessageToReply_Null() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        ItemInventoryService inventoryService = mock(ItemInventoryService.class);
        ItemInventoryMQService itemInventoryMQService = mock(ItemInventoryMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        RabbitMQInventoryRequestQueueListener rabbitMQInventoryRequestQueueListener = new RabbitMQInventoryRequestQueueListener(messageConverter, inventoryService, itemInventoryMQService, simpleRabbitListenerContainerFactory);

        Message message = mock(Message.class);
        MessageProperties messageProperties = mock(MessageProperties.class);
        doReturn(messageProperties).when(message).getMessageProperties();
        doReturn("test.consumer.queue").when(messageProperties).getConsumerQueue();
        InventoryOperationRequestMessageDTO inventoryOperationRequestMessageDTO = mock(InventoryOperationRequestMessageDTO.class);
        doReturn(inventoryOperationRequestMessageDTO).when(messageConverter).fromMessage(any(Message.class));
        doReturn(null).when(inventoryService).handleMessageFromRequest(any(InventoryOperationRequestMessageDTO.class));

        // When
        rabbitMQInventoryRequestQueueListener.onMessage(message);
        // Then
        verify(itemInventoryMQService, times(0)).sendToInventoryResponseDestination(any(InventoryOperationResultMessageDTO.class));
    }
}
