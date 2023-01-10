package com.parasoft.demoapp.grpc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GRPCConfig {
    @Value("${grpc.server.port}")
    private int port;
}
