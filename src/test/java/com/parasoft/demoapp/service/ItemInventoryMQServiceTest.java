package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationStatus;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import javax.jms.Destination;

import static com.parasoft.demoapp.model.global.preferences.MqType.ACTIVE_MQ;
import static com.parasoft.demoapp.model.global.preferences.MqType.KAFKA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

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

    @Mock
    KafkaTemplate operationResultKafkaTemplate;

    @Mock
    GlobalPreferencesService globalPreferencesService;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for sendToInventoryResponseDestination(InventoryOperationResultMessageDTO)
     *
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#sendToInventoryResponseDestination(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testSendToInventoryResponseDestination_jms() throws Throwable {
        // Given
        MQConfig.currentMQType = ACTIVE_MQ;
        doNothing().when(jmsMessagingTemplate).convertAndSend(nullable(String.class), nullable(Object.class));

        // When
        String requestedBy = "purchaser";
        String information = "Order 23-456-001 is submitted";
        InventoryOperationResultMessageDTO messageDto = new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, requestedBy, InventoryOperationStatus.SUCCESS, information);
        underTest.sendToInventoryResponseDestination(messageDto);

        //then
        Mockito.verify(jmsMessagingTemplate, times(1)).convertAndSend(any(ActiveMQQueue.class), any(InventoryOperationResultMessageDTO.class));
    }

    /**
     * Test for sendToInventoryResponseDestination(InventoryOperationResultMessageDTO)
     *
     * @see com.parasoft.demoapp.service.ItemInventoryMQService#sendToInventoryResponseDestination(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testSendToInventoryResponseDestination_kafka() throws Throwable {
        // Given
        MQConfig.currentMQType = KAFKA;

        // When
        String information = "Order 23-456-001 is submitted";
        String orderNumber = "23-456-002";
        InventoryOperationResultMessageDTO messageDto = new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, orderNumber, InventoryOperationStatus.SUCCESS, information);
        GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
        globalPreferences.setOrderServiceResponseTopic("response.topic");
        doReturn(globalPreferences).when(globalPreferencesService).getCurrentGlobalPreferences();
        underTest.sendToInventoryResponseDestination(messageDto);

        //then
        Mockito.verify(operationResultKafkaTemplate, times(1)).send(any(String.class), any(String.class), any(InventoryOperationResultMessageDTO.class));
    }

    /**
     * send(Destination, InventoryOperationResultMessageDTO)
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