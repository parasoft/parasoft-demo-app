package com.parasoft.demoapp.dto;

import lombok.Data;

@Data
public class MqConfigDTO {
    private String orderServiceSendTo;
    private String orderServiceListenOn;
}
