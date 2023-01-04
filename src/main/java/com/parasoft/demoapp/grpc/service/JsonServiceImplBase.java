package com.parasoft.demoapp.grpc.service;

import com.parasoft.demoapp.grpc.util.Marshallers;
import com.parasoft.demoapp.model.industry.ItemEntity;
import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class JsonServiceImplBase implements BindableService {

    public static final String SERVICE_NAME = "grpc.demoApp.JsonService";

    static final MethodDescriptor<Long, Integer> GET_STOCK_BY_ITEM_ID_METHOD;
    static final MethodDescriptor<Object, ItemEntity> GET_ITEMS_IN_STOCK_METHOD;

    static {
        GET_STOCK_BY_ITEM_ID_METHOD = MethodDescriptor.newBuilder(
                Marshallers.marshallerFor(Long.class),
                Marshallers.marshallerFor(Integer.class))
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "getStockByItemId"))
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .build();
        
        GET_ITEMS_IN_STOCK_METHOD = MethodDescriptor.newBuilder(
                Marshallers.marshallerFor(Object.class),
                Marshallers.marshallerFor(ItemEntity.class))
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "getItemsInStock"))
                    .setType(MethodDescriptor.MethodType.SERVER_STREAMING)
                    .build();
    }

    public abstract void getStockByItemId(Long itemId, StreamObserver<Integer> streamObserver);
    public abstract void getItemsInStock(StreamObserver<ItemEntity> responseObserver);

    @Override
    public ServerServiceDefinition bindService() {
        ServerServiceDefinition.Builder ssd = ServerServiceDefinition.builder(SERVICE_NAME);

        ssd.addMethod(GET_STOCK_BY_ITEM_ID_METHOD, ServerCalls.asyncUnaryCall((request, responseObserver) -> {
            this.getStockByItemId(request, responseObserver);
        }));
        
        ssd.addMethod(GET_ITEMS_IN_STOCK_METHOD, ServerCalls.asyncServerStreamingCall((request, responseObserver) -> {
            this.getItemsInStock(responseObserver);
        }));

        return ssd.build();
    }
}