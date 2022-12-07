package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

import static com.parasoft.demoapp.model.global.preferences.MqType.ACTIVE_MQ;
import static com.parasoft.demoapp.model.global.preferences.MqType.KAFKA;

@Slf4j
@Service
public class ItemInventoryMQService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private KafkaTemplate<String, InventoryOperationResultMessageDTO> operationResultKafkaTemplate;

    public void sendToInventoryResponseDestination(InventoryOperationResultMessageDTO message) {
        String responseDestination = null;
        if (MQConfig.currentMQType == ACTIVE_MQ) {
            ActiveMQQueue destination = ActiveMQConfig.getInventoryServiceSendToQueue();
            responseDestination = "ActiveMQ: " + destination.toString();
            jmsMessagingTemplate.convertAndSend(destination, message);
        } else if (MQConfig.currentMQType == KAFKA) {
            String destination = KafkaConfig.getOrderServiceListenToTopic();
            responseDestination = "Kafka topic: " + destination;
            operationResultKafkaTemplate.send(destination, message.getOrderNumber(), message);
        }
        log.info("Inventory service sent a message to {} \n Message content: {}", responseDestination, message);
    }

    public void send(Destination destination, InventoryOperationResultMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }
}
