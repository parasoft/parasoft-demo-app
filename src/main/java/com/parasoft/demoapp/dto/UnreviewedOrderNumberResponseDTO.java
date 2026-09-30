package com.parasoft.demoapp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UnreviewedOrderNumberResponseDTO {
    int unreviewedByApprover;
    int unreviewedByPurchaser;

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public UnreviewedOrderNumberResponseDTO(int unreviewedByApprover, int unreviewedByPurchaserOrderNumber) {
        this.unreviewedByApprover = unreviewedByApprover;
        this.unreviewedByPurchaser = unreviewedByPurchaserOrderNumber;
    }
}
