package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInfoDTO {
    private Long itemId;
    private Integer quantity;

    public static List<InventoryInfoDTO> convertFrom(List<OrderItemEntity> orderItems) {
        List<InventoryInfoDTO> inventoryInfos =  new ArrayList<>();
        orderItems.forEach(orderItem -> {
            InventoryInfoDTO inventoryInfo = new InventoryInfoDTO(orderItem.getItemId(), orderItem.getQuantity());
            inventoryInfos.add(inventoryInfo);
        });
        return inventoryInfos;
    }
}
