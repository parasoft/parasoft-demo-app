package com.parasoft.demoapp.grpc.service;


import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.grpc.message.OperationType;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.service.ItemInventoryService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import com.parasoft.demoapp.exception.IncorrectOperationException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.grpc.message.ItemRequest;
import com.parasoft.demoapp.grpc.message.ItemResponse;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import com.parasoft.demoapp.service.ItemService;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;

@GrpcService
public class JsonServiceImpl extends JsonServiceImplBase {
    @Autowired
    private ItemInventoryService itemInventoryService;

    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

    @Autowired
    private ItemService itemService;

    @Override
    public void getStockByItemId(Long itemId, StreamObserver<Integer> responseObserver) {
        try {
            Integer inStock = itemInventoryService.getInStockByItemId(itemId);
            if (inStock == null) {
                throw new InventoryNotFoundException(MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, itemId));
            }
            responseObserver.onNext(inStock);
            responseObserver.onCompleted();
        } catch (ParameterException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (InventoryNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public StreamObserver<ItemRequest> updateItemsInStock(StreamObserver<ItemResponse> responseObserver) {
        return new StreamObserver<ItemRequest>() {
            @Override
            public void onNext(ItemRequest value) {
                try {
                    Integer newInStock = 0;
                    ItemEntity item = itemService.getItemById(value.getId());
                    Integer inStock = item.getInStock();

                    if (value.getValue() == null) {
                        throw new ParameterException(AssetMessages.IN_STOCK_CANNOT_BE_NULL);
                    } else if (value.getOperation() == null) {
                        throw new IncorrectOperationException(AssetMessages.INCORRECT_OPERATION);
                    } else if (value.getOperation() == OperationType.DEDUCTION) {
                        newInStock = inStock - value.getValue();
                        if (newInStock < 0) {
                            throw new ParameterException(AssetMessages.INVENTORY_IS_NOT_ENOUGH);
                        }
                    } else if (value.getOperation() == OperationType.ADDITION) {
                        newInStock = inStock + value.getValue();
                    }
                    itemInventoryRepository.save(new ItemInventoryEntity(value.getId(), newInStock));
                    responseObserver.onNext(new ItemResponse(value.getId(), item.getName(), newInStock));
                }  catch (ParameterException e) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException());
                }  catch (ItemNotFoundException e) {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException());
                } catch (IncorrectOperationException e) {
                    responseObserver.onError(Status.UNIMPLEMENTED
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException());
                } catch (Throwable t) {
                    responseObserver.onError(Status.INTERNAL
                            .withDescription(t.getMessage())
                            .withCause(t)
                            .asRuntimeException());
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