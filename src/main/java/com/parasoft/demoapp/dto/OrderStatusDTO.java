package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class OrderStatusDTO {

    private OrderStatus status;
    private String comments;
    @Schema(description = "Any changes for review status only work when role is purchaser and order status is not changed.")
    private boolean reviewedByPRCH;
    @Schema(description = "Any changes for review status only work when role is approver and order status is not changed.")
    private boolean reviewedByAPV;
}
