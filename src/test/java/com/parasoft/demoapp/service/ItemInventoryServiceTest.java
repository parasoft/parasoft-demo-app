package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static com.parasoft.demoapp.dto.InventoryOperation.DECREASE;
import static com.parasoft.demoapp.dto.InventoryOperation.INCREASE;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.FAIL;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class ItemInventoryServiceTest {

    @InjectMocks
    private ItemInventoryService itemInventoryService;

    @Mock
    private ItemInventoryRepository itemInventoryRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDecrease_normal() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForDecrease();

        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 1));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(new ItemInventoryEntity(2L, 3));

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequestQueue(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(DECREASE, requestMessage.getOrderNumber(), SUCCESS, null);
        assertEquals(expectedResultMessage, resultMessage);
    }

    @Test
    public void testDecrease_itemNotExist() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForDecrease();

        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 1));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(null);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequestQueue(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(DECREASE, requestMessage.getOrderNumber(), FAIL,
                        "Inventory item with id 2 doesn't exist.");
        assertEquals(expectedResultMessage, resultMessage);
    }

    @Test
    public void testDecrease_itemOutOfStock() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForDecrease();

        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 1));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(new ItemInventoryEntity(2L, 1));

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequestQueue(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(DECREASE, requestMessage.getOrderNumber(), FAIL,
                        "Inventory item with id 2 is out of stock.");
        assertEquals(expectedResultMessage, resultMessage);
    }

    @Test
    public void testDecrease_noRequestItem() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage =
                new InventoryOperationRequestMessageDTO(DECREASE, "23-456-001",
                        new ArrayList<>(), null);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequestQueue(requestMessage);

        // Then
        assertNull(resultMessage);
    }

    private static InventoryOperationRequestMessageDTO createRequestMessageForDecrease() {
        return new InventoryOperationRequestMessageDTO(DECREASE, "23-456-001",
                Arrays.asList(
                        new InventoryInfoDTO(1L, 1),
                        new InventoryInfoDTO(2L, 2)), null);
    }

    @Test
    public void testIncrease_normal() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForIncrease();

        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 1));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(new ItemInventoryEntity(2L, 3));

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequestQueue(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(INCREASE, requestMessage.getOrderNumber(), SUCCESS, null);
        assertEquals(expectedResultMessage, resultMessage);
    }

    @Test
    public void testIncrease_itemNotExist() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForIncrease();

        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 1));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(null);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequestQueue(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(INCREASE, requestMessage.getOrderNumber(), FAIL,
                        "Inventory item with id 2 doesn't exist.");
        assertEquals(expectedResultMessage, resultMessage);
    }

    @Test
    public void testIncrease_noRequestItem() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage =
                new InventoryOperationRequestMessageDTO(INCREASE, "23-456-002",
                        new ArrayList<>(), null);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequestQueue(requestMessage);

        // Then
        assertNull(resultMessage);
    }

    private static InventoryOperationRequestMessageDTO createRequestMessageForIncrease() {
        return new InventoryOperationRequestMessageDTO(INCREASE, "23-456-002",
                Arrays.asList(
                        new InventoryInfoDTO(1L, 1),
                        new InventoryInfoDTO(2L, 2)), null);
    }

}