/**
 *
 */
package com.parasoft.demoapp.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.parasoft.demoapp.dto.OrderDTO;
import com.parasoft.demoapp.dto.OrderStatusDTO;
import com.parasoft.demoapp.exception.CartItemNotFoundException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.OrderNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.DemoBugService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.OrderMQService;
import com.parasoft.demoapp.service.OrderService;

/**
 * Test for OrderController
 *
 * @see OrderController
 */
public class OrderControllerTest {

	@InjectMocks
	OrderController underTest;

	@Mock
	OrderService orderService;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	@Mock
	OrderMQService orderMQService;

	@Mock
	DemoBugService demoBugService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for addNewOrder(Authentication, OrderDTO)
	 *
	 * @see OrderController#addNewOrder(Authentication, OrderDTO)
	 */
	@Test
	public void testAddNewOrder_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();

		OrderDTO orderDto = new OrderDTO();
		orderDto.setRegion(RegionType.JAPAN);
		orderDto.setLocation("JAPAN 82.8628° S, 135.0000° E");
		orderDto.setReceiverId("345-6789-21");
		orderDto.setEventId("45833-ORG-7834");
		orderDto.setEventNumber("55-444-33-22");
		OrderEntity order = mock(OrderEntity.class);

		doReturn(order).when(orderService).addNewOrderSynchronized(anyLong(), (RegionType) any(), anyString(), anyString(),
				anyString(), anyString());

		// When
		ResponseResult<OrderEntity> result = underTest.addNewOrder(auth, orderDto);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for addNewOrder(Authentication, OrderDTO) with ParameterException
	 *
	 * @see OrderController#addNewOrder(Authentication, OrderDTO)
	 */
	@Test(expected = ParameterException.class)
	public void testAddNewOrder_exception_ParameterException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();

		OrderDTO orderDto = mock(OrderDTO.class);
		when(orderService.addNewOrderSynchronized(anyLong(), (RegionType) any(), any(), any(), any(), any()))
				.thenThrow(ParameterException.class);

		// When
		ResponseResult<OrderEntity> result = underTest.addNewOrder(auth, orderDto);
		System.out.println(result.getMessage());
	}

	/**
	 * Test for addNewOrder(Authentication, OrderDTO) with ItemNotFoundException
	 *
	 * @see OrderController#addNewOrder(Authentication, OrderDTO)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testAddNewOrder_exception_ItemNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();

		OrderDTO orderDto = mock(OrderDTO.class);
		doThrow(ItemNotFoundException.class).when(orderService).addNewOrderSynchronized(anyLong(), (RegionType) any(), any(), any(),
				any(), any());

		// When
		underTest.addNewOrder(auth, orderDto);
	}

	/**
	 * Test for addNewOrder(Authentication, OrderDTO) with CartItemNotFoundException
	 *
	 * @see OrderController#addNewOrder(Authentication, OrderDTO)
	 */
	@Test(expected = CartItemNotFoundException.class)
	public void testAddNewOrder_exception_CartItemNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();

		OrderDTO orderDto = mock(OrderDTO.class);
		doThrow(CartItemNotFoundException.class).when(orderService).addNewOrderSynchronized(anyLong(), (RegionType) any(), any(),
				any(), any(), any());

		// When
		underTest.addNewOrder(auth, orderDto);
	}

	/**
	 * Test for getOrderByOrderNumber(String)
	 *
	 * @see OrderController#getOrderByOrderNumber(String)
	 */
	@Test
	public void testGetOrderByOrderNumber_normal() throws Throwable {
		// Given
		String orderNumber = "23-456-010";
		OrderEntity order = mock(OrderEntity.class);
		doReturn(order).when(orderService).getOrderByOrderNumber(anyString());
		doNothing().when(demoBugService).introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(any(OrderEntity.class));
		// When
		ResponseResult<OrderEntity> result = underTest.getOrderByOrderNumber(orderNumber);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for getOrderByOrderNumber(String) with ParameterException
	 *
	 * @see OrderController#getOrderByOrderNumber(String)
	 */
	@Test(expected = ParameterException.class)
	public void testGetOrderByOrderNumber_exception_ParameterException() throws Throwable {
		// Given
		String orderNumber = "";
		doThrow(ParameterException.class).when(orderService).getOrderByOrderNumber(anyString());

		// When
		underTest.getOrderByOrderNumber(orderNumber);
	}

	/**
	 * Test for getOrderByOrderNumber(String) with OrderNotFoundExcption
	 *
	 * @see OrderController#getOrderByOrderNumber(String)
	 */
	@Test(expected = OrderNotFoundException.class)
	public void testGetOrderByOrderNumber_exception_OrderNotFoundException() throws Throwable {
		// Given
		String orderNumber = "23-456-010";
		doThrow(OrderNotFoundException.class).when(orderService).getOrderByOrderNumber(anyString());

		// When
		underTest.getOrderByOrderNumber(orderNumber);
	}

	/**
	 * Test for updateStatusOfOrderByOrderNumber(Authentication, OrderStatusDTO, String)
	 *
	 * @see OrderController#updateStatusOfOrderByOrderNumber(Authentication, OrderStatusDTO, String)
	 */
	@Test
	public void testUpdateStatusOfOrderByOrderNumber_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		RoleEntity roleEntity = new RoleEntity();
		String userRoleName = RoleType.ROLE_PURCHASER.toString();
		roleEntity.setName(userRoleName);
		UserEntity userEntity = new UserEntity();
		userEntity.setRole(roleEntity);
		doReturn(userEntity).when(auth).getPrincipal();
		String orderNumber = "23-456-010";
		OrderStatus newOrderStatus = OrderStatus.APPROVED;
		OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
		orderStatusDTO.setStatus(newOrderStatus);
		OrderEntity order = mock(OrderEntity.class);
		doReturn(order).when(orderService).updateOrderByOrderNumberSynchronized(
				anyString(), anyString(), (OrderStatus) any(), anyBoolean(), anyBoolean(), nullable(String.class), nullable(String.class), any(boolean.class));

		// When
		ResponseResult<OrderEntity> result = underTest.updateStatusOfOrderByOrderNumber(auth, orderStatusDTO, orderNumber);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for updateStatusOfOrderByOrderNumber(Authentication, OrderStatusDTO, String) with ParameterException
	 *
	 * @see OrderController#updateStatusOfOrderByOrderNumber(Authentication, OrderStatusDTO, String)
	 */
	@Test(expected = ParameterException.class)
	public void testUpdateStatusOfOrderByOrderNumber_exception_ParameterException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		RoleEntity roleEntity = new RoleEntity();
		String userRoleName = RoleType.ROLE_PURCHASER.toString();
		roleEntity.setName(userRoleName);
		UserEntity userEntity = new UserEntity();
		userEntity.setRole(roleEntity);
		doReturn(userEntity).when(auth).getPrincipal();
		String orderNumber = "23-456-010";
		OrderStatus newOrderStatus = OrderStatus.APPROVED;
		OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
		orderStatusDTO.setStatus(newOrderStatus);
		doThrow(ParameterException.class).when(orderService).updateOrderByOrderNumberSynchronized(anyString(),
				anyString(), (OrderStatus) any(), anyBoolean(), anyBoolean(), nullable(String.class), nullable(String.class), any(boolean.class));

		// When
		underTest.updateStatusOfOrderByOrderNumber(auth, orderStatusDTO, orderNumber);
	}

	/**
	 * Test for updateStatusOfOrderByOrderNumber(Authentication, OrderStatusDTO, String) with OrderNotFoundException
	 *
	 * @see OrderController#updateStatusOfOrderByOrderNumber(Authentication, OrderStatusDTO, String)
	 */
	@Test(expected = OrderNotFoundException.class)
	public void testUpdateStatusOfOrderByOrderNumber_exception_OrderNotFoundException() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		RoleEntity roleEntity = new RoleEntity();
		String userRoleName = RoleType.ROLE_PURCHASER.toString();
		roleEntity.setName(userRoleName);
		UserEntity userEntity = new UserEntity();
		userEntity.setRole(roleEntity);
		doReturn(userEntity).when(auth).getPrincipal();
		String orderNumber = "23-456-010";
		OrderStatus newOrderStatus = OrderStatus.APPROVED;
		OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
		orderStatusDTO.setStatus(newOrderStatus);
		doThrow(OrderNotFoundException.class).when(orderService).updateOrderByOrderNumberSynchronized(anyString(),
				anyString(), (OrderStatus) any(), anyBoolean(), anyBoolean(), nullable(String.class), nullable(String.class), any(boolean.class));

		// When
		underTest.updateStatusOfOrderByOrderNumber(auth, orderStatusDTO, orderNumber);
	}

	/**
	 * Test for showAllOrders(Authentication)
	 *
	 * @see OrderController#showAllOrders(Authentication)
	 */
	@Test
	public void testShowAllOrders_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();

		// When
		ResponseResult<List<OrderEntity>> result = underTest.showAllOrders(auth);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}


	/**
	 * Test for showAllOrders(Authentication, Pageable)
	 *
	 * @see com.parasoft.demoapp.controller.OrderController#showAllOrders(Authentication, Pageable)
	 */
	@Test
	public void testShowAllOrders_withPageable_normal() throws Throwable {
		// Given
		Authentication auth = mock(Authentication.class);
		UserEntity userEntity = new UserEntity();
		long userId = 1L;
		userEntity.setId(userId);
		doReturn(userEntity).when(auth).getPrincipal();

		Pageable pageable = Pageable.unpaged();
		List<OrderEntity> content = new ArrayList<>();
		content.add(new OrderEntity());
		int totalElement = 2;
		Page<OrderEntity> page = new PageImpl<>(content, pageable, totalElement);
		doReturn(page).when(orderService).getAllOrders(nullable(Long.class), nullable(String.class),
				nullable(Pageable.class));
		doNothing().when(demoBugService).introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(any(List.class));
		doReturn(pageable).when(demoBugService).introduceBugWithReverseOrdersIfNeeded(any(Pageable.class));

		// When
		ResponseResult<PageInfo<OrderEntity>> result = underTest.showAllOrders(auth, pageable);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(content, result.getData().getContent());
		assertEquals(totalElement, result.getData().getTotalElements());
	}

	/**
	 * Test for showAllOrders(Authentication, Pageable)
	 *
	 * @see com.parasoft.demoapp.controller.OrderController#showAllOrders(Authentication, Pageable)
	 */
	@Test(expected = ParameterException.class)
	public void testShowAllOrders_withPageable_parameterException() throws Throwable {
		// Given
		doThrow(ParameterException.class).when(orderService).getAllOrders(nullable(Long.class), nullable(String.class),
				nullable(Pageable.class));

		// When
		Authentication auth = mock(Authentication.class);
		Pageable pageable = Pageable.unpaged();
		underTest.showAllOrders(auth, pageable);
	}

}