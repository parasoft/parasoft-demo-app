package com.parasoft.demoapp.grpc.service;

import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import com.parasoft.demoapp.service.ItemInventoryService;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.ParameterValidator;
import grpc.demoApp.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@GrpcService
public class ProtobufServiceImpl extends ProtobufServiceGrpc.ProtobufServiceImplBase {

    private static final Object lock = new Object();

    @Autowired
    private ItemInventoryService itemInventoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

    @Override
    public void getStockByItemId(GetStockByItemIdRequest request, StreamObserver<GetStockByItemIdResponse> responseObserver) {
        try {
            Integer inStock = itemInventoryService.getInStockByItemId(request.getId());
            if (inStock == null) {
                InventoryNotFoundException inventoryNotFoundException = new InventoryNotFoundException(
                        MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, request.getId()));
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription(inventoryNotFoundException.getMessage())
                        .withCause(inventoryNotFoundException)
                        .asRuntimeException());
                log.error(inventoryNotFoundException.getMessage(), inventoryNotFoundException);
                return;
            }
            responseObserver.onNext(GetStockByItemIdResponse.newBuilder().setStock(inStock).build());
            responseObserver.onCompleted();
        } catch (ParameterException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
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

    @Override
    public StreamObserver<UpdateItemsInStockRequest> updateItemsInStock(StreamObserver<Item> responseObserver) {
        return new StreamObserver<UpdateItemsInStockRequest>() {
            @Override
            public void onNext(UpdateItemsInStockRequest value) {
                try {
                    synchronized (lock) {
                        int newInStock = 0;
                        ItemEntity item = itemService.getItemById(value.getId());
                        Integer inStock = item.getInStock();
                        if (value.getOperation() == Operation.DEDUCTION) {
                            newInStock = inStock - value.getValue();
                        } else if (value.getOperation() == Operation.ADDITION) {
                            newInStock = inStock + value.getValue();
                        }
                        ParameterValidator.requireNonNegative(newInStock, AssetMessages.INVENTORY_IS_NOT_ENOUGH);
                        itemInventoryRepository.save(new ItemInventoryEntity(value.getId(), newInStock));
                        responseObserver.onNext(Item.newBuilder()
                                .setId(item.getId())
                                .setName(item.getName())
                                .setStock(newInStock)
                                .build());
                    }
                } catch (ParameterException e) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException());
                    log.error(e.getMessage(), e);
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

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
