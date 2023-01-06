package com.parasoft.demoapp.grpc.service;


import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.service.ItemInventoryService;
import com.parasoft.demoapp.service.ItemService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;

@GrpcService
public class JsonServiceImpl extends JsonServiceImplBase {
    @Autowired
    private ItemInventoryService itemInventoryService;
    
    @Autowired
    private ItemService itemService;

    @Override
    public void getStockByItemId(Long itemId, StreamObserver<Integer> responseObserver) {
        try {
            Integer inStock = itemInventoryService.getInStockByItemId(itemId);
            if (inStock == null) {
                InventoryNotFoundException inventoryNotFoundException = new InventoryNotFoundException(
                        MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, itemId));
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription(inventoryNotFoundException.getMessage())
                        .withCause(inventoryNotFoundException)
                        .asRuntimeException());
                return;
            }
            responseObserver.onNext(inStock);
            responseObserver.onCompleted();
        } catch (ParameterException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
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
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}