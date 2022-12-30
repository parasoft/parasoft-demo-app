package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQConfig;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.model.global.preferences.MqType;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

import static com.parasoft.demoapp.model.global.preferences.MqType.ACTIVE_MQ;
import static com.parasoft.demoapp.model.global.preferences.MqType.KAFKA;
import static org.springframework.amqp.core.Address.AMQ_RABBITMQ_REPLY_TO;

@Slf4j
@Service
public class ItemInventoryMQService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private KafkaTemplate<String, InventoryOperationResultMessageDTO> operationResultKafkaTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        } else if (MQConfig.currentMQType == MqType.RABBIT_MQ) {
            responseDestination = "RabbitMQ queue: " + RabbitMQConfig.getOrderServiceListenToQueue();
            rabbitTemplate.convertAndSend(RabbitMQConfig.INVENTORY_DIRECT_EXCHANGE,
                                          RabbitMQConfig.INVENTORY_QUEUE_RESPONSE_ROUTING_KEY,
                                          message);
        }

        log.info("Inventory service sent a message to {} \n Message content: {}", responseDestination, message);
    }

    public void send(Destination destination, InventoryOperationResultMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    public void sendToAmqRabbitMqReplyToQueue(InventoryOperationResultMessageDTO messageToReply, String routingKey) {
        rabbitTemplate.convertAndSend(routingKey, messageToReply);
        log.info("Inventory service sent a message to RabbitMQ queue: {} \n Message content: {}", AMQ_RABBITMQ_REPLY_TO, messageToReply);
    }
}
