package com.parasoft.demoapp.grpc;

import com.parasoft.demoapp.grpc.service.ProtobufServiceImpl;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ItemInventoryService;
import grpc.demoApp.*;
import io.grpc.Status;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class GrpcProtobufServiceImplSpringTest {

    @Autowired
    GlobalPreferencesService globalPreferencesService;
    
    @Autowired
    ProtobufServiceImpl protobufServiceImpl;
    
    @Autowired
    ItemInventoryService itemInventoryService;
    
    @Before
    public void setUp() {
        globalPreferencesService.resetAllIndustriesDatabase();
    }

    @Test
    public void testGetStockByItemId_normal() throws Throwable {
        StreamRecorder<GetStockByItemIdResponse> responseObserver = StreamRecorder.create();
        GetStockByItemIdRequest request = GetStockByItemIdRequest.newBuilder().setId(1L).build();
        protobufServiceImpl.getStockByItemId(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        assertNull(responseObserver.getError());

        List<GetStockByItemIdResponse> results = responseObserver.getValues();
        int response = results.get(0).getStock();
        
        assertEquals(1, results.size());
        assertEquals(10, response);
    }

    @Test
    public void testGetStockByItemId_inStockNotFound() throws Throwable {
        long itemId = 0L;
        StreamRecorder<GetStockByItemIdResponse> responseObserver = StreamRecorder.create();
        GetStockByItemIdRequest request = GetStockByItemIdRequest.newBuilder().setId(itemId).build();
        protobufServiceImpl.getStockByItemId(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.NOT_FOUND, MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, itemId)), error.getMessage());
    }
    
    @Test
    public void testGetStockByItemId_nullRequest() throws Throwable {
        StreamRecorder<GetStockByItemIdResponse> responseObserver = StreamRecorder.create();
        protobufServiceImpl.getStockByItemId(null, responseObserver);
        
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        
        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(Status.INTERNAL.getCode().toString(), error.getMessage());
    }
    
    @Test
    public void testGetItemsInStock_normal() throws Throwable {
        Long itemId = 1L;
        StreamRecorder<Item> responseObserver = StreamRecorder.create();
        GetItemsInStockRequest request = GetItemsInStockRequest.newBuilder().build();
        protobufServiceImpl.getItemsInStock(request, responseObserver);
        
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        
        List<Item> results = responseObserver.getValues();
        Item response = results.get(0);
    
        assertNull(responseObserver.getError());
        assertEquals(9, results.size());
        assertEquals(itemId.longValue(), response.getId());
        assertEquals("Blue Sleeping Bag", response.getName());
        assertEquals(10, response.getStock());
        
        // When there is an item that is not in stock
        itemInventoryService.saveItemInStock(itemId, 0);
        responseObserver = StreamRecorder.create();
        protobufServiceImpl.getItemsInStock(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
    
        results = responseObserver.getValues();
        response = results.get(0);
        
        assertNull(responseObserver.getError());
        assertEquals(8, results.size());
        assertEquals(2L, response.getId());
        assertEquals("Green Sleeping Bag", response.getName());
        assertEquals(15, response.getStock());
    }
    
    @Test
    public void testGetItemsInStock_itemsNotFound() throws Throwable {
        globalPreferencesService.clearCurrentIndustryDatabase();
        StreamRecorder<Item> responseObserver = StreamRecorder.create();
        GetItemsInStockRequest request = GetItemsInStockRequest.newBuilder().build();
        protobufServiceImpl.getItemsInStock(request, responseObserver);
        
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        
        assertEquals(0, responseObserver.getValues().size());
        assertNotNull(responseObserver.getError());
        assertEquals(getExpectedErrorMessage(Status.NOT_FOUND, AssetMessages.NO_ITEMS), responseObserver.getError().getMessage());
    }
    
    @Test
    public void testUpdateItemsInStock_normal() throws Throwable {
        StreamRecorder<Item> responseObserver = StreamRecorder.create();
        StreamObserver<UpdateItemsInStockRequest> requestObserver = protobufServiceImpl.updateItemsInStock(responseObserver);
        
        requestObserver.onNext(UpdateItemsInStockRequest.newBuilder().setId(1L).setOperation(Operation.ADDITION).setValue(1).build());
        requestObserver.onNext(UpdateItemsInStockRequest.newBuilder().setId(1L).setOperation(Operation.ADDITION).setValue(1).build());
        requestObserver.onCompleted();
        
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        assertNull(responseObserver.getError());
        
        List<Item> results = responseObserver.getValues();
        Item response1 = results.get(0);
        Item response2 = results.get(1);
        
        assertEquals(2, results.size());
        assertEquals(11, response1.getStock());
        assertEquals(12, response2.getStock());
    }
    
    @Test
    public void testUpdateItemsInStock_nullItem() throws Throwable {
        StreamRecorder<Item> responseObserver = StreamRecorder.create();
        StreamObserver<UpdateItemsInStockRequest> requestObserver = protobufServiceImpl.updateItemsInStock(responseObserver);
        
        requestObserver.onNext(null);
        requestObserver.onCompleted();
        
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        
        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(Status.INTERNAL.getCode().toString(), error.getMessage());
    }
    
    @Test
    public void testUpdateItemsInStock_itemNotFound() throws Throwable {
        StreamRecorder<Item> responseObserver = StreamRecorder.create();
        StreamObserver<UpdateItemsInStockRequest> requestObserver = protobufServiceImpl.updateItemsInStock(responseObserver);
    
        requestObserver.onNext(UpdateItemsInStockRequest.newBuilder().setId(0L).setOperation(Operation.ADDITION).setValue(10).build());
        requestObserver.onCompleted();
        
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        
        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, 0L)), error.getMessage());
    }
    
    @Test
    public void testUpdateItemsInStock_inStockNotEnough() throws Throwable {
        StreamRecorder<Item> responseObserver = StreamRecorder.create();
        StreamObserver<UpdateItemsInStockRequest> requestObserver = protobufServiceImpl.updateItemsInStock(responseObserver);
    
        requestObserver.onNext(UpdateItemsInStockRequest.newBuilder().setId(1L).setOperation(Operation.DEDUCTION).setValue(1000).build());
        requestObserver.onCompleted();
        
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        
        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.INVALID_ARGUMENT, AssetMessages.INVENTORY_IS_NOT_ENOUGH), error.getMessage());
    }

    private String getExpectedErrorMessage(Status status, String message) {
        return status.getCode() + ": " + message;
    }
}
