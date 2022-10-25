package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.parasoft.demoapp.dto.InventoryOperation.DECREASE;
import static com.parasoft.demoapp.dto.InventoryOperation.INCREASE;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.FAIL;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.SUCCESS;

@Service
public class ItemInventoryService {

    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

    @Transactional
    public InventoryOperationResultMessageDTO receiveFromRequestQueue(InventoryOperationRequestMessageDTO requestMessage) {
        InventoryOperationResultMessageDTO resultMessage = new InventoryOperationResultMessageDTO();

        InventoryOperation operation = requestMessage.getOperation();
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
            System.out.println(requestMessage);
        }

        return null;
    }

    private InventoryOperationResultMessageDTO decrease(List<InventoryInfoDTO> requestedItems,
                                                        InventoryOperationResultMessageDTO resultMessage) {
        Pair<Boolean, String> statusInfoPair = checkItemStockBeforeDecrease(requestedItems);
        if (statusInfoPair.getFirst()) {
            requestedItems.forEach(this::decreaseItemStock);
            resultMessage.setStatus(SUCCESS);
        } else {
            resultMessage.setInfo(statusInfoPair.getSecond());
            resultMessage.setStatus(FAIL);
        }
        return resultMessage;
    }

    private void decreaseItemStock(InventoryInfoDTO requestedItem) {
        ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(requestedItem.getItemId());
        inventoryItem.setInStock(inventoryItem.getInStock() - requestedItem.getQuantity());
        itemInventoryRepository.save(inventoryItem);
    }

    private Pair<Boolean, String> checkItemStockBeforeDecrease(List<InventoryInfoDTO> requestedItems) {
        for (InventoryInfoDTO requestedItem : requestedItems) {
            Long itemId = requestedItem.getItemId();
            ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(itemId);
            if (inventoryItem == null) {
                return Pair.of(Boolean.FALSE, "Inventory item with id " + itemId + " doesn't exist.");
            } else if (inventoryItem.getInStock() < requestedItem.getQuantity()) {
                return Pair.of(Boolean.FALSE, "Inventory item with id " + itemId + " is out of stock.");
            }
        }
        return Pair.of(Boolean.TRUE, "");
    }
}
