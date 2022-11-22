package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.ShoppingCartRepository;

/**
 * Test class for ShoppingCartService
 *
 * @see ShoppingCartService
 */
public class ShoppingCartServiceTest {

	@InjectMocks
	ShoppingCartService underTest;

	@Mock
	ShoppingCartRepository shoppingCartRepository;

	@Mock
	ItemService itemService;

	@Mock
    ItemInventoryService itemInventoryService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with normal
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_normal1() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		String name = "name";
		String description = "description";
		String image = "imagePath";
		Integer inStock = 20;
		Integer quantity = 10;
		ItemEntity item = mock(ItemEntity.class);
		doReturn(item).when(itemService).getItemById(anyLong());
		doReturn(itemId).when(item).getId();
		doReturn(image).when(item).getImage();
		doReturn(name).when(item).getName();
		doReturn(description).when(item).getDescription();
		doReturn(inStock).when(item).getInStock();

		CartItemEntity saveResult = new CartItemEntity(userId, item, quantity);
		doReturn(false).when(shoppingCartRepository).existsByItemIdAndUserId(anyLong(), anyLong());
		doReturn(saveResult).when(shoppingCartRepository).save((CartItemEntity) any());

		// When
		CartItemEntity result = underTest.addCartItemInShoppingCart(userId, itemId, quantity);

		// Then
		assertNotNull(result);
		assertEquals(userId, result.getUserId());
		assertEquals(itemId, result.getItemId());
		assertEquals(name, result.getName());
		assertEquals(description, result.getDescription());
		assertEquals(image, result.getImage());
		assertEquals(quantity, result.getQuantity());
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer), for validating realInStock property.
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_normal2() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		String name = "name";
		String description = "description";
		String image = "imagePath";
		Integer inStock = 20;
		Integer quantity = 10;
		ItemEntity item = mock(ItemEntity.class);
		doReturn(item).when(itemService).getItemById(anyLong());
		doReturn(itemId).when(item).getId();
		doReturn(image).when(item).getImage();
		doReturn(name).when(item).getName();
		doReturn(description).when(item).getDescription();
		doReturn(inStock).when(item).getInStock();

		CartItemEntity saveResult = new CartItemEntity(userId, item, quantity);
		doReturn(false).when(shoppingCartRepository).existsByItemIdAndUserId(anyLong(), anyLong());
		doReturn(saveResult).when(shoppingCartRepository).save((CartItemEntity) any());
		doReturn(inStock).when(itemInventoryService).getInStockByItemId(anyLong());

		// When
		CartItemEntity result = underTest.addCartItemInShoppingCart(userId, itemId, quantity);

		// Then
		assertNotNull(result);

		assertEquals(inStock, result.getRealInStock());
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with normal cartItemExists
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_normal_cartItemExists() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer inStock = 20;
		Integer quantity = 10;
		Integer newQuantity = 5;
		String name = "name";
		String description = "description";
		String image = "imagePath";
		ItemEntity item = mock(ItemEntity.class);
		doReturn(item).when(itemService).getItemById(anyLong());
		doReturn(itemId).when(item).getId();
		doReturn(image).when(item).getImage();
		doReturn(name).when(item).getName();
		doReturn(description).when(item).getDescription();
		doReturn(inStock).when(item).getInStock();

		CartItemEntity cartItem = new CartItemEntity(userId, item, quantity);
		CartItemEntity newCartItem = new CartItemEntity(userId, item, quantity+newQuantity);
		doReturn(true).when(shoppingCartRepository).existsByItemIdAndUserId(anyLong(), anyLong());
		doReturn(cartItem).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());
		doReturn(item).when(itemService).getItemById(anyLong());
		doReturn(inStock).when(item).getInStock();
		doReturn(itemId).when(item).getId();
		doReturn(newCartItem).when(shoppingCartRepository).save((CartItemEntity) any());

		// When
		CartItemEntity result = underTest.addCartItemInShoppingCart(userId, itemId, newQuantity);

		System.out.println(result);

		// Then
		assertNotNull(result);
		assertEquals(userId, result.getUserId());
		assertEquals(itemId, result.getItemId());
		assertEquals((Integer) (quantity + newQuantity), result.getQuantity());
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with NullUserIdException
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_nullUserId() throws Throwable {
		// Given
		Long userId = null;
		Long itemId = 1L;
		Integer quantity = 10;

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, quantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.USER_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with NullItemIdException
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_nullItemId() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = null;
		Integer quantity = 10;

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, quantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.ITEM_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with NullItemIdException
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_inventoryNotExists() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer quantity = 10;
		ItemEntity item = new ItemEntity();
		item.setId(itemId);
		item.setInStock(null); // test point
		when(itemService.getItemById(itemId)).thenReturn(item);

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, quantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(MessageFormat.format(AssetMessages.INVENTORY_NOT_FOUND_WITH_ITEM_ID, itemId), message);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with NullQuantityException
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_nullQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer quantity = null;

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, quantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.QUANTITY_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with ZeroQuantityException
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_zeroQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer quantity = 0;

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, quantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.QUANTITY_CANNOT_BE_ZERO, message);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with NegativeQuantityException
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_negativeQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer quantity = -10;

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, quantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO, message);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with OverQuantityException
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_overQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		ItemEntity item = mock(ItemEntity.class);
		Long itemId = 1L;
		Integer inStock = 10;
		Integer quantity = 20;
		doReturn(item).when(itemService).getItemById(itemId);
		doReturn(inStock).when(item).getInStock();

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, quantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.INCLUDES_SHOPPING_CART_IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT, message);
	}

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) with OverQuantityException for exists cart item
	 *
	 * @see ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_exception_existsCartItem_overQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		ItemEntity item = mock(ItemEntity.class);
		Long itemId = 1L;
		Integer inStock = 20;
		Integer quantity = 10;
		Integer newQuantity = 15;
		CartItemEntity CartItem = new CartItemEntity(userId, item, quantity);
		doReturn(true).when(shoppingCartRepository).existsByItemIdAndUserId(anyLong(), anyLong());
		doReturn(CartItem).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());
		doReturn(item).when(itemService).getItemById(anyLong());
		doReturn(inStock).when(item).getInStock();
		doReturn(itemId).when(item).getId();

		// When
		String message = "";
		try {
			underTest.addCartItemInShoppingCart(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.INCLUDES_SHOPPING_CART_IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT, message);
	}

	/**
	 * Test for removeCartItemByUserIdAndItemId(Long) with normal
	 *
	 * @see ShoppingCartService#removeCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testRemoveCartItemByUserIdAndItemId_normal() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		doReturn(true).when(shoppingCartRepository).existsByItemId(anyLong());
		doNothing().when(shoppingCartRepository).deleteByUserIdAndItemId(anyLong(), anyLong());

		// When
		underTest.removeCartItemByUserIdAndItemId(userId, itemId);

	}

	/**
	 * Test for removeCartItemByUserIdAndItemId(Long) with NullItemIdException
	 *
	 * @see ShoppingCartService#removeCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testRemoveCartItemByUserIdAndItemId_exception_nullUserId() throws Throwable {
		// Given
		Long userId = null;
		Long itemId = 1L;

		// When
		String message = "";
		try {
			underTest.removeCartItemByUserIdAndItemId(userId, itemId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.USER_ID_CANNOT_BE_NULL, itemId), message);
	}

	/**
	 * Test for removeCartItemByUserIdAndItemId(Long) with NullItemIdException
	 *
	 * @see ShoppingCartService#removeCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testRemoveCartItemByUserIdAndItemId_exception_nullItemId() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = null;

		// When
		String message = "";
		try {
			underTest.removeCartItemByUserIdAndItemId(userId, itemId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_ID_CANNOT_BE_NULL, itemId), message);
	}

	/**
	 * Test for removeCartItemByUserIdAndItemId(Long) with NoCartItemException
	 *
	 * @see ShoppingCartService#removeCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testRemoveCartItemByUserIdAndItemId_exception_noCartItem() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		doReturn(false).when(shoppingCartRepository).existsById(anyLong());

		// When
		String message = "";
		try {
			underTest.removeCartItemByUserIdAndItemId(userId, itemId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.THIS_ITEM_IS_NOT_IN_THE_SHOPPING_CART, itemId), message);
	}

	/**
	 * Test for clearShoppingCart(Long) with normal
	 *
	 * @see ShoppingCartService#clearShoppingCart(Long)
	 */
	@Test
	public void testClearShoppingCart_normal() throws Throwable {
		// Given
		Long userId = 1L;
		doReturn(true).when(shoppingCartRepository).existsByUserId(anyLong());
		doNothing().when(shoppingCartRepository).deleteByUserId(anyLong());

		// When
		underTest.clearShoppingCart(userId);
	}

	/**
	 * Test for clearShoppingCart(Long) with NullUserIdException
	 *
	 * @see ShoppingCartService#clearShoppingCart(Long)
	 */
	@Test
	public void testClearShoppingCart_exception_nullUserId() throws Throwable {
		// Given
		Long userId = null;

		// When
		String message = "";
		try {
			underTest.clearShoppingCart(userId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.USER_ID_CANNOT_BE_NULL, userId), message);
	}

	/**
	 * Test for clearShoppingCart(Long) with NoCartItemsException
	 *
	 * @see ShoppingCartService#clearShoppingCart(Long)
	 */
	@Test
	public void testClearShoppingCart_exception_noCartItems() throws Throwable {
		// Given
		Long userId = 1L;
		doReturn(false).when(shoppingCartRepository).existsByUserId(anyLong());

		// When
		String message = "";
		try {
			underTest.clearShoppingCart(userId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.NO_CART_ITEMS, message);
	}

	/**
	 * Test for updateCartItemQuantity with normal
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_normal() throws Throwable {
		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = 1L;
		Integer inStock = 20;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();
		ItemEntity item = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);

		Long itemId = 1L;
		Long userId = 1L;
		Integer quantity = 1;
		Integer newQuantity = 10;

		CartItemEntity cartItem = new CartItemEntity(userId, item, quantity);
		CartItemEntity newCartItem = new CartItemEntity(userId, item, newQuantity);
		doReturn(cartItem).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());
		doReturn(newCartItem).when(shoppingCartRepository).save((CartItemEntity) any());
		doReturn(item).when(itemService).getItemById(anyLong());

		// When
		CartItemEntity result = underTest.updateCartItemQuantity(userId, itemId, newQuantity);

		// Then
		assertNotNull(result);
		assertEquals(newQuantity, result.getQuantity());
	}

	/**
	 * Test for updateCartItemQuantity(Long, Long, Integer), for validating realInStock property.
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_normal2() throws Throwable {
		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = 1L;
		Integer inStock = 20;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();
		ItemEntity item = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);

		Long itemId = 1L;
		Long userId = 1L;
		Integer quantity = 1;
		Integer newQuantity = 10;

		CartItemEntity cartItem = new CartItemEntity(userId, item, quantity);
		CartItemEntity newCartItem = new CartItemEntity(userId, item, newQuantity);
		doReturn(cartItem).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());
		doReturn(newCartItem).when(shoppingCartRepository).save((CartItemEntity) any());
		doReturn(item).when(itemService).getItemById(anyLong());
		doReturn(newQuantity).when(itemInventoryService).getInStockByItemId(anyLong());

		// When
		CartItemEntity result = underTest.updateCartItemQuantity(userId, itemId, newQuantity);

		// Then
		assertNotNull(result);
		assertEquals(newQuantity, result.getRealInStock());
	}

	/**
	 * Test for updateCartItemQuantity with CartItemNotFoundException
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_exception_cartItemNotFound() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer newQuantity = 10;
		doReturn(null).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());

		// When
		String message = "";
		try {
			underTest.updateCartItemQuantity(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.THERE_IS_NO_CART_ITEM_CORRESPONDING_TO, itemId), message);
	}

	/**
	 * Test for updateCartItemQuantity with NullItemIdException
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_exception_nullUserId() throws Throwable {
		// Given
		Long userId = null;
		Long itemId = 1L;
		Integer newQuantity = 10;

		// When
		String message = "";
		try {
			underTest.updateCartItemQuantity(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.USER_ID_CANNOT_BE_NULL, itemId), message);
	}

	/**
	 * Test for updateCartItemQuantity with NullItemIdException
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_exception_nullItemId() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = null;
		Integer newQuantity = 10;

		// When
		String message = "";
		try {
			underTest.updateCartItemQuantity(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_ID_CANNOT_BE_NULL, itemId), message);
	}

	/**
	 * Test for updateCartItemQuantity with NullQuantityException
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_exception_nullQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer newQuantity = null;

		// When
		String message = "";
		try {
			underTest.updateCartItemQuantity(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.QUANTITY_CANNOT_BE_NULL, itemId), message);
	}

	/**
	 * Test for updateCartItemQuantity with NegativeQuantityException
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_exception_negativeQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer newQuantity = -1;

		// When
		String message = "";
		try {
			underTest.updateCartItemQuantity(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO, itemId), message);
	}

	/**
	 * Test for updateCartItemQuantity with zero quantity.
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_exception_zeroQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer newQuantity = 0;

		// When
		String message = "";
		try {
			underTest.updateCartItemQuantity(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.QUANTITY_CANNOT_BE_A_NEGATIVE_NUMBER_OR_ZERO, itemId), message);
	}


	/**
	 * Test for updateCartItemQuantity with OverQuantityException
	 *
	 * @see ShoppingCartService#updateCartItemQuantity(Long, Long, Integer)
	 */
	@Test
	public void testUpdateCartItemQuantity_exception_overQuantity() throws Throwable {
		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = 1L;
		Integer inStock = 20;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();
		ItemEntity item = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);

		Long itemId = 1L;
		Long userId = 1L;
		Integer quantity = 1;
		Integer newQuantity = 100;

		CartItemEntity cartItem = new CartItemEntity(userId, item, quantity);
		doReturn(cartItem).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());
		doReturn(item).when(itemService).getItemById(anyLong());

		// When
		String message = "";
		try {
			underTest.updateCartItemQuantity(userId, itemId, newQuantity);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.IN_STOCK_OF_CART_ITEM_IS_INSUFFICIENT, message);
	}

	/**
	 * Test for getCartItemsByUserId(Long) with normal
	 *
	 * @see ShoppingCartService#getCartItemsByUserId(Long)
	 */
	@Test
	public void testGetCartItemsByUserId_normal1() throws Throwable {
		// Given
		Long userId = 1L;
		CartItemEntity shoppingCart = mock(CartItemEntity.class);
		List<CartItemEntity> findResult = new ArrayList<>();
		findResult.add(shoppingCart);

		doReturn(findResult).when(shoppingCartRepository).findAllByUserId(anyLong());

		// When
		List<CartItemEntity> result = underTest.getCartItemsByUserId(userId);

		// Then
		assertNotNull(result);
		Assertions.assertEquals(1, result.size());
	}

	/**
	 * Test for getCartItemsByUserId(Long), for validating realInStock property.
	 *
	 * @see ShoppingCartService#getCartItemsByUserId(Long)
	 */
	@Test
	public void testGetCartItemsByUserId_normal2() throws Throwable {
		// Given
		Long itemId = 2L;
		ItemEntity item = mock(ItemEntity.class);
		doReturn(itemId).when(item).getId();

		Long userId = 1L;
		Integer quantity = 10;
		CartItemEntity shoppingCart = new CartItemEntity(userId, item, quantity);
		List<CartItemEntity> findResult = new ArrayList<>();
		findResult.add(shoppingCart);

		doReturn(findResult).when(shoppingCartRepository).findAllByUserId(anyLong());
		doReturn(quantity).when(itemInventoryService).getInStockByItemId(anyLong());

		// When
		List<CartItemEntity> result = underTest.getCartItemsByUserId(userId);

		// Then
		assertNotNull(result);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(quantity, result.get(0).getRealInStock());
	}

	/**
	 * Test for getCartItemsByUserId(Long) with NullUserIdException
	 *
	 * @see ShoppingCartService#getCartItemsByUserId(Long)
	 */
	@Test
	public void testGetCartItemsByUserId_exception_nullUserId() throws Throwable {
		// Given
		Long userId = null;

		// When
		String message = "";
		try {
			underTest.getCartItemsByUserId(userId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.USER_ID_CANNOT_BE_NULL, userId), message);
	}

	/**
	 * Test for getCartItemByItemId(Long) with normal
	 *
	 * @see ShoppingCartService#getCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testGetCartItemByUserIdAndItemId_normal1() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer quantity = 2;
		CartItemEntity cartItem = new CartItemEntity(userId, mock(ItemEntity.class), quantity);
		doReturn(cartItem).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());

		// When
		CartItemEntity result = underTest.getCartItemByUserIdAndItemId(userId, itemId);

		// Then
		assertNotNull(result);
		assertEquals(userId, result.getUserId());
		assertEquals(quantity, result.getQuantity());
	}

	/**
	 * Test for getCartItemByItemId(Long), for validating realInStock property.
	 *
	 * @see ShoppingCartService#getCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testGetCartItemByUserIdAndItemId_normal2() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer quantity = 10;
		CartItemEntity cartItem = new CartItemEntity(userId, mock(ItemEntity.class), quantity);
		doReturn(cartItem).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());
		doReturn(quantity).when(itemInventoryService).getInStockByItemId(anyLong());

		// When
		CartItemEntity result = underTest.getCartItemByUserIdAndItemId(userId, itemId);

		// Then
		assertNotNull(result);
		Assertions.assertEquals(quantity, result.getRealInStock());
	}

	/**
	 * Test for getCartItemByItemId(Long, Long), when item not in the cart.
	 *
	 * @see ShoppingCartService#getCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testGetCartItemByUserIdAndItemId_normal3() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = 1L;
		Integer quantity = 0;
		Integer realInStock = 10;
		ItemEntity item = mock(ItemEntity.class);
		doReturn(itemId).when(item).getId();
		doReturn(item).when(itemService).getItemById(itemId);
		doReturn(null).when(shoppingCartRepository).findByUserIdAndItemId(anyLong(), anyLong());
		doReturn(realInStock).when(itemInventoryService).getInStockByItemId(anyLong());

		// When
		CartItemEntity result = underTest.getCartItemByUserIdAndItemId(userId, itemId);

		// Then
		assertNotNull(result);
		assertEquals(null, result.getId());
		assertEquals(userId, result.getUserId());
		assertEquals(quantity, result.getQuantity());
		assertEquals(itemId, result.getItemId());
		assertEquals(realInStock, result.getRealInStock());
	}

	/**
	 * Test for getCartItemByItemId(Long) with NullItemIdException
	 *
	 * @see ShoppingCartService#getCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testGetCartItemByUserIdAndItemId_exception_nullUserId() throws Throwable {
		// Given
		Long userId = null;
		Long itemId = 2L;

		// When
		String message = "";
		try {
			underTest.getCartItemByUserIdAndItemId(userId, itemId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.USER_ID_CANNOT_BE_NULL, itemId), message);
	}

	/**
	 * Test for getCartItemByItemId(Long) with NullItemIdException
	 *
	 * @see ShoppingCartService#getCartItemByUserIdAndItemId(Long, Long)
	 */
	@Test
	public void testGetCartItemByUserIdAndItemId_exception_nullItemId() throws Throwable {
		// Given
		Long userId = 1L;
		Long itemId = null;

		// When
		String message = "";
		try {
			underTest.getCartItemByUserIdAndItemId(userId, itemId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_ID_CANNOT_BE_NULL, itemId), message);
	}
}