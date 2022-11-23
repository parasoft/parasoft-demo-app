package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

@Slf4j
@Service
public class ItemInventoryMQService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void sendToInventoryResponseQueue(InventoryOperationResultMessageDTO message) {
        ActiveMQQueue destination = ActiveMQConfig.getInventoryServiceSendToQueue();
        jmsMessagingTemplate.convertAndSend(destination, message);

        log.info("Inventory service sent a message to {} \n Message content: {}", destination, message);
    }

    public void send(Destination destination, InventoryOperationResultMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }
}
