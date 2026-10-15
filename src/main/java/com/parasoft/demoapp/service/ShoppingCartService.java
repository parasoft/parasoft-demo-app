package com.parasoft.demoapp.service;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.exception.CartItemNotFoundException;
import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.graphql.RestTemplateUtil;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.repository.industry.ShoppingCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ItemInventoryService itemInventoryService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestEndpointService restEndpointService;

    @Autowired
    private HttpServletRequest httpRequest;

    public CartItemEntity addCartItemInShoppingCart(Long userId, Long itemId, Integer quantity)
            throws ParameterException, ItemNotFoundException, InventoryNotFoundException {

        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(quantity, AssetMessages.QUANTITY_CANNOT_BE_NULL);
        ParameterValidator.requireNonZero(quantity, AssetMessages.QUANTITY_CANNOT_BE_ZERO);
        ParameterValidator.requireNonNegative(quantity, AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO);

        synchronized (this){
            ItemEntity item = getItemById(itemId);
            if(item.getInStock() == null) {
                throw new InventoryNotFoundException(MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, itemId));
            }

            if (quantity > item.getInStock()) {
                throw new ParameterException(AssetMessages.INCLUDES_SHOPPING_CART_IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT);
            }

            CartItemEntity cartItem = new CartItemEntity();
            if (shoppingCartRepository.existsByItemIdAndUserId(item.getId(), userId)) {
                cartItem = shoppingCartRepository.findByUserIdAndItemId(userId, itemId);
                if (cartItem.getQuantity() + quantity > item.getInStock()) {
                    throw new ParameterException(AssetMessages.INCLUDES_SHOPPING_CART_IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT);
                }
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            } else {
                cartItem.setUserId(userId);
                cartItem.setItemId(item.getId());
                cartItem.setName(item.getName());
                cartItem.setDescription(item.getDescription());
                cartItem.setImage(item.getImage());
                cartItem.setQuantity(quantity);
            }

            cartItem = shoppingCartRepository.save(cartItem);
            cartItem.setRealInStock(getInStockByItemId(itemId));

            return cartItem;
        }
    }

    @Transactional(value = "industryTransactionManager")
    public void removeCartItemByUserIdAndItemId(Long userId, Long itemId)
            throws ParameterException, CartItemNotFoundException {
        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);

        if (!shoppingCartRepository.existsByItemId(itemId)) {
            throw new CartItemNotFoundException(AssetMessages.THIS_ITEM_IS_NOT_IN_THE_SHOPPING_CART);
        }

        shoppingCartRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Transactional(value = "industryTransactionManager")
    public void clearShoppingCart(Long userId) throws ParameterException, CartItemNotFoundException {
        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);

        if (!shoppingCartRepository.existsByUserId(userId)){
            throw new CartItemNotFoundException(AssetMessages.NO_CART_ITEMS);
        }

        shoppingCartRepository.deleteByUserId(userId);
    }

    @Transactional(value = "industryTransactionManager")
    public CartItemEntity updateCartItemQuantity(Long userId, Long itemId, Integer newQuantity)
            throws ParameterException, ItemNotFoundException, CartItemNotFoundException {
        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(newQuantity, AssetMessages.QUANTITY_CANNOT_BE_NULL);
        ParameterValidator.requireNonNegative(newQuantity, AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO);
        ParameterValidator.requireNonZero(newQuantity, AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO);

        ItemEntity item = getItemById(itemId);
        CartItemEntity cartItem = shoppingCartRepository.findByUserIdAndItemId(userId, itemId);
        if (cartItem == null) {
            throw new CartItemNotFoundException(MessageFormat.format(
                    AssetMessages.THERE_IS_NO_CART_ITEM_CORRESPONDING_TO, itemId));
        }

        if (item.getInStock() < newQuantity) {
            throw new ParameterException(AssetMessages.INVENTORY_IS_NOT_ENOUGH);
        }

        cartItem.setQuantity(newQuantity);
        cartItem = shoppingCartRepository.save(cartItem);

        cartItem.setRealInStock(getInStockByItemId(itemId));

        return cartItem;
    }

    private ItemEntity getItemById(Long itemId) throws ItemNotFoundException {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("itemId", itemId);
            ResponseEntity<ResponseResult<ItemEntity>> entity =
                    restTemplate.exchange(restEndpointService.getItemsBaseUrl() + "/{itemId}",
                            HttpMethod.GET,
                            new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                            new ParameterizedTypeReference<ResponseResult<ItemEntity>>() {},
                            uriVariables);
            return Objects.requireNonNull(entity.getBody()).getData();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ItemNotFoundException(
                        MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
            }
            throw e;
        }
    }

    public List<CartItemEntity> getCartItemsByUserId(Long userId) throws ParameterException {
        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);

        List<CartItemEntity> cartItemEntities = shoppingCartRepository.findAllByUserId(userId);
        for(CartItemEntity cartItemEntity : cartItemEntities){
            try {
                cartItemEntity.setRealInStock(getInStockByItemId(cartItemEntity.getItemId()));
            } catch (ItemNotFoundException e) {
                log.error("Failed to obtain real in-stock for item id {} from the items REST API.", cartItemEntity.getItemId(), e);
            }
        }

        return cartItemEntities;
    }

    public CartItemEntity getCartItemByUserIdAndItemId(Long userId, Long itemId)
            throws ParameterException, ItemNotFoundException {
        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);

        ItemEntity item = getItemById(itemId);
        CartItemEntity cartItem = shoppingCartRepository.findByUserIdAndItemId(userId, itemId);

        if (null == cartItem) {
            cartItem = new CartItemEntity(userId, item, 0);
        }
        cartItem.setRealInStock(getInStockByItemId(itemId));

        return cartItem;
    }

    private Integer getInStockByItemId(Long itemId) throws ItemNotFoundException {
        return itemInventoryService.getInStockByItemIdFromRestApi(itemId);
    }

}
