package com.parasoft.demoapp.grpc.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    private OperationType operation;
    private Integer value;
}
