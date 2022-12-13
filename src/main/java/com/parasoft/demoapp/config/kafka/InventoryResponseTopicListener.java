package com.parasoft.demoapp.config.kafka;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.OrderMQService;
import com.parasoft.demoapp.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryResponseTopicListener extends KafkaRefreshableMessageListener<InventoryOperationResultMessageDTO> {
    private final OrderService orderService;
    private final OrderMQService orderMQService;

    public InventoryResponseTopicListener(
                                        ConcurrentKafkaListenerContainerFactory<String, InventoryOperationResultMessageDTO> kafkaListenerContainerFactory,
                                        OrderService orderService, OrderMQService orderMQService) {
        super(kafkaListenerContainerFactory, KafkaConfig.getOrderServiceListenToTopic());
        this.orderService = orderService;
        this.orderMQService = orderMQService;
    }

    @Override
    public void onMessage(ConsumerRecord<String, InventoryOperationResultMessageDTO> data) {
        InventoryOperationRequestMessageDTO messageToReply = orderService.handleMessageFromResponse(data.value());
        log.info("Order service receives a message from Kafka topic: {} \n Message content: {}", data.topic(), data.value());

        if(messageToReply == null) {
            log.info("Order service has no response message to reply.");
            return;
        }

        orderMQService.sendToInventoryRequestDestination(messageToReply);
    }
}
