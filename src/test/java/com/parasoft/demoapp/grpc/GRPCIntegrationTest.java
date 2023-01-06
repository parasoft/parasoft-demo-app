package com.parasoft.demoapp.grpc;

import com.parasoft.demoapp.grpc.service.JsonServiceImpl;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import io.grpc.*;
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
public class GRPCIntegrationTest {

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
        assertEquals(getExpectedErrorMessage(Status.INVALID_ARGUMENT, MessageFormat.format(AssetMessages.ITEM_ID_CANNOT_BE_NULL, null)), error.getMessage());
    }

    private String getExpectedErrorMessage(Status status, String message) {
        return status.getCode() + ": " + message;
    }
}
