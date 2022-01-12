package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderMQMessageDTO {

    private String orderNumber;
    private OrderStatus status;
    private String information;

}
