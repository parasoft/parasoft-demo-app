package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQConfig;
import com.parasoft.demoapp.dto.*;
import com.parasoft.demoapp.model.global.preferences.MqType;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.parasoft.demoapp.config.kafka.KafkaConfig.ADMIN_CLIENT_TIMEOUT_MS;

import static org.springframework.amqp.core.Address.AMQ_RABBITMQ_REPLY_TO;

@Slf4j
@Component
public class OrderMQService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private KafkaTemplate<String, InventoryOperationRequestMessageDTO> operationRequestKafkaTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendToApprover(OrderMQMessageDTO messageDto) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.TOPIC_ORDER_APPROVER, messageDto);
    }

    public void sendToPurchaser(OrderMQMessageDTO messageDto) {
        jmsMessagingTemplate.convertAndSend(ActiveMQConfig.TOPIC_ORDER_PURCHASER, messageDto);
    }

    public void sendToInventoryRequestDestination(InventoryOperation operation, String orderNumber, List<OrderItemEntity> orderItems) {
        sendToInventoryRequestDestination(operation, orderNumber, orderItems, null);
    }

    public void sendToInventoryRequestDestination(InventoryOperation operation, String orderNumber, List<OrderItemEntity> orderItems, String info) {
        sendToInventoryRequestDestination(new InventoryOperationRequestMessageDTO(operation, orderNumber, InventoryInfoDTO.convertFrom(orderItems), info));
    }

    public void sendToInventoryRequestDestination(InventoryOperationRequestMessageDTO message) {
        String requestDestination = null;

        if (MQConfig.currentMQType == MqType.ACTIVE_MQ) {
            ActiveMQQueue destination = ActiveMQConfig.getOrderServiceSendToQueue();
            requestDestination = "ActiveMQ: " + destination.toString();
            jmsMessagingTemplate.convertAndSend(destination, message);
        } else if (MQConfig.currentMQType == MqType.KAFKA) {
            String destination = KafkaConfig.getOrderServiceSendToTopic();
            requestDestination = "Kafka topic: " + destination;
            try {
                operationRequestKafkaTemplate.send(destination, 0, message.getOrderNumber(), message).get(ADMIN_CLIENT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new KafkaException("Can not send message to Kafka broker.", e);
            }
        } else if (MQConfig.currentMQType == MqType.RABBIT_MQ) {
            requestDestination = "RabbitMQ queue: " + RabbitMQConfig.getOrderServiceSendToQueue();
            try{
                rabbitTemplate.convertAndSend(RabbitMQConfig.INVENTORY_DIRECT_EXCHANGE,
                                              RabbitMQConfig.INVENTORY_QUEUE_REQUEST_ROUTING_KEY,
                                              message);
            } catch (Exception e) {
                throw new AmqpException("Can not send message to RabbitMQ broker.", e);
            }
        }

        log.info("Order service sent a message to {} \n Message content: {}", requestDestination, message);
    }

    public void send(Destination destination, InventoryOperationRequestMessageDTO message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    public void sendToAmqRabbitMqReplyToQueue(InventoryOperationRequestMessageDTO messageToReply, String routingKey) {
        rabbitTemplate.convertAndSend(routingKey, messageToReply);
        log.info("Order service sent a message to RabbitMQ queue: {} \n Message content: {}", AMQ_RABBITMQ_REPLY_TO, messageToReply);
    }
}
