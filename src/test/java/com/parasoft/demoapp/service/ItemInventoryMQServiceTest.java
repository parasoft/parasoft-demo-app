package com.parasoft.demoapp.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.parasoft.demoapp.dto.*;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Destination;

/**
 * Test class for ItemInventoryMQService
 *
 * @see com.parasoft.demoapp.service.ItemInventoryMQService
 */
public class ItemInventoryMQServiceTest {

    // Object under test
    @InjectMocks
    ItemInventoryMQService underTest;

    @Mock
    JmsMessagingTemplate jmsMessagingTemplate;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for sendToResponseQueue(InventoryOperationResultMessageDTO)
     *
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#sendToInventoryResponseQueue(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testSendToInventoryResponseQueue() throws Throwable {
        // Given
        doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

        // When
        String requestedBy = "purchaser";
        String information = "Order 23-456-001 is submitted";
        InventoryOperationResultMessageDTO messageDto = new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, requestedBy, InventoryOperationStatus.SUCCESS, information);
        underTest.sendToInventoryResponseQueue(messageDto);

        //then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(any(ActiveMQQueue.class), any(InventoryOperationResultMessageDTO.class));
    }

    /**
     * sendToRequestQueue(InventoryOperationRequestMessageDTO)
     *
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#send(Destination, InventoryOperationResultMessageDTO)
     */
    @Test
    public void testSend() throws Throwable {
        // Given
        doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

        // When
        String requestedBy = "purchaser";
        String information = "Order 23-456-001 is submitted";
        InventoryOperationResultMessageDTO messageDto = new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, requestedBy, InventoryOperationStatus.SUCCESS, information);
        Destination destination = null;
        underTest.send(destination, messageDto);

        //Then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(destination, messageDto);
    }
}