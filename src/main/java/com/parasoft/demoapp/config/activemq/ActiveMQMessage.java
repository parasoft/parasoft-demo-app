package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.model.industry.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ActiveMQMessage {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryOperationRequestMessage {
        private InventoryOperation operation;
        private String orderNumber;
        private List<InventoryInfo> inventoryInfos;
        private String info;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryInfo {
        private Long itemId;
        private Integer quantity;

        public static List<InventoryInfo> convertFrom(List<OrderItemEntity> orderItems) {
            List<InventoryInfo> inventoryInfos =  new ArrayList<>();
            orderItems.forEach(orderItem -> {
                InventoryInfo inventoryInfo = new InventoryInfo(orderItem.getItemId(), orderItem.getQuantity());
                inventoryInfos.add(inventoryInfo);
            });
            return inventoryInfos;
        }
    }

    public enum InventoryOperation {
        INCREASE_FOR_ORDER_DENIAL, DECREASE_FOR_ORDER_CREATION, INCREASE_FOR_ORDER_CREATION_FAILED
    }
}
