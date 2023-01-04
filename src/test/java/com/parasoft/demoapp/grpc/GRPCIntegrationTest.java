package com.parasoft.demoapp.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.parasoft.demoapp.grpc.util.Marshallers;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ItemInventoryService;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class GRPCIntegrationTest {

    @Autowired
    ItemInventoryService itemInventoryService;

    @Autowired
    GlobalPreferencesService globalPreferencesService;

    public static final String SERVICE_NAME = "grpc.demoApp.JsonService";
    public static final String TARGET = "localhost:50051";

    static final MethodDescriptor<Long, Integer> GET_STOCK_BY_ITEM_ID_METHOD = MethodDescriptor.newBuilder(
            Marshallers.marshallerFor(Long.class),
            Marshallers.marshallerFor(Integer.class))
            .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "getStockByItemId"))
            .setType(MethodDescriptor.MethodType.UNARY)
            .build();


    @Before
    public void setUp() {
        globalPreferencesService.resetAllIndustriesDatabase();
    }

    @Test
    public void testGetStockByItemId_normal() throws Throwable {
        Long id = 1L;
        ListenableFuture<Integer> result = createGetStockByItemIdCall(id);

        Integer stock = result.get();
        assertNotNull(stock);
    }

    @Test
    public void testGetStockByItemId_notFound() throws InterruptedException {
        Long id = 0L;
        ListenableFuture<Integer> result = createGetStockByItemIdCall(id);
        try {
            result.get();
        } catch (ExecutionException e) {
            String expectedMessage = Status.NOT_FOUND.getCode() + ": " + MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, id);
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void testGetStockByItemId_null() throws InterruptedException {
        Long id = null;
        ListenableFuture<Integer> result = createGetStockByItemIdCall(id);
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause().getMessage().startsWith(Status.INTERNAL.getCode().toString()));
        }
    }

    private ListenableFuture<Integer> createGetStockByItemIdCall(Long id) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(TARGET).usePlaintext().build();
        ClientCall<Long, Integer> call = channel.newCall(GET_STOCK_BY_ITEM_ID_METHOD, CallOptions.DEFAULT);
        return ClientCalls.futureUnaryCall(call, id);
    }
}
