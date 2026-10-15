package com.parasoft.demoapp.service;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.parasoft.demoapp.dto.InventoryOperation.DECREASE;
import static com.parasoft.demoapp.dto.InventoryOperation.INCREASE;
import static com.parasoft.demoapp.dto.InventoryOperation.NONE;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.FAIL;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.SUCCESS;

@Slf4j
@Service
public class ItemInventoryService {

    private static final String ITEM_DOES_NOT_EXIST = "Inventory item with id %d doesn't exist.";
    private static final String ITEM_OUT_OF_STOCK = "Inventory item with id %d is out of stock.";
    private static final String UNABLE_TO_VERIFY_ITEM = "Unable to verify inventory item with id %d: %s";

    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestEndpointService restEndpointService;

    @Transactional(value = "industryTransactionManager")
    public InventoryOperationResultMessageDTO handleMessageFromRequest(InventoryOperationRequestMessageDTO
                                                                                        requestMessage) {
        InventoryOperation operation = requestMessage.getOperation();
        if (operation == NONE) {
            return null;
        }

        InventoryOperationResultMessageDTO resultMessage = new InventoryOperationResultMessageDTO();
        resultMessage.setOperation(operation);
        resultMessage.setOrderNumber(requestMessage.getOrderNumber());
        List<InventoryInfoDTO> requestedItems = requestMessage.getInventoryInfos();
        if (CollectionUtils.isEmpty(requestedItems)) {
            return null;
        }

        if (operation == DECREASE) {
            return decrease(requestedItems, resultMessage);
        }

        if (operation == INCREASE) {
            return increase(requestedItems, resultMessage);
        }

        return null;
    }

    private synchronized InventoryOperationResultMessageDTO decrease(List<InventoryInfoDTO> requestedItems,
                                                        InventoryOperationResultMessageDTO resultMessage) {
        String errorMessage = validateItemStockBeforeDecrease(requestedItems);
        if (errorMessage.isEmpty()) {
            requestedItems.forEach(this::decreaseItemStock);
            resultMessage.setStatus(SUCCESS);
        } else {
            resultMessage.setInfo(errorMessage);
            resultMessage.setStatus(FAIL);
        }
        return resultMessage;
    }

    private synchronized InventoryOperationResultMessageDTO increase(List<InventoryInfoDTO> requestedItems,
                                                                     InventoryOperationResultMessageDTO resultMessage) {
        String errorMessage = validateItemStockBeforeIncrease(requestedItems);
        if (errorMessage.isEmpty()) {
            requestedItems.forEach(this::increaseItemStock);
            resultMessage.setStatus(SUCCESS);
        } else {
            resultMessage.setInfo(errorMessage);
            resultMessage.setStatus(FAIL);
        }
        return resultMessage;
    }

    private void decreaseItemStock(InventoryInfoDTO requestedItem) {
        ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(requestedItem.getItemId());
        inventoryItem.setInStock(inventoryItem.getInStock() - requestedItem.getQuantity());
        itemInventoryRepository.save(inventoryItem);
    }

    private void increaseItemStock(InventoryInfoDTO requestedItem) {
        ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(requestedItem.getItemId());
        inventoryItem.setInStock(inventoryItem.getInStock() + requestedItem.getQuantity());
        itemInventoryRepository.save(inventoryItem);
    }

    private String validateItemStockBeforeDecrease(List<InventoryInfoDTO> requestedItems) {
        for (InventoryInfoDTO requestedItem : requestedItems) {
            Long itemId = requestedItem.getItemId();
            Integer inStock;
            try {
                inStock = getInStockByItemIdFromRestApi(itemId);
            } catch (ItemNotFoundException e) {
                return String.format(ITEM_DOES_NOT_EXIST, itemId);
            } catch (Exception e) {
                log.error("Failed to obtain inventory item with id {} from the items REST API.", itemId, e);
                return String.format(UNABLE_TO_VERIFY_ITEM, itemId, e.getMessage());
            }

            if (inStock == null) {
                return String.format(ITEM_DOES_NOT_EXIST, itemId);
            } else if (inStock < requestedItem.getQuantity()) {
                return String.format(ITEM_OUT_OF_STOCK, itemId);
            }
        }
        return "";
    }

    private String validateItemStockBeforeIncrease(List<InventoryInfoDTO> requestedItems) {
        for (InventoryInfoDTO requestedItem : requestedItems) {
            Long itemId = requestedItem.getItemId();
            Integer inStock;
            try {
                inStock = getInStockByItemIdFromRestApi(itemId);
            } catch (ItemNotFoundException e) {
                return String.format(ITEM_DOES_NOT_EXIST, itemId);
            } catch (Exception e) {
                log.error("Failed to obtain inventory item with id {} from the items REST API.", itemId, e);
                return String.format(UNABLE_TO_VERIFY_ITEM, itemId, e.getMessage());
            }

            if (inStock == null) {
                return String.format(ITEM_DOES_NOT_EXIST, itemId);
            }
        }
        return "";
    }

    /**
     * Obtains the current stock of an item through the items REST API
     * (GET /v1/assets/items/{itemId}) instead of reading the inventory table directly.
     *
     * <p>This runs on an MQ listener thread, so there is no incoming HTTP request whose headers
     * could be forwarded. That is fine because GET on /v1/assets/** is permitted anonymously.</p>
     *
     * @return the current stock, or {@code null} when the item has no inventory record
     * @throws ItemNotFoundException when the REST API reports that the item does not exist
     */
    protected Integer getInStockByItemIdFromRestApi(Long itemId) throws ItemNotFoundException {
        String itemsBaseUrl = restEndpointService.getItemsBaseUrl();
        if (itemsBaseUrl == null) {
            throw new IllegalStateException("The items REST endpoint route is not available.");
        }

        Map<String, Long> uriVariables = new HashMap<>();
        uriVariables.put("itemId", itemId);

        try {
            ResponseEntity<ResponseResult<ItemEntity>> entity =
                    restTemplate.exchange(itemsBaseUrl + "/{itemId}",
                            HttpMethod.GET,
                            new HttpEntity<Void>(new HttpHeaders()),
                            new ParameterizedTypeReference<ResponseResult<ItemEntity>>() {},
                            uriVariables);

            ItemEntity item = Objects.requireNonNull(entity.getBody()).getData();
            if (item == null) {
                throw new ItemNotFoundException(
                        MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
            }

            return item.getInStock();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ItemNotFoundException(
                        MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
            }
            throw e;
        }
    }

    public ItemInventoryEntity saveItemInStock(Long itemId, Integer inStock) throws ParameterException {
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(inStock, AssetMessages.IN_STOCK_CANNOT_BE_NULL);
        ParameterValidator.requireNonNegative(inStock, MessageFormat.format(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, inStock));

        return itemInventoryRepository.save(new ItemInventoryEntity(itemId, inStock));
    }

    public Integer getInStockByItemId(Long itemId) throws ParameterException {
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);

        return itemInventoryRepository.findInStockByItemId(itemId);
    }

    public void removeItemInventoryByItemId(Long itemId) throws ParameterException {
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);

        if (itemInventoryExistById(itemId)){
            itemInventoryRepository.deleteById(itemId);
        }
    }

    private boolean itemInventoryExistById(Long itemId) {
        return itemInventoryRepository.existsById(itemId);
    }
}
