package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.List;

@Service
public class ItemInventoryMQService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void sendToRequestQueue(InventoryOperation operation, String orderNUmber, List<OrderItemEntity> orderItems) {
        sendToRequestQueue(operation, orderNUmber, orderItems, null);
    }

    public void sendToRequestQueue(InventoryOperation operation, String orderNumber, List<OrderItemEntity> orderItems, String info) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.inventoryRequestActiveMqQueue,
                new InventoryOperationRequestMessageDTO(operation, orderNumber,
                        InventoryInfoDTO.convertFrom(orderItems), info));
    }

    public void sendToRequestQueue(InventoryOperationRequestMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.inventoryRequestActiveMqQueue, message);
    }

    public void sendToResponseQueue(InventoryOperationResultMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.inventoryResponseActiveMqQueue, message);
    }

    public void send(Destination destination, InventoryOperationResultMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }
}
