package com.parasoft.demoapp.grpc.service;


import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.grpc.message.ItemRequest;
import com.parasoft.demoapp.grpc.message.ItemResponse;
import com.parasoft.demoapp.grpc.message.OperationType;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import com.parasoft.demoapp.service.ItemInventoryService;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.ParameterValidator;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@GrpcService
public class JsonServiceImpl extends JsonServiceImplBase {

    private static final Object lock = new Object();

    @Autowired
    private ItemInventoryService itemInventoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

    @Override
    public void getStockByItemId(Long itemId, StreamObserver<Integer> responseObserver) {
        try {
            Integer inStock = itemInventoryService.getInStockByItemId(itemId);
            if (inStock == null) {
                String errorMsg = MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, itemId);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription(errorMsg)
                        .asRuntimeException());
                log.error(errorMsg);
                return;
            }
            responseObserver.onNext(inStock);
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
    public void getItemsInStock(StreamObserver<ItemEntity> responseObserver) {
        try {
            List<ItemEntity> items = itemService.getAllItems();
            for(ItemEntity item : items) {
                if(item.getInStock() > 0) {
                    responseObserver.onNext(item);
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
    public StreamObserver<ItemRequest> updateItemsInStock(StreamObserver<ItemResponse> responseObserver) {
        return new StreamObserver<ItemRequest>() {
            @Override
            public void onNext(ItemRequest itemRequest) {
                try {
                    synchronized (lock) {
                        ParameterValidator.requireNonNull(itemRequest, AssetMessages.REQUEST_PARAMETER_CANNOT_BE_NULL);
                        ParameterValidator.requireNonNull(itemRequest.getValue(), AssetMessages.OPERATION_QUANTITY_CANNOT_BE_NULL);
                        int newInStock;
                        ItemEntity item = itemService.getItemById(itemRequest.getId());
                        Integer inStock = item.getInStock();

                        if (itemRequest.getOperation() == OperationType.DEDUCTION) {
                            newInStock = inStock - itemRequest.getValue();
                        } else if (itemRequest.getOperation() == OperationType.ADDITION) {
                            newInStock = inStock + itemRequest.getValue();
                        } else {
                            responseObserver.onError(Status.INVALID_ARGUMENT
                                    .withDescription(AssetMessages.INCORRECT_OPERATION)
                                    .asRuntimeException());
                            log.error(AssetMessages.INCORRECT_OPERATION);
                            return;
                        }
                        ParameterValidator.requireNonNegative(newInStock, AssetMessages.INVENTORY_IS_NOT_ENOUGH);
                        itemInventoryRepository.save(new ItemInventoryEntity(itemRequest.getId(), newInStock));
                        responseObserver.onNext(new ItemResponse(itemRequest.getId(), item.getName(), newInStock));
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