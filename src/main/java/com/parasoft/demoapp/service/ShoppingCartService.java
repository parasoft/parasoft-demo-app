package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.CartItemNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.repository.industry.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemInventoryService itemInventoryService;

    public CartItemEntity addCartItemInShoppingCart(Long userId, Long itemId, Integer quantity)
            throws ParameterException, ItemNotFoundException {

        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(quantity, AssetMessages.QUANTITY_CANNOT_BE_NULL);
        ParameterValidator.requireNonZero(quantity, AssetMessages.QUANTITY_CANNOT_BE_ZERO);
        ParameterValidator.requireNonNegative(quantity, AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO);

        synchronized (this){
            ItemEntity item = itemService.getItemById(itemId);

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
            cartItem.setRealInStock(itemInventoryService.getInStockByItemId(itemId));

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

        ItemEntity item = itemService.getItemById(itemId);
        CartItemEntity cartItem = shoppingCartRepository.findByUserIdAndItemId(userId, itemId);
        if (cartItem == null) {
            throw new CartItemNotFoundException(MessageFormat.format(
                    AssetMessages.THERE_IS_NO_CART_ITEM_CORRESPONDING_TO, itemId));
        }
        
        if (item.getInStock() < newQuantity) {
            throw new ParameterException(AssetMessages.INCLUDES_SHOPPING_CART_IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT);
        }

        cartItem.setQuantity(newQuantity);
        cartItem = shoppingCartRepository.save(cartItem);
        
        cartItem.setRealInStock(itemInventoryService.getInStockByItemId(itemId));

        return cartItem;
    }

    public List<CartItemEntity> getCartItemsByUserId(Long userId) throws ParameterException {
        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);

        List<CartItemEntity> cartItemEntities = shoppingCartRepository.findAllByUserId(userId);
        for(CartItemEntity cartItemEntity : cartItemEntities){
            cartItemEntity.setRealInStock(itemInventoryService.getInStockByItemId(cartItemEntity.getItemId()));
        }

        return cartItemEntities;
    }

    public CartItemEntity getCartItemByUserIdAndItemId(Long userId, Long itemId)
            throws ParameterException, ItemNotFoundException {
        ParameterValidator.requireNonNull(userId, AssetMessages.USER_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);

        ItemEntity item = itemService.getItemById(itemId);
        CartItemEntity cartItem = shoppingCartRepository.findByUserIdAndItemId(userId, itemId);

        if (null == cartItem) {
            cartItem = new CartItemEntity(userId, item, 0);
        }
        cartItem.setRealInStock(itemInventoryService.getInStockByItemId(itemId));

        return cartItem;
    }

}
