package com.parasoft.demoapp.grpc;

import com.parasoft.demoapp.grpc.message.ItemRequest;
import com.parasoft.demoapp.grpc.message.ItemResponse;
import com.parasoft.demoapp.grpc.message.OperationType;
import com.parasoft.demoapp.grpc.service.JsonServiceImpl;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import io.grpc.*;
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
public class GrpcJsonServiceImplSpringTest {

    @Autowired
    GlobalPreferencesService globalPreferencesService;

    @Autowired
    JsonServiceImpl jsonServiceImpl;

    @Before
    public void setUp() {
        globalPreferencesService.resetAllIndustriesDatabase();
    }

    @Test
    public void testGetStockByItemId_normal() throws Throwable {
        StreamRecorder<Integer> responseObserver = StreamRecorder.create();
        jsonServiceImpl.getStockByItemId(1L, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        assertNull(responseObserver.getError());

        List<Integer> results = responseObserver.getValues();
        assertEquals(1, results.size());
        int response = results.get(0);
        assertEquals(10, response);
    }

    @Test
    public void testGetStockByItemId_notFound() throws Throwable {
        Long id = 0L;
        StreamRecorder<Integer> responseObserver = StreamRecorder.create();
        jsonServiceImpl.getStockByItemId(0L, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.NOT_FOUND, MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, id)), error.getMessage());

    }

    @Test
    public void testGetStockByItemId_nullItemId() throws Throwable {
        StreamRecorder<Integer> responseObserver = StreamRecorder.create();
        jsonServiceImpl.getStockByItemId(null, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.INVALID_ARGUMENT, AssetMessages.ITEM_ID_CANNOT_BE_NULL), error.getMessage());
    }

    @Test
    public void testUpdateItemsInStock_normal() throws Throwable {
        StreamRecorder<ItemResponse> responseObserver = StreamRecorder.create();
        StreamObserver<ItemRequest> requestObserver = jsonServiceImpl.updateItemsInStock(responseObserver);

        requestObserver.onNext(new ItemRequest(1L, OperationType.ADDITION, 1));
        requestObserver.onNext(new ItemRequest(1L, OperationType.ADDITION, 1));
        requestObserver.onCompleted();

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        assertNull(responseObserver.getError());

        List<ItemResponse> results = responseObserver.getValues();
        assertEquals(2, results.size());
        ItemResponse response1 = results.get(0);
        ItemResponse response2 = results.get(1);
        assertEquals(11, response1.getStock().intValue());
        assertEquals(12, response2.getStock().intValue());
    }

    @Test
    public void testUpdateItemsInStock_nullItem() throws Throwable {
        StreamRecorder<ItemResponse> responseObserver = StreamRecorder.create();
        StreamObserver<ItemRequest> requestObserver = jsonServiceImpl.updateItemsInStock(responseObserver);

        requestObserver.onNext(null);
        requestObserver.onCompleted();

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.INVALID_ARGUMENT, AssetMessages.REQUEST_PARAMETER_CANNOT_BE_NULL), error.getMessage());
    }

    @Test
    public void testUpdateItemsInStock_nullItemId() throws Throwable {
        StreamRecorder<ItemResponse> responseObserver = StreamRecorder.create();
        StreamObserver<ItemRequest> requestObserver = jsonServiceImpl.updateItemsInStock(responseObserver);

        requestObserver.onNext(new ItemRequest(1L, OperationType.ADDITION, null));
        requestObserver.onCompleted();

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.INVALID_ARGUMENT, AssetMessages.OPERATION_QUANTITY_CANNOT_BE_NULL), error.getMessage());
    }

    @Test
    public void testUpdateItemsInStock_notFound() throws Throwable {
        StreamRecorder<ItemResponse> responseObserver = StreamRecorder.create();
        StreamObserver<ItemRequest> requestObserver = jsonServiceImpl.updateItemsInStock(responseObserver);

        requestObserver.onNext(new ItemRequest(0L, OperationType.ADDITION, 10));
        requestObserver.onCompleted();

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.NOT_FOUND, MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, 0L)), error.getMessage());
    }

    @Test
    public void testUpdateItemsInStock_nullOperation() throws Throwable {
        StreamRecorder<ItemResponse> responseObserver = StreamRecorder.create();
        StreamObserver<ItemRequest> requestObserver = jsonServiceImpl.updateItemsInStock(responseObserver);

        requestObserver.onNext(new ItemRequest(1L, null, 10));
        requestObserver.onCompleted();

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        Throwable error = responseObserver.getError();
        assertNotNull(error);
        assertEquals(getExpectedErrorMessage(Status.INVALID_ARGUMENT, AssetMessages.INCORRECT_OPERATION), error.getMessage());
    }

    @Test
    public void testUpdateItemsInStock_InStockNotEnough() throws Throwable {
        StreamRecorder<ItemResponse> responseObserver = StreamRecorder.create();
        StreamObserver<ItemRequest> requestObserver = jsonServiceImpl.updateItemsInStock(responseObserver);

        requestObserver.onNext(new ItemRequest(1L, OperationType.DEDUCTION, 1000));
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
