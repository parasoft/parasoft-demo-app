package com.parasoft.demoapp.grpc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GRPCConfig {
    private int port;
    @Autowired
    public void setPort(@Value("${grpc.server.port}") int port) {
        this.port = port;
    }
}
