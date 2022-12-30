package com.parasoft.demoapp.grpc.service;


import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.service.ItemInventoryService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class JsonServiceImpl extends JsonServiceImplBase {
    @Autowired
    private ItemInventoryService itemInventoryService;

    @Override
    public void getStockByItemId(Long itemId, StreamObserver<Integer> responseObserver) {
        try {
            Integer inStock = itemInventoryService.getInStockByItemId(itemId);
            responseObserver.onNext(inStock);
            responseObserver.onCompleted();
        } catch (ParameterException e) {
            responseObserver.onError(Status.UNKNOWN
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
            e.printStackTrace();
        }
    }
}