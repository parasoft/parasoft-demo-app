package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class OrderStatusDTO {

    @NotNull
    private OrderStatus status;
    private String comments;
    @Schema(description = "Any changes for review status only work when role is purchaser and order status is not changed.")
    private Boolean reviewedByPRCH = false;
    @Schema(description = "Any changes for review status only work when role is approver and order status is not changed.")
    private Boolean reviewedByAPV = false;
}
