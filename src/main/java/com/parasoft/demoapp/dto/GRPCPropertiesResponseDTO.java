package com.parasoft.demoapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GRPCPropertiesResponseDTO {
    private int port;

    public GRPCPropertiesResponseDTO(int port) {
        this.port = port;
    }
}
