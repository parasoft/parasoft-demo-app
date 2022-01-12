package com.parasoft.demoapp.service;

import com.google.gson.Gson;
import com.parasoft.demoapp.config.ActiveMQConfig;
import com.parasoft.demoapp.dto.OrderMQMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

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

}
