package com.parasoft.demoapp.grpc.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetStockByItemIdRequest {
    private Long id;
}
