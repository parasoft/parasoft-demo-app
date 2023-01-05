package com.parasoft.demoapp.grpc.service;

import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.service.ItemService;
import grpc.demoApp.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Slf4j
@GrpcService
public class ProtobufServiceImpl extends ProtobufServiceGrpc.ProtobufServiceImplBase {

    @Autowired
    private ItemService itemService;

    @Override
    public void getItemsInStock(Empty request, StreamObserver<Item> responseObserver) {
        try {
            List<ItemEntity> items = itemService.getAllItems();

            for (ItemEntity item: items) {
                if (item.getInStock() > 0) {
                    responseObserver.onNext(Item.newBuilder()
                            .setId(item.getId())
                            .setName(item.getName())
                            .setStock(item.getInStock())
                            .build());
                }
            }
            responseObserver.onCompleted();
        } catch (ItemNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
            log.error(e.getMessage(), e);
        }
    }
}
