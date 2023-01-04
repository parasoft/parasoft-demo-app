package com.parasoft.demoapp.grpc.service;

import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.service.ItemInventoryService;
import grpc.demoApp.GetStockByItemIdRequest;
import grpc.demoApp.GetStockByItemIdResponse;
import grpc.demoApp.ProtoServerGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;

@GrpcService
public class ProtoServiceImpl extends ProtoServerGrpc.ProtoServerImplBase {
    @Autowired
    private ItemInventoryService itemInventoryService;
    
    @Override
    public void getStockByItemId(GetStockByItemIdRequest request, StreamObserver<GetStockByItemIdResponse> responseObserver) {
        try {
            Integer inStock = itemInventoryService.getInStockByItemId(request.getId());
            if (inStock == null) {
                throw new InventoryNotFoundException(MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, request.getId()));
            }
            responseObserver.onNext(GetStockByItemIdResponse.newBuilder().setStock(inStock).build());
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
}
