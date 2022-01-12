package com.parasoft.demoapp.service;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsMessagingTemplate;

import com.parasoft.demoapp.dto.OrderMQMessageDTO;
import com.parasoft.demoapp.model.industry.OrderStatus;

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
		OrderStatus status = OrderStatus.SUBMITTED;
		String information = "Order 23-456-001 is submitted";
		OrderMQMessageDTO messageDto = new OrderMQMessageDTO(orderNumber, status, information);
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
		OrderStatus status = OrderStatus.SUBMITTED;
		String information = "Order 23-456-001 is submitted";
		OrderMQMessageDTO messageDto = new OrderMQMessageDTO(orderNumber, status, information);
		underTest.sendToPurchaser(messageDto);
	}
}