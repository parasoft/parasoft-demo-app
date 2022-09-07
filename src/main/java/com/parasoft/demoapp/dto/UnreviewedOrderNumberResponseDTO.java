package com.parasoft.demoapp.dto;

import lombok.Data;

@Data
public class UnreviewedOrderNumberResponseDTO {
    int unreviewedByApprover;
    int unreviewedByPurchaser;

    public UnreviewedOrderNumberResponseDTO(int unreviewedByApprover, int unreviewedByPurchaserOrderNumber) {
        this.unreviewedByApprover = unreviewedByApprover;
        this.unreviewedByPurchaser = unreviewedByPurchaserOrderNumber;
    }
}
