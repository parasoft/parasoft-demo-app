package com.parasoft.demoapp.grpc.service;


import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.grpc.message.OperationType;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.service.ItemInventoryService;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.ParameterValidator;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import com.parasoft.demoapp.exception.IncorrectOperationException;
import com.parasoft.demoapp.grpc.message.ItemRequest;
import com.parasoft.demoapp.grpc.message.ItemResponse;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@GrpcService
public class JsonServiceImpl extends JsonServiceImplBase {

    private static final Object clock = new Object();

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
                throw new InventoryNotFoundException(MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, itemId));
            }
            responseObserver.onNext(inStock);
            responseObserver.onCompleted();
        } catch (Exception e) {
            if (e instanceof ParameterException) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription(e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            } else if (e instanceof InventoryNotFoundException) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            } else {
                responseObserver.onError(Status.INTERNAL
                        .withDescription(e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
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
        } catch (Exception e) {
            if (e instanceof ItemNotFoundException) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            } else {
                responseObserver.onError(Status.INTERNAL
                        .withDescription(e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public StreamObserver<ItemRequest> updateItemsInStock(StreamObserver<ItemResponse> responseObserver) {
        return new StreamObserver<ItemRequest>() {
            @Override
            public void onNext(ItemRequest value) {
                try {
                    synchronized (clock) {
                        ParameterValidator.requireNonNull(value, AssetMessages.REQUEST_PARAMETER_CANNOT_BE_EMPTY);
                        ParameterValidator.requireNonNull(value.getValue(), AssetMessages.IN_STOCK_CANNOT_BE_NULL);
                        Integer newInStock = 0;
                        ItemEntity item = itemService.getItemById(value.getId());
                        Integer inStock = item.getInStock();

                        if (value.getOperation() == null) {
                            throw new IncorrectOperationException(AssetMessages.INCORRECT_OPERATION);
                        } else if (value.getOperation() == OperationType.DEDUCTION) {
                            newInStock = inStock - value.getValue();
                        } else if (value.getOperation() == OperationType.ADDITION) {
                            newInStock = inStock + value.getValue();
                        }
                        ParameterValidator.requireNonNegative(newInStock, AssetMessages.INVENTORY_IS_NOT_ENOUGH);
                        itemInventoryRepository.save(new ItemInventoryEntity(value.getId(), newInStock));
                        responseObserver.onNext(new ItemResponse(value.getId(), item.getName(), newInStock));
                    }
                } catch (Exception e) {
                    if (e instanceof ParameterException) {
                        responseObserver.onError(Status.INVALID_ARGUMENT
                                .withDescription(e.getMessage())
                                .withCause(e)
                                .asRuntimeException());
                    } else if (e instanceof ItemNotFoundException) {
                        responseObserver.onError(Status.NOT_FOUND
                                .withDescription(e.getMessage())
                                .withCause(e)
                                .asRuntimeException());
                    } else if (e instanceof IncorrectOperationException) {
                        responseObserver.onError(Status.UNIMPLEMENTED
                                .withDescription(e.getMessage())
                                .withCause(e)
                                .asRuntimeException());
                    } else {
                        responseObserver.onError(Status.INTERNAL
                                .withDescription(e.getMessage())
                                .withCause(e)
                                .asRuntimeException());
                    }
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