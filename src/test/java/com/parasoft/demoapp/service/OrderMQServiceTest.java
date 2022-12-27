package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.OrderMQMessageDTO;
import com.parasoft.demoapp.model.global.preferences.MqType;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import javax.jms.Destination;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

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

	@Mock
	KafkaTemplate<String, InventoryOperationRequestMessageDTO> operationRequestKafkaTemplate;

    @Mock
    RabbitTemplate rabbitTemplate;

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
     * Test for sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>)
     *
     * @see com.parasoft.demoapp.service.OrderMQService#sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>)
     */
    @Test
    public void testSendToInventoryRequestDestination_without_info_jms() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        MQConfig.currentMQType = MqType.ACTIVE_MQ;

        // When
        underTest.sendToInventoryRequestDestination(InventoryOperation.DECREASE, orderNumber, orderItems);


        // Then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(any(ActiveMQQueue.class), any(InventoryOperationRequestMessageDTO.class));
    }

    /**
     * Test for sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>)
     *
     * @see com.parasoft.demoapp.service.OrderMQService#sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>)
     */
    @Test
    public void testSendToInventoryRequestDestination_without_info_kafka() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        MQConfig.currentMQType = MqType.KAFKA;
        KafkaConfig.setOrderServiceSendToTopic("test.topic");
        ListenableFuture<SendResult<String, InventoryOperationRequestMessageDTO>> future = mock(ListenableFuture.class);
        doReturn(future).when(operationRequestKafkaTemplate).send(anyString(), anyString(), any(InventoryOperationRequestMessageDTO.class));

        // When
        underTest.sendToInventoryRequestDestination(InventoryOperation.DECREASE, orderNumber, orderItems);


        // Then
        Mockito.verify(operationRequestKafkaTemplate, times(1)).send(anyString(), anyString(), any(InventoryOperationRequestMessageDTO.class));
    }

    /**
     * Test for sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>, String)
     *
     * @see com.parasoft.demoapp.service.OrderMQService#sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>, String)
     */
    @Test
    public void testSendToInventoryRequestDestination_with_info_jms() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        MQConfig.currentMQType = MqType.ACTIVE_MQ;

        // When
        underTest.sendToInventoryRequestDestination(InventoryOperation.DECREASE, orderNumber, orderItems, "test");

        // Then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(any(ActiveMQQueue.class), any(InventoryOperationRequestMessageDTO.class));
    }

    /**
     * Test for sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>, String)
     *
     * @see com.parasoft.demoapp.service.OrderMQService#sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>, String)
     */
    @Test
    public void testSendToInventoryRequestDestination_with_info_kafka() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        MQConfig.currentMQType = MqType.KAFKA;
        KafkaConfig.setOrderServiceSendToTopic("test.topic");
        ListenableFuture<SendResult<String, InventoryOperationRequestMessageDTO>> future = mock(ListenableFuture.class);
        doReturn(future).when(operationRequestKafkaTemplate).send(anyString(), anyString(), any(InventoryOperationRequestMessageDTO.class));

        // When
        underTest.sendToInventoryRequestDestination(InventoryOperation.DECREASE, orderNumber, orderItems, "test");

        // Then
        Mockito.verify(operationRequestKafkaTemplate, times(1)).send(anyString(), anyString(), any(InventoryOperationRequestMessageDTO.class));
    }

    /**
     * Test for sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>, String)
     *
     * @see com.parasoft.demoapp.service.OrderMQService#sendToInventoryRequestDestination(InventoryOperation, String, List<OrderItemEntity>, String)
     */
    @Test
    public void testSendToInventoryRequestDestination_with_info_rabbit() {
        // Given
        String orderNumber = "11-234-567";
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        MQConfig.currentMQType = MqType.RABBIT_MQ;

        // When
        underTest.sendToInventoryRequestDestination(InventoryOperation.DECREASE, orderNumber, orderItems, "test");

        // Then
        Mockito.verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(InventoryOperationRequestMessageDTO.class));
    }

	/**
	 * send(Destination, InventoryOperationRequestMessageDTO)
	 *
	 * @see com.parasoft.demoapp.service.OrderMQService#send(Destination, InventoryOperationRequestMessageDTO)
	 */
	@Test
	public void testSend() throws Throwable {
		// Given
		String orderNumber = "123-456-789";
		OrderEntity order = new OrderEntity();
		order.setOrderNumber(orderNumber);
		order.setStatus(OrderStatus.CANCELED);
		order.setOrderItems(new ArrayList<>());
		doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

		// When
		String requestedBy = "purchaser";
		String information = "Order 23-456-001 is submitted";
		InventoryOperationRequestMessageDTO messageDto = new InventoryOperationRequestMessageDTO(
				InventoryOperation.DECREASE, requestedBy, InventoryInfoDTO.convertFrom(order.getOrderItems()), information);
		Destination destination = null;
		underTest.send(destination, messageDto);

		//Then
		Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(destination, messageDto);
	}
}