package com.parasoft.demoapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryOperationResultMessageDTO {
    private InventoryOperation operation;
    private String orderNumber;
    private InventoryOperationStatus status;
    // Used for FAIL status.
    private String info;
}
