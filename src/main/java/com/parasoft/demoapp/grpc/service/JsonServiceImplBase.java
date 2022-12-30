package com.parasoft.demoapp.grpc.service;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class JsonServiceImplBase implements BindableService {

    public static final String SERVICE_NAME = "grpc.demoApp.JsonService";

    @Override
    public ServerServiceDefinition bindService() {
        ServerServiceDefinition.Builder ssd = ServerServiceDefinition.builder(SERVICE_NAME);
        return ssd.build();
    }
}