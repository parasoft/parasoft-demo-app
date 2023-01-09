package com.parasoft.demoapp.grpc.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private Integer stock;
}
