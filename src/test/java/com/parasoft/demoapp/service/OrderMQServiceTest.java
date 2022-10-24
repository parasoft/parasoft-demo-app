package com.parasoft.demoapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

import com.parasoft.demoapp.config.activemq.ActiveMQMessage.InventoryOperationRequestMessage;
import com.parasoft.demoapp.config.activemq.ActiveMQMessage.InventoryOperation;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsMessagingTemplate;

import com.parasoft.demoapp.dto.OrderMQMessageDTO;
import com.parasoft.demoapp.model.industry.OrderStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for OrderMQService
 *
 * @see com.parasoft.demoapp.service.OrderMQService
 */
public class OrderMQServiceTest {

	// Object under test
	@InjectMocks
	OrderMQService underTest;

	@Mock
	JmsMessagingTemplate jmsMessagingTemplate;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for sendToApprover(OrderMQMessageDTO)
	 *
	 * @see com.parasoft.demoapp.service.OrderMQService#sendToApprover(OrderMQMessageDTO)
	 */
	@Test
	public void testSendToApprover() throws Throwable {
		// Given
		doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

		// When
		String orderNumber = "23-456-001";
		String requestedBy = "purchaser";
		OrderStatus status = OrderStatus.SUBMITTED;
		String information = "Order 23-456-001 is submitted";
		OrderMQMessageDTO messageDto = new OrderMQMessageDTO(orderNumber, requestedBy, status, information);
		underTest.sendToApprover(messageDto);
	}

	/**
	 * Test for sendToPurchaser(OrderMQMessageDTO)
	 *
	 * @see com.parasoft.demoapp.service.OrderMQService#sendToPurchaser(OrderMQMessageDTO)
	 */
	@Test
	public void testSendToPurchaser() throws Throwable {
		// Given
		doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

		// When
		String orderNumber = "23-456-001";
		String requestedBy = "purchaser";
		OrderStatus status = OrderStatus.SUBMITTED;
		String information = "Order 23-456-001 is submitted";
		OrderMQMessageDTO messageDto = new OrderMQMessageDTO(orderNumber, requestedBy, status, information);
		underTest.sendToPurchaser(messageDto);
	}

    /**
     * Test for sendToInventoryRequestQueue(InventoryOperation, String, List<OrderItemEntity>, String)
     *
     * @see com.parasoft.demoapp.service.OrderMQService#sendToInventoryRequestQueue(InventoryOperation, String, List<OrderItemEntity>, String)
     */
    @Test
    public void testSendToInventoryRequestQueue() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        // When
        underTest.sendToInventoryRequestQueue(InventoryOperation.DECREASE_FOR_ORDER_CREATION, orderNumber, orderItems, null);

        // Then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(any(ActiveMQQueue.class), any(InventoryOperationRequestMessage.class));
    }
}