package com.parasoft.demoapp.service;

import com.google.gson.Gson;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.OrderMQMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMQService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    private Gson gson = new Gson();

    public void sendToApprover(OrderMQMessageDTO messageDto) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.TOPIC_ORDER_APPROVER, gson.toJson(messageDto));
    }

    public void sendToPurchaser(OrderMQMessageDTO messageDto) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.TOPIC_ORDER_PURCHASER, gson.toJson(messageDto));
    }

    public void sendToInventoryRequestQueue(InventoryOperation operation, String orderNumber, List<OrderItemEntity> orderItems) {
        sendToInventoryRequestQueue(operation, orderNumber, orderItems, null);
    }

    public void sendToInventoryRequestQueue(InventoryOperation operation, String orderNumber, List<OrderItemEntity> orderItems, String info) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.inventoryRequestActiveMqQueue,
                new InventoryOperationRequestMessageDTO(operation, orderNumber,
                        InventoryInfoDTO.convertFrom(orderItems), info));
    }
    
}
