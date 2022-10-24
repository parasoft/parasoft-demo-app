package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.dto.OrderMQMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

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

}
