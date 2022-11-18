/**
 *
 */
package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.ShoppingCartDTO;
import com.parasoft.demoapp.exception.CartItemNotFoundException;
import com.parasoft.demoapp.exception.InventoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.service.ShoppingCartService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for ShoppingCartController
 *
 * @see ShoppingCartController
 */
public class ShoppingCartControllerTest {

	@InjectMocks
	ShoppingCartController underTest;

	@Mock
	ShoppingCartService shoppingCartService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for addItemInCart(Authentication, ShoppingCartDTO)
	 *
	 * @see ShoppingCartController#addItemInCart(Authentication, ShoppingCartDTO)
	 */
	@Test
	public void testAddItemInCart_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();

		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		CartItemEntity getCartItemByCartIdResult = mock(CartItemEntity.class);
		doReturn(getCartItemByCartIdResult).when(shoppingCartService).addCartItemInShoppingCart(anyLong(), anyLong(), anyInt());

		// When
		ResponseResult<CartItemEntity> result = underTest.addItemInCart(auth, shoppingCartDto);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for addItemInCart(Authentication, ShoppingCartDTO) with ItemNotFoundException
	 *
	 * @see ShoppingCartController#addItemInCart(Authentication, ShoppingCartDTO)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testAddItemInCart_exception_ItemNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		doThrow(ItemNotFoundException.class).when(shoppingCartService).addCartItemInShoppingCart(anyLong(), anyLong(), anyInt());

		// When
		underTest.addItemInCart(auth, shoppingCartDto);
	}

	/**
	 * Test for addItemInCart(Authentication, ShoppingCartDTO) with ItemNotFoundException
	 *
	 * @see ShoppingCartController#addItemInCart(Authentication, ShoppingCartDTO)
	 */
	@Test(expected = InventoryNotFoundException.class)
	public void testAddItemInCart_exception_InventoryNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		doThrow(InventoryNotFoundException.class).when(shoppingCartService).addCartItemInShoppingCart(anyLong(), anyLong(), anyInt());

		// When
		underTest.addItemInCart(auth, shoppingCartDto);
	}

	/**
	 * Test for addItemInCart(Authentication, ShoppingCartDTO) with ParameterException
	 *
	 * @see ShoppingCartController#addItemInCart(Authentication, ShoppingCartDTO)
	 */
	@Test(expected = ParameterException.class)
	public void testAddItemInCart_exception_ParameterException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		doThrow(ParameterException.class).when(shoppingCartService).addCartItemInShoppingCart(anyLong(), anyLong(), anyInt());

		// When
		underTest.addItemInCart(auth, shoppingCartDto);
	}

	/**
	 * Test for getCartItems(Authentication)
	 *
	 * @see ShoppingCartController#getCartItems(Authentication)
	 */
	@Test
	public void testGetCartItems_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		CartItemEntity getCartItemByCartIdResult = mock(CartItemEntity.class);
		doReturn(getCartItemByCartIdResult).when(shoppingCartService).addCartItemInShoppingCart(anyLong(), anyLong(), anyInt());

		// When
		ResponseResult<List<CartItemEntity>> result = underTest.getCartItems(auth);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for getCartItem(Authentication, Long)
	 *
	 * @see ShoppingCartController#getCartItem(Authentication, Long)
	 */
	@Test
	public void testGetCartItemByItemId_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = 0L;
		CartItemEntity getCartItemByCartIdResult = mock(CartItemEntity.class);
		doReturn(getCartItemByCartIdResult).when(shoppingCartService).getCartItemByUserIdAndItemId(anyLong(),anyLong());

		// When
		ResponseResult<CartItemEntity> result = underTest.getCartItem(auth, itemId);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for getCartItem(Authentication, Long) with ParameterException
	 *
	 * @see ShoppingCartController#getCartItem(Authentication, Long)
	 */
	@Test(expected = ParameterException.class)
	public void testGetCartItemByItemId_exception_ParameterException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = -1L;
		doThrow(ParameterException.class).when(shoppingCartService).getCartItemByUserIdAndItemId(anyLong(), anyLong());

		// When
		underTest.getCartItem(auth, itemId);
	}

	/**
	 * Test for removeAllCartItems(Authentication)
	 *
	 * @see ShoppingCartController#removeAllCartItems(Authentication)
	 */
	@Test
	public void testRemoveAllCartItems_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		doNothing().when(shoppingCartService).clearShoppingCart(anyLong());

		// When
		ResponseResult<Boolean> result = underTest.removeAllCartItems(auth);

		// Then
		assertNotNull(result);
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for removeAllCartItems(Authentication) with CartItemNotFoundException
	 *
	 * @see ShoppingCartController#removeAllCartItems(Authentication)
	 */
	@Test(expected = CartItemNotFoundException.class)
	public void testRemoveAllCartItems_exception_CartItemNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		doThrow(CartItemNotFoundException.class).when(shoppingCartService).clearShoppingCart(anyLong());

		// When
		underTest.removeAllCartItems(auth);
	}

	/**
	 * Test for removeCartItem(Authentication. Long)
	 *
	 * @see ShoppingCartController#removeCartItem(Authentication, Long)
	 */
	@Test
	public void testRemoveCartItemByItemId_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = 0L;
		doNothing().when(shoppingCartService).removeCartItemByUserIdAndItemId(anyLong(), anyLong());

		// When
		ResponseResult<Long> result = underTest.removeCartItem(auth, itemId);

		// Then
		assertNotNull(result);
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for removeCartItem(Authentication. Long) with CartItemNotFoundException
	 *
	 * @see ShoppingCartController#removeCartItem(Authentication, Long)
	 */
	@Test(expected = CartItemNotFoundException.class)
	public void testRemoveCartItemByItemId_exception_CartItemNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = 0L;
		doThrow(CartItemNotFoundException.class).when(shoppingCartService).removeCartItemByUserIdAndItemId(anyLong(), anyLong());

		// When
		underTest.removeCartItem(auth, itemId);
	}

	/**
	 * Test for removeCartItem(Authentication. Long) with ParameterException
	 *
	 * @see ShoppingCartController#removeCartItem(Authentication, Long)
	 */
	@Test(expected = ParameterException.class)
	public void testRemoveCartItemByItemId_exception_ParameterException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = 0L;
		doThrow(ParameterException.class).when(shoppingCartService).removeCartItemByUserIdAndItemId(anyLong(), anyLong());

		// When
		underTest.removeCartItem(auth, itemId);
	}

	/**
	 * Test for updateCartItemQuantity(Authentication, Long, Integer)
	 *
	 * @see ShoppingCartController#updateCartItemQuantity(Authentication, Long, ShoppingCartDTO)
	 */
	@Test
	public void testUpdateCartItemQuantity_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = 0L;
		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		CartItemEntity updateCartItemResult = mock(CartItemEntity.class);
		doReturn(updateCartItemResult).when(shoppingCartService).updateCartItemQuantity(anyLong(), anyLong(), anyInt());
		// When
		ResponseResult<CartItemEntity> result = underTest.updateCartItemQuantity(auth, itemId, shoppingCartDto);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for updateCartItemQuantity(Authentication, Long, Integer) with ItemNotFoundException
	 *
	 * @see ShoppingCartController#updateCartItemQuantity(Authentication, Long, ShoppingCartDTO)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testUpdateCartItemQuantity_exception_CartItemNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = -1L;
		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		doThrow(ItemNotFoundException.class).when(shoppingCartService).updateCartItemQuantity(anyLong(), anyLong(), anyInt());

		// When
		underTest.updateCartItemQuantity(auth, itemId, shoppingCartDto);
	}

	/**
	 * Test for updateCartItemQuantity(Authentication, Long, Integer) with ItemNotFoundException
	 *
	 * @see ShoppingCartController#updateCartItemQuantity(Authentication, Long, ShoppingCartDTO)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testUpdateCartItemQuantity_exception_ItemNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = -1L;
		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		doThrow(ItemNotFoundException.class).when(shoppingCartService).updateCartItemQuantity(anyLong(), anyLong(), anyInt());

		// When
		underTest.updateCartItemQuantity(auth, itemId, shoppingCartDto);
	}

	/**
	 * Test for updateCartItemQuantity(Authentication, Long, Integer) with ParameterException
	 *
	 * @see ShoppingCartController#updateCartItemQuantity(Authentication, Long, ShoppingCartDTO)
	 */
	@Test(expected = ParameterException.class)
	public void testUpdateCartItemQuantity_exception_ParameterException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		doReturn(userEntity).when(auth).getPrincipal();
		Long itemId = 0L;
		ShoppingCartDTO shoppingCartDto = mock(ShoppingCartDTO.class);
		doThrow(ParameterException.class).when(shoppingCartService).updateCartItemQuantity(anyLong(), anyLong(), anyInt());

		// When
		underTest.updateCartItemQuantity(auth, itemId, shoppingCartDto);
	}


}