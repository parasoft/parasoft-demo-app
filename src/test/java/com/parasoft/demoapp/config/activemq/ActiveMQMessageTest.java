package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.model.industry.OrderItemEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActiveMQMessageTest {

    @Test
    public void test_InventoryInfo_convertFrom() {
        // Given
        OrderItemEntity orderItem = new OrderItemEntity("name", "description", "imagePath", 1);
        orderItem.setItemId(1L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        // When
        List<ActiveMQMessage.InventoryInfo> result = ActiveMQMessage.InventoryInfo.convertFrom(orderItems);

        // Then
        assertEquals(result.size(), orderItems.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getItemId(), orderItems.get(i).getItemId());
            assertEquals(result.get(i).getQuantity(), orderItems.get(i).getQuantity());
        }
    }
}