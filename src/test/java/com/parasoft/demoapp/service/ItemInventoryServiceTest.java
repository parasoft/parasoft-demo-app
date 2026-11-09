package com.parasoft.demoapp.service;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static com.parasoft.demoapp.dto.InventoryOperation.DECREASE;
import static com.parasoft.demoapp.dto.InventoryOperation.INCREASE;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.FAIL;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

public class ItemInventoryServiceTest {

    private static final String ITEMS_BASE_URL = "http://localhost:8080/v1/assets/items";
    private static final String ITEMS_URL_TEMPLATE = ITEMS_BASE_URL + "/{itemId}";

    @InjectMocks
    private ItemInventoryService itemInventoryService;

    @Mock
    private ItemInventoryRepository itemInventoryRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestEndpointService restEndpointService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        lenient().when(restEndpointService.getItemsBaseUrl()).thenReturn(ITEMS_BASE_URL);
    }

    /**
     * Stubs GET /v1/assets/items/{itemId} to return an item carrying the given stock.
     */
    private void mockItemInRestApi(Long itemId, Integer inStock) {
        ItemEntity item = new ItemEntity();
        item.setId(itemId);
        item.setInStock(inStock);

        ResponseResult<ItemEntity> body =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK, item);

        when(exchangeForItem(itemId)).thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
    }

    /**
     * Stubs GET /v1/assets/items/{itemId} to respond with 404, as it does for an unknown item.
     */
    private void mockItemMissingInRestApi(Long itemId) {
        when(exchangeForItem(itemId)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<ResponseResult<ItemEntity>> exchangeForItem(Long itemId) {
        Map<String, Long> uriVariables = Collections.singletonMap("itemId", itemId);

        return restTemplate.exchange(
                eq(ITEMS_URL_TEMPLATE),
                eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<Void>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<ResponseResult<ItemEntity>>>any(),
                eq(uriVariables));
    }

    @Test
    public void testDecrease_normal() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForDecrease();

        // Stock is validated through the items REST API...
        mockItemInRestApi(1L, 1);
        mockItemInRestApi(2L, 3);
        // ...while the decrement itself is still written through the inventory repository.
        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 1));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(new ItemInventoryEntity(2L, 3));

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(DECREASE, requestMessage.getOrderNumber(), SUCCESS, null);
        assertEquals(expectedResultMessage, resultMessage);
        verify(restEndpointService, atLeastOnce()).getItemsBaseUrl();
    }

    @Test
    public void testDecrease_itemNotExist() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForDecrease();

        mockItemInRestApi(1L, 1);
        mockItemMissingInRestApi(2L);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(DECREASE, requestMessage.getOrderNumber(), FAIL,
                        "Inventory item with id 2 doesn't exist.");
        assertEquals(expectedResultMessage, resultMessage);
        verify(itemInventoryRepository, never()).save(any(ItemInventoryEntity.class));
    }

    @Test
    public void testDecrease_itemHasNoInventoryRecord() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForDecrease();

        mockItemInRestApi(1L, 1);
        // The item exists but carries no inventory record, so the REST API reports a null stock.
        mockItemInRestApi(2L, null);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

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

        mockItemInRestApi(1L, 1);
        mockItemInRestApi(2L, 1);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(DECREASE, requestMessage.getOrderNumber(), FAIL,
                        "Inventory item with id 2 is out of stock.");
        assertEquals(expectedResultMessage, resultMessage);
        verify(itemInventoryRepository, never()).save(any(ItemInventoryEntity.class));
    }

    @Test
    public void testDecrease_noRequestItem() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage =
                new InventoryOperationRequestMessageDTO(DECREASE, "23-456-001",
                        new ArrayList<>(), null);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

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

        // Existence is validated through the items REST API...
        mockItemInRestApi(1L, 1);
        mockItemInRestApi(2L, 3);
        // ...while the increment itself is still written through the inventory repository.
        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 1));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(new ItemInventoryEntity(2L, 3));

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(INCREASE, requestMessage.getOrderNumber(), SUCCESS, null);
        assertEquals(expectedResultMessage, resultMessage);
        verify(restEndpointService, atLeastOnce()).getItemsBaseUrl();
    }

    @Test
    public void testIncrease_normal_ignoresInsufficientStock() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForIncrease();

        // An increase only requires the item to exist, so a low stock must not fail the operation.
        mockItemInRestApi(1L, 0);
        mockItemInRestApi(2L, 0);
        when(itemInventoryRepository.findByItemId(1L)).thenReturn(new ItemInventoryEntity(1L, 0));
        when(itemInventoryRepository.findByItemId(2L)).thenReturn(new ItemInventoryEntity(2L, 0));

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(INCREASE, requestMessage.getOrderNumber(), SUCCESS, null);
        assertEquals(expectedResultMessage, resultMessage);
    }

    @Test
    public void testIncrease_itemNotExist() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage = createRequestMessageForIncrease();

        mockItemInRestApi(1L, 1);
        mockItemMissingInRestApi(2L);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

        // Then
        InventoryOperationResultMessageDTO expectedResultMessage =
                new InventoryOperationResultMessageDTO(INCREASE, requestMessage.getOrderNumber(), FAIL,
                        "Inventory item with id 2 doesn't exist.");
        assertEquals(expectedResultMessage, resultMessage);
        verify(itemInventoryRepository, never()).save(any(ItemInventoryEntity.class));
    }

    @Test
    public void testIncrease_noRequestItem() {
        // Given
        InventoryOperationRequestMessageDTO requestMessage =
                new InventoryOperationRequestMessageDTO(INCREASE, "23-456-002",
                        new ArrayList<>(), null);

        // When
        InventoryOperationResultMessageDTO resultMessage =
                itemInventoryService.handleMessageFromRequest(requestMessage);

        // Then
        assertNull(resultMessage);
    }

    private static InventoryOperationRequestMessageDTO createRequestMessageForIncrease() {
        return new InventoryOperationRequestMessageDTO(INCREASE, "23-456-002",
                Arrays.asList(
                        new InventoryInfoDTO(1L, 1),
                        new InventoryInfoDTO(2L, 2)), null);
    }


    @Test
    public void testSaveItemInStock_normal() {
        // Given
        Long itemId = 1L;
        Integer inStock = 10;

        when(itemInventoryRepository.save(new ItemInventoryEntity(itemId, inStock))).thenReturn(new ItemInventoryEntity(itemId, inStock));

        // When
        ItemInventoryEntity res = null;

        try {
            res = itemInventoryService.saveItemInStock(itemId, inStock);
        } catch (ParameterException e) {
            e.printStackTrace();
        }

        // Then
        assertEquals(itemId, res.getItemId());
        assertEquals(inStock, res.getInStock());
    }

    @Test
    public void testSaveItemInStock_nullItemId() {
        // Given
        Long itemId = null;
        Integer inStock = 10;

        when(itemInventoryRepository.save(new ItemInventoryEntity(itemId, inStock))).thenReturn(new ItemInventoryEntity(itemId, inStock));

        // When
        String message = "";

        try {
            itemInventoryService.saveItemInStock(itemId, inStock);
        } catch (ParameterException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(AssetMessages.ITEM_ID_CANNOT_BE_NULL, message);
    }

    @Test
    public void testSaveItemInStock_nullInStock() {
        // Given
        Long itemId = 1L;
        Integer inStock = null;

        when(itemInventoryRepository.save(new ItemInventoryEntity(itemId, inStock))).thenReturn(new ItemInventoryEntity(itemId, inStock));

        // When
        String message = "";

        try {
            itemInventoryService.saveItemInStock(itemId, inStock);
        } catch (ParameterException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(AssetMessages.IN_STOCK_CANNOT_BE_NULL, message);
    }

    @Test
    public void testSaveItemInStock_negativeInStock() {
        // Given
        Long itemId = 1L;
        Integer inStock = -10;

        when(itemInventoryRepository.save(new ItemInventoryEntity(itemId, inStock))).thenReturn(new ItemInventoryEntity(itemId, inStock));

        // When
        String message = "";

        try {
            itemInventoryService.saveItemInStock(itemId, inStock);
        } catch (ParameterException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, message);
    }

    @Test
    public void testGetInStockByItemId_normal() {
        // Given
        Long itemId = 1L;
        Integer inStock = 10;

        when(itemInventoryRepository.findInStockByItemId(itemId)).thenReturn(inStock);

        // When
        Integer res = null;

        try {
            res = itemInventoryService.getInStockByItemId(itemId);
        } catch (ParameterException e) {
            e.printStackTrace();
        }

        // Then
        assertEquals(inStock, res);
    }

    @Test
    public void testGetInStockByItemId_nullItemId() {
        // Given
        Long itemId = null;
        Integer inStock = 10;

        when(itemInventoryRepository.findInStockByItemId(itemId)).thenReturn(inStock);

        // When
        String message = "";

        try {
            itemInventoryService.getInStockByItemId(itemId);
        } catch (ParameterException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(AssetMessages.ITEM_ID_CANNOT_BE_NULL, message);
    }

    @Test
    public void testGetInStockByItemId_ItemInventoryNotExist() {
        // Given
        Long itemId = 1L;
        Integer expected = null;

        when(itemInventoryRepository.findInStockByItemId(itemId)).thenReturn(null);

        // When
        Integer res = null;

        try {
            res = itemInventoryService.getInStockByItemId(itemId);
        } catch (ParameterException e) {
            e.printStackTrace();
        }

        // Then
        assertEquals(expected, res);
    }

    @Test
    public void testRemoveItemInventoryByItemId_normal() {
        // Given
        Long itemId = 1L;

        when(itemInventoryRepository.existsById(itemId)).thenReturn(true);

        // When
        try {
            itemInventoryService.removeItemInventoryByItemId(itemId);
        } catch (ParameterException e) {
            e.printStackTrace();
        }

        // Then
        verify(itemInventoryRepository, times(1)).deleteById(itemId);
    }

    @Test
    public void testRemoveItemInventoryByItemId_itemInventoryNotExist() {
        // Given
        Long itemId = 1L;

        when(itemInventoryRepository.existsById(itemId)).thenReturn(false);

        // When
        try {
            itemInventoryService.removeItemInventoryByItemId(itemId);
        } catch (ParameterException e) {
            e.printStackTrace();
        }

        // Then
        verify(itemInventoryRepository, times(0)).deleteById(itemId);
    }

    @Test
    public void testRemoveItemInventoryByItemId_nullItemId() {
        // Given
        Long itemId = null;

        when(itemInventoryRepository.existsById(itemId)).thenReturn(true);

        // When
        String message = "";

        try {
            itemInventoryService.removeItemInventoryByItemId(itemId);
        } catch (ParameterException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(AssetMessages.ITEM_ID_CANNOT_BE_NULL, message);
        verify(itemInventoryRepository, times(0)).deleteById(itemId);
    }
}