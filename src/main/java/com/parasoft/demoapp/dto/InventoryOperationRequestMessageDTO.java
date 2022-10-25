package com.parasoft.demoapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryOperationRequestMessageDTO {
    private InventoryOperation operation;
    private String orderNumber;
    private List<InventoryInfoDTO> inventoryInfos;
    private String info;
}
