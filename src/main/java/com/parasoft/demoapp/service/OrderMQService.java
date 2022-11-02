package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.dto.*;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import java.util.List;

@Slf4j
@Component
public class OrderMQService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void sendToApprover(OrderMQMessageDTO messageDto) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.TOPIC_ORDER_APPROVER, messageDto);
    }

    public void sendToPurchaser(OrderMQMessageDTO messageDto) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.TOPIC_ORDER_PURCHASER, messageDto);
    }

    public void sendToInventoryRequestQueue(InventoryOperation operation, String orderNumber, List<OrderItemEntity> orderItems) {
        sendToInventoryRequestQueue(operation, orderNumber, orderItems, null);
    }

    public void sendToInventoryRequestQueue(InventoryOperation operation, String orderNumber, List<OrderItemEntity> orderItems, String info) {
        sendToInventoryRequestQueue(new InventoryOperationRequestMessageDTO(operation, orderNumber, InventoryInfoDTO.convertFrom(orderItems), info));
    }

    public void sendToInventoryRequestQueue(InventoryOperationRequestMessageDTO message) {
        ActiveMQQueue destination = ActiveMQConfig.getInventoryRequestActiveMqQueue();
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.getInventoryRequestActiveMqQueue(), message);

        log.info("Order service sent a message to {} \n Message content: {}", destination, message);
    }

    public void send(Destination destination, InventoryOperationRequestMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

}
