package com.parasoft.demoapp.config.kafka;

import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.service.ItemInventoryMQService;
import com.parasoft.demoapp.service.ItemInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryRequestTopicListener extends KafkaRefreshableMessageListener<InventoryOperationRequestMessageDTO> {
    private final ItemInventoryService itemInventoryService;
    private final ItemInventoryMQService itemInventoryMQService;


    public InventoryRequestTopicListener(
            ConcurrentKafkaListenerContainerFactory<String, InventoryOperationRequestMessageDTO> operationRequestContainerFactory,
            ItemInventoryService itemInventoryService, ItemInventoryMQService itemInventoryMQService) {
        super(operationRequestContainerFactory, KafkaConfig.getOrderServiceRequestTopic());
        this.itemInventoryService = itemInventoryService;
        this.itemInventoryMQService = itemInventoryMQService;
    }

    @Override
    public void onMessage(ConsumerRecord<String, InventoryOperationRequestMessageDTO> data) {
        InventoryOperationResultMessageDTO messageToReply = itemInventoryService.handleMessageFromRequest(data.value());
        log.info("Inventory service receives a message from Kafka topic: {} \n Message content: {}", data.topic(), data.value());

        if(messageToReply == null) {
            log.info("Inventory service has no response message to reply.");
            return;
        }

        // TODO: send message to response topic
    }
}
