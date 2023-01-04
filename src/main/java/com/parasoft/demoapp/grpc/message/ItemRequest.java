package com.parasoft.demoapp.grpc.message;

import lombok.Data;

@Data
public class ItemRequest {
    private Long id;
    private OperationType operation;
    private Integer value;
}
