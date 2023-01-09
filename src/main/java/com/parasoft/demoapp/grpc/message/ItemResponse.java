package com.parasoft.demoapp.grpc.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private Integer stock;
}
