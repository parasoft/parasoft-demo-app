package com.parasoft.demoapp.model.global.preferences;

import lombok.Data;

@Data
public class KafkaConfigEntity {
    private String orderServiceSendTo;
    private String orderServiceListenOn;
}
