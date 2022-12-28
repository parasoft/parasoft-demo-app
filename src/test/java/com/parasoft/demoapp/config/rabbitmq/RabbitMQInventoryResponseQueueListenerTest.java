package com.parasoft.demoapp.config.rabbitmq;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.OrderMQService;
import com.parasoft.demoapp.service.OrderService;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class RabbitMQInventoryResponseQueueListenerTest {

    @Test
    public void testOnMessage_MessageToReply_NotNull() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        OrderService orderService = mock(OrderService.class);
        OrderMQService orderMQService = mock(OrderMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        RabbitMQInventoryResponseQueueListener rabbitMQInventoryResponseQueueListener = new RabbitMQInventoryResponseQueueListener(messageConverter, orderService, orderMQService, simpleRabbitListenerContainerFactory);

        Message message = mock(Message.class);
        MessageProperties messageProperties = mock(MessageProperties.class);
        doReturn(messageProperties).when(message).getMessageProperties();
        doReturn("test.consumer.queue").when(messageProperties).getConsumerQueue();
        InventoryOperationResultMessageDTO inventoryOperationResultMessageDTO = mock(InventoryOperationResultMessageDTO.class);
        InventoryOperationRequestMessageDTO inventoryOperationRequestMessageDTO = mock(InventoryOperationRequestMessageDTO.class);
        doReturn(inventoryOperationResultMessageDTO).when(messageConverter).fromMessage(any(Message.class));
        doReturn(inventoryOperationRequestMessageDTO).when(orderService).handleMessageFromResponse(any(InventoryOperationResultMessageDTO.class));

        // When
        rabbitMQInventoryResponseQueueListener.onMessage(message);
        // Then
        verify(orderMQService, times(1)).sendToInventoryRequestDestination(inventoryOperationRequestMessageDTO);
    }

    @Test
    public void testOnMessage_MessageToReply_Null() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        OrderService orderService = mock(OrderService.class);
        OrderMQService orderMQService = mock(OrderMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        RabbitMQInventoryResponseQueueListener rabbitMQInventoryResponseQueueListener = new RabbitMQInventoryResponseQueueListener(messageConverter, orderService, orderMQService, simpleRabbitListenerContainerFactory);

        Message message = mock(Message.class);
        MessageProperties messageProperties = mock(MessageProperties.class);
        doReturn(messageProperties).when(message).getMessageProperties();
        doReturn("test.consumer.queue").when(messageProperties).getConsumerQueue();
        InventoryOperationResultMessageDTO inventoryOperationResultMessageDTO = mock(InventoryOperationResultMessageDTO.class);
        doReturn(inventoryOperationResultMessageDTO).when(messageConverter).fromMessage(any(Message.class));
        doReturn(null).when(orderService).handleMessageFromResponse(any(InventoryOperationResultMessageDTO.class));

        // When
        rabbitMQInventoryResponseQueueListener.onMessage(message);
        // Then
        verify(orderMQService, times(0)).sendToInventoryRequestDestination(any(InventoryOperationRequestMessageDTO.class));
    }

    @Test
    public void testRegisterListener() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        OrderService orderService = mock(OrderService.class);
        OrderMQService orderMQService = mock(OrderMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        SimpleMessageListenerContainer simpleMessageListenerContainer = mock(SimpleMessageListenerContainer.class);
        doReturn(simpleMessageListenerContainer).when(simpleRabbitListenerContainerFactory).createListenerContainer(any(SimpleRabbitListenerEndpoint.class));
        RabbitMQInventoryResponseQueueListener rabbitMQInventoryResponseQueueListener = new RabbitMQInventoryResponseQueueListener(messageConverter, orderService, orderMQService, simpleRabbitListenerContainerFactory);
        // When
        rabbitMQInventoryResponseQueueListener.registerListener("test");
        // Then
        verify(simpleMessageListenerContainer, times(1)).setConsumerStartTimeout(5000);
        verify(simpleMessageListenerContainer, times(1)).start();
    }

    @Test
    public void testStopAllListenedDestinations() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        OrderService orderService = mock(OrderService.class);
        OrderMQService orderMQService = mock(OrderMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        SimpleMessageListenerContainer simpleMessageListenerContainer = mock(SimpleMessageListenerContainer.class);
        doReturn(simpleMessageListenerContainer).when(simpleRabbitListenerContainerFactory).createListenerContainer(any(SimpleRabbitListenerEndpoint.class));
        RabbitMQInventoryResponseQueueListener rabbitMQInventoryResponseQueueListener = new RabbitMQInventoryResponseQueueListener(messageConverter, orderService, orderMQService, simpleRabbitListenerContainerFactory);
        rabbitMQInventoryResponseQueueListener.registerListener("test");
        // When
        rabbitMQInventoryResponseQueueListener.stopAllListenedDestinations();
        // Then
        verify(simpleMessageListenerContainer, times(1)).stop();
    }

    @Test
    public void testRefreshDestination_UnchangedDestination() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        OrderService orderService = mock(OrderService.class);
        OrderMQService orderMQService = mock(OrderMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        SimpleMessageListenerContainer simpleMessageListenerContainer = mock(SimpleMessageListenerContainer.class);
        doReturn(simpleMessageListenerContainer).when(simpleRabbitListenerContainerFactory).createListenerContainer(any(SimpleRabbitListenerEndpoint.class));
        RabbitMQInventoryResponseQueueListener rabbitMQInventoryResponseQueueListener = new RabbitMQInventoryResponseQueueListener(messageConverter, orderService, orderMQService, simpleRabbitListenerContainerFactory);
        // When
        rabbitMQInventoryResponseQueueListener.registerListener("test");
        verify(simpleMessageListenerContainer, times(1)).start();
        verify(simpleMessageListenerContainer, times(1)).setConsumerStartTimeout(5000);
        rabbitMQInventoryResponseQueueListener.refreshDestination("test");
        // Then
        verify(simpleMessageListenerContainer, times(1)).stop();
        verify(simpleMessageListenerContainer, times(2)).start();
        verify(simpleMessageListenerContainer, times(1)).setConsumerStartTimeout(5000);
    }

    @Test
    public void testRefreshDestination_ChangedDestination() {
        // Given
        MessageConverter messageConverter = mock(MessageConverter.class);
        OrderService orderService = mock(OrderService.class);
        OrderMQService orderMQService = mock(OrderMQService.class);
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
        SimpleMessageListenerContainer simpleMessageListenerContainer = mock(SimpleMessageListenerContainer.class);
        doReturn(simpleMessageListenerContainer).when(simpleRabbitListenerContainerFactory).createListenerContainer(any(SimpleRabbitListenerEndpoint.class));
        RabbitMQInventoryResponseQueueListener rabbitMQInventoryResponseQueueListener = new RabbitMQInventoryResponseQueueListener(messageConverter, orderService, orderMQService, simpleRabbitListenerContainerFactory);
        // When
        rabbitMQInventoryResponseQueueListener.registerListener("test");
        verify(simpleMessageListenerContainer, times(1)).start();
        verify(simpleMessageListenerContainer, times(1)).setConsumerStartTimeout(5000);
        rabbitMQInventoryResponseQueueListener.refreshDestination("other");
        // Then
        verify(simpleMessageListenerContainer, times(1)).stop();
        verify(simpleMessageListenerContainer, times(2)).start();
        verify(simpleMessageListenerContainer, times(2)).setConsumerStartTimeout(5000);
    }
}
