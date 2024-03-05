package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class OrderStatusDTO {

    @NotNull
    private OrderStatus status;
    private String comments;
    @NotNull
    @Schema(description = "Any changes for review status only work when role is purchaser and order status is not changed.")
    private boolean reviewedByPRCH;
    @NotNull
    @Schema(description = "Any changes for review status only work when role is approver and order status is not changed.")
    private boolean reviewedByAPV;
}
