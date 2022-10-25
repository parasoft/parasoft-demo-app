package com.parasoft.demoapp.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.parasoft.demoapp.dto.*;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Destination;
import java.util.ArrayList;
import java.util.List;

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
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#sendToResponseQueue(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testSendToResponseQueue() throws Throwable {
        // Given
        doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

        // When
        String requestedBy = "purchaser";
        String information = "Order 23-456-001 is submitted";
        InventoryOperationResultMessageDTO messageDto = new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, requestedBy, InventoryOperationStatus.SUCCESS, information);
        underTest.sendToResponseQueue(messageDto);
    }

    /**
     * sendToRequestQueue(InventoryOperationRequestMessageDTO)
     *
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#sendToRequestQueue(InventoryOperationRequestMessageDTO)
     */
    @Test
    public void testSendToRequestQueue_with_message() throws Throwable {
        // Given
        doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

        // When
        String requestedBy = "purchaser";
        List<InventoryInfoDTO> inventoryInfo = new ArrayList<>();
        String information = "Order 23-456-001 is submitted";
        InventoryOperationRequestMessageDTO messageDto = new InventoryOperationRequestMessageDTO(InventoryOperation.DECREASE, requestedBy, inventoryInfo, information);
        underTest.sendToRequestQueue(messageDto);
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
    }

    /**
     * Test for sendToInventoryRequestQueue(InventoryOperation, String, List<OrderItemEntity>)
     *
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#sendToRequestQueue(InventoryOperation, String, List<OrderItemEntity>)
     */
    @Test
    public void testSendToRequestQueue_without_info() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        // When
        underTest.sendToRequestQueue(InventoryOperation.DECREASE, orderNumber, orderItems);


        // Then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(any(ActiveMQQueue.class), any(InventoryOperationRequestMessageDTO.class));
    }

    /**
     * Test for sendToInventoryRequestQueue(InventoryOperation, String, List<OrderItemEntity>, String)
     *
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#sendToRequestQueue(InventoryOperation, String, List<OrderItemEntity>, String)
     */
    @Test
    public void testSendToRequestQueue_with_info() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        // When
        underTest.sendToRequestQueue(InventoryOperation.DECREASE, orderNumber, orderItems, "test");

        // Then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(any(ActiveMQQueue.class), any(InventoryOperationRequestMessageDTO.class));
    }
}