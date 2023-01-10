package com.parasoft.demoapp.grpc;

import com.parasoft.demoapp.grpc.service.ProtobufServiceImpl;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import grpc.demoApp.GetStockByItemIdRequest;
import grpc.demoApp.GetStockByItemIdResponse;
import io.grpc.Status;
import io.grpc.internal.testing.StreamRecorder;
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
        assertEquals(1, results.size());
        int response = results.get(0).getStock();
        assertEquals(10, response);
    }

    @Test
    public void testGetStockByItemId_notFound() throws Throwable {
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

    private String getExpectedErrorMessage(Status status, String message) {
        return status.getCode() + ": " + message;
    }
}
