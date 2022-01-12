/**
 *
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.parasoft.demoapp.exception.LocationNotFoundException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderItemEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.OrderRepository;

/**
 * Test class for OrderService
 *
 * @see OrderService
 */
public class OrderServiceTest {

	@InjectMocks
	OrderService underTest;

	@Mock
	OrderRepository orderRepository;

	@Mock
	ShoppingCartService shoppingCartService;

	@Mock
	ItemService itemService;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	@Mock
	LocationService locationService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String)
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_normal() throws Throwable {
		// Given
		Long orderId = 10L;
		Long userId = 1L;
		Long itemId = 2L;
		String name = "name";
		String description = "description";
		String imagePath = "imagePath";
		RegionType region = RegionType.JAPAN;
		ItemEntity item = new ItemEntity(name, description, null, 20, imagePath, region, new Date());
		item.setId(itemId);

		int ItemQuantityInOrder = 1;
		OrderItemEntity orderItem = new OrderItemEntity(name, description, imagePath, ItemQuantityInOrder);
		List<OrderItemEntity> orderItems = new ArrayList<>();
		orderItems.add(orderItem);

		int inStock = 10;
		List<CartItemEntity> cartItems = new ArrayList<>();
		CartItemEntity cartItem = new CartItemEntity(userId, item, inStock);
		cartItems.add(cartItem);

		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";
		Date submissionDate = new Date();

		OrderEntity saveResult = new OrderEntity(userId, region, location, receiverId, eventId, eventNumber);
		saveResult.setId(orderId);
		saveResult.setStatus(OrderStatus.SUBMITTED);
		saveResult.setOrderItems(orderItems);
		saveResult.setSubmissionDate(submissionDate);
		saveResult.setOrderImage(imagePath);
		orderItem.setOrder(saveResult);

		LocationEntity locationEntity = new LocationEntity();
		locationEntity.setRegion(region);
		locationEntity.setLocationInfo(location);
		locationEntity.setLocationImage(imagePath);
		when(locationService.getLocationByRegion(any(RegionType.class))).thenReturn(locationEntity);

		when(shoppingCartService.getCartItemsByUserId(anyLong())).thenReturn(cartItems);
		when(itemService.getItemById(anyLong())).thenReturn(item);
		when(orderRepository.save((OrderEntity) any())).thenReturn(saveResult);

		// When
		OrderEntity result = underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);

		// Then
		assertNotNull(result);
		assertEquals(userId, result.getUserId());
		assertEquals(OrderStatus.SUBMITTED.getStatus(), result.getStatus().getStatus());
		assertEquals(1, result.getOrderItems().size());
		assertEquals(RegionType.JAPAN, result.getRegion());
		assertEquals(receiverId, result.getReceiverId());
		assertEquals(location, result.getLocation());
		assertEquals(eventId, result.getEventId());
		assertEquals(imagePath, result.getOrderImage());
		assertEquals(eventNumber, result.getEventNumber());
		assertEquals("23-456-010", result.getOrderNumber());
		assertEquals(submissionDate, result.getSubmissionDate());
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String)
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_regionTypeNotFound() throws Throwable {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";

		when(locationService.getLocationByRegion(any(RegionType.class))).thenThrow(LocationNotFoundException.class);

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(MessageFormat.format(OrderMessages.LOCATION_NOT_FOUND_FOR_REGION, region.toString()), message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String)
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_noCartItemsInCart() throws Throwable {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";

		LocationEntity locationEntity = mock(LocationEntity.class);
		when(locationService.getLocationByRegion(any(RegionType.class))).thenReturn(locationEntity);
		List<CartItemEntity> cartItems = new ArrayList<>();
		when(shoppingCartService.getCartItemsByUserId(anyLong())).thenReturn(cartItems);

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(AssetMessages.NO_CART_ITEMS, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with NullUserIdException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_nullUserIdException() {
		// Given
		Long userId = null;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.USER_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with NullRegionException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_nullRegionException() throws Exception {
		// Given
		Long userId = 1L;
		RegionType region = null;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.REGION_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with BlankReceiverIdException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_nullReceiverIdException() throws Exception {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = null;
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.RECEIVER_ID_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with BlankReceiverIdException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_blankReceiverIdException() throws Exception {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.RECEIVER_ID_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with BlankEventIdException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_nullEventIdException() throws Exception {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = null;
		String eventNumber = "55-444-33-22";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.EVENT_ID_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with BlankEventIdException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_blankEventIdException() throws Exception {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "";
		String eventNumber = "55-444-33-22";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.EVENT_ID_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with BlankEventNumberException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_nullEventNumberException() throws Exception {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = null;

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.EVENT_NUMBER_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String) with BlankEventNumberException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_blankEventNumberException() throws Exception {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.EVENT_NUMBER_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for OrderService#addNewOrder(Long, RegionType, String, String, String, String) with BlankLocationException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_nullLocationException() {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = null;
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "23-456-010";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.LOCATION_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for OrderService#addNewOrder(Long, RegionType, String, String, String, String) with BlankLocationException
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_blankLocationException() {
		// Given
		Long userId = 1L;
		RegionType region = RegionType.JAPAN;
		String location = "";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "23-456-010";

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.LOCATION_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for addNewOrder(Long, RegionType, String, String, String, String)
	 *
	 * @see OrderService#addNewOrder(Long, RegionType, String, String, String, String)
	 */
	@Test
	public void testAddNewOrder_exception_overQuantity() throws Throwable {
		// Given
		Long userId = 1L;
		Long categoryId = 1L;
		ItemEntity item = new ItemEntity("name", "description", categoryId, 10, "iamgePath", RegionType.JAPAN, new Date());
		item.setId(1L);
		List<ItemEntity> items = new ArrayList<>();
		items.add(item);

		CartItemEntity cartItem = new CartItemEntity(userId, item, 11);
		List<CartItemEntity> cartItems = new ArrayList<>();
		cartItems.add(cartItem);

		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";

		LocationEntity locationEntity = mock(LocationEntity.class);
		when(locationService.getLocationByRegion(any(RegionType.class))).thenReturn(locationEntity);
		when(shoppingCartService.getCartItemsByUserId(anyLong())).thenReturn(cartItems);
		when(itemService.getItemById(anyLong())).thenReturn(item);

		// When
		String message = "";
		try {
			underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(
				MessageFormat.format(AssetMessages.IN_STOCK_OF_ITEM_IS_INSUFFICIENT, item.getName()), message);
	}

	/**
	 * Test for getOrderByOrderNumber(String)
	 *
	 * @see OrderService#getOrderByOrderNumber(String)
	 */
	@Test
	public void testGetOrderByOrderNumber_normal() throws Exception {
		// Given
		String orderNumber = "11-234-567";
		OrderEntity findResult = mock(OrderEntity.class);
		doReturn(findResult).when(orderRepository).findOrderByOrderNumber(anyString());

		// When
		OrderEntity result = underTest.getOrderByOrderNumber(orderNumber);

		// Then
		assertNotNull(result);
	}

	/**
	 * Test for getOrderByOrderNumber(String) with BlankOrderNumberException
	 *
	 * @see OrderService#getOrderByOrderNumber(String)
	 */
	@Test
	public void testGetOrderByOrderNumber_exception_nullOrderNumberException() {
		// Given
		String orderNumber = null;

		// When
		String message = "";
		try {
			underTest.getOrderByOrderNumber(orderNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for getOrderByOrderNumber(String) with BlankOrderNumberException
	 *
	 * @see OrderService#getOrderByOrderNumber(String)
	 */
	@Test
	public void testGetOrderByOrderNumber_exception_blankOrderNumberException() {
		// Given
		String orderNumber = "";

		// When
		String message = "";
		try {
			underTest.getOrderByOrderNumber(orderNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for getOrderByOrderNumber(String) with NoOrderCorrespondingToException
	 *
	 * @see OrderService#getOrderByOrderNumber(String)
	 */
	@Test
	public void testGetOrderByOrderNumber_exception_noOrderCorrespondingToException() throws Exception {
		// Given
		String orderNumber = "123";
		doReturn(null).when(orderRepository).findOrderByOrderNumber(anyString());

		// When
		String message = "";
		try {
			underTest.getOrderByOrderNumber(orderNumber);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(
				MessageFormat.format(OrderMessages.THERE_IS_NO_ORDER_CORRESPONDING_TO, orderNumber), message);
	}

	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean) with NullStatusException
	 *
	 * @see OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateStatusOfOrderByOrderNumber_exception_nullStatusException() throws Throwable {
		// Given
		OrderStatus newStatus = null;
		String orderNumber = "11-234-567";

		// When
		String message = "";
		String roleTypeName = "";
		Boolean reviewedByPRCH = false;
		Boolean reviewedByAPV = true;
		String comments = "reject";
		boolean publicToMQ = true;
		try {
			underTest.updateOrderByOrderNumber(
					orderNumber, roleTypeName, newStatus, reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.STATUS_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean) with NullOrderNumberException
	 *
	 * @see OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateStatusOfOrderByOrderNumber_exception_nullOrderNumberException() throws Throwable {
		// Given
		OrderStatus newStatus = OrderStatus.DECLINED;
		String orderNumber = null;

		// When
		String message = "";
		String roleTypeName = "";
		Boolean reviewedByPRCH = false;
		Boolean reviewedByAPV = true;
		String comments = "reject";
		boolean publicToMQ = true;
		try {
			underTest.updateOrderByOrderNumber(
					orderNumber, roleTypeName, newStatus, reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean) with NullOrderNumberException
	 *
	 * @see OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateStatusOfOrderByOrderNumber_exception_blankOrderNumberException() throws Throwable {
		// Given
		OrderStatus newStatus = OrderStatus.DECLINED;
		String orderNumber = "";
		String roleTypeName = "";
		Boolean reviewedByPRCH = false;
		Boolean reviewedByAPV = true;
		String comments = "reject";
		boolean publicToMQ = true;

		// When
		String message = "";
		try {
			underTest.updateOrderByOrderNumber(
					orderNumber, roleTypeName, newStatus, reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for getAllOrders(Long userId, String userRoleName)
	 *
	 * @see OrderService#getAllOrders(Long userId, String userRoleName)
	 */
	@Test
	public void testGetAllOrders_normal_approver() throws Throwable {
		// Given
		Long userId = 1L;
		String userRoleName = RoleType.ROLE_APPROVER.toString();
		OrderEntity orderEntity = mock(OrderEntity.class);
		List<OrderEntity> findResult = new ArrayList<>();
		findResult.add(orderEntity);

		doReturn(findResult).when(orderRepository).findAll();

		// When
		List<OrderEntity> result = underTest.getAllOrders(userId, userRoleName);

		// Then
		assertNotNull(result);
		Assertions.assertEquals(1, result.size());
	}

	/**
	 * Test for getAllOrders(Long userId, String userRoleName)
	 *
	 * @see OrderService#getAllOrders(Long userId, String userRoleName)
	 */
	@Test
	public void testGetAllOrders_normal_purchaser() throws Throwable {
		// Given
		Long userId = 1L;
		String userRoleName = RoleType.ROLE_PURCHASER.toString();
		OrderEntity orderEntity = mock(OrderEntity.class);
		orderEntity.setUserId(userId);
		List<OrderEntity> findResult = new ArrayList<>();
		findResult.add(orderEntity);

		doReturn(findResult).when(orderRepository).findAllByUserId(anyLong());

		// When
		List<OrderEntity> result = underTest.getAllOrders(userId, userRoleName);

		// Then
		assertNotNull(result);
		Assertions.assertEquals(1, result.size());
	}

	/**
	 * Test for getAllOrders(Long userId, String userRoleName) with NullUserIdException
	 *
	 * @see OrderService#getAllOrders(Long userId, String userRoleName)
	 */
	@Test
	public void testGetAllOrders_exception_nullUserIdException() {
		// Given
		Long userId = null; // test point
		String userRoleName = RoleType.ROLE_APPROVER.toString();

		// When
		String message = "";
		try {
			underTest.getAllOrders(userId, userRoleName);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.USER_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for getAllOrders(Long userId, String userRoleName)
	 *
	 * @see OrderService#getAllOrders(Long userId, String userRoleName)
	 * @throws Throwable 
	 */
	@Test
	public void testGetAllOrders_normal_nullUserRoleName() throws Throwable {
		// Given
		Long userId = 1L;
		String userRoleName = null; // test point
		List<OrderEntity> findResult = new ArrayList<>();

		doReturn(findResult).when(orderRepository).findAllByUserId(anyLong());

		// When
		String message = "";
		try {
			underTest.getAllOrders(userId, userRoleName);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.USER_ROLE_NAME_CANNOT_BE_BLANK, message);	}

	/**
	 * Test for getAllOrders(Long, String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#getAllOrders(Long, String, Pageable)
	 */
	@Test
	public void testGetAllOrders_withPageable_approver() throws Throwable {
		// Given
		Pageable pageable = Pageable.unpaged();
		List<OrderEntity> content = new ArrayList<>();
		content.add(new OrderEntity());
		int totalElement = 2;
		Page<OrderEntity> page = new PageImpl<>(content, pageable, totalElement);
		
		doReturn(page).when(orderRepository).findAll(nullable(Pageable.class));
		
		//doReturn(page).when(orderRepository).findAllByUserId(nullable(Long.class),
		//		nullable(Pageable.class));

		// When
		Long userId = 1L;
		String userRoleName = RoleType.ROLE_APPROVER.toString();
		Page<OrderEntity> result = underTest.getAllOrders(userId, userRoleName, pageable);

		// Then
		assertNotNull(result);
		Assertions.assertEquals(content, result.getContent());
		Assertions.assertEquals(totalElement, result.getTotalElements());
	}

	/**
	 * Test for getAllOrders(Long, String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#getAllOrders(Long, String, Pageable)
	 */
	@Test
	public void testGetAllOrders_withPageable_purcher() throws Throwable {
		// Given
		Pageable pageable = Pageable.unpaged();
		List<OrderEntity> content = new ArrayList<>();
		content.add(new OrderEntity());
		int totalElement = 2;
		Page<OrderEntity> page = new PageImpl<>(content, pageable, totalElement);
		
		doReturn(page).when(orderRepository).findAllByUserId(nullable(Long.class),
				nullable(Pageable.class));

		// When
		Long userId = 2L;
		String userRoleName = RoleType.ROLE_PURCHASER.toString();
		Page<OrderEntity> result = underTest.getAllOrders(userId, userRoleName, pageable);

		// Then
		assertNotNull(result);
		Assertions.assertEquals(content, result.getContent());
		Assertions.assertEquals(totalElement, result.getTotalElements());
	}
	
	/**
	 * Test for getAllOrders(Long, String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#getAllOrders(Long, String, Pageable)
	 */
	@Test
	public void testGetAllOrders_withPageable_nullUserId() throws Throwable {
		// Given
		Long userId = null;
		String userRoleName = RoleType.ROLE_PURCHASER.toString();
		Pageable pageable = Pageable.unpaged();

		// When
		String message = "";
		try {
			underTest.getAllOrders(userId, userRoleName, pageable);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assertions.assertEquals(OrderMessages.USER_ID_CANNOT_BE_NULL, message);
	}
	
	/**
	 * Test for getAllOrders(Long, String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#getAllOrders(Long, String, Pageable)
	 */
	@Test
	public void testGetAllOrders_withPageable_unwantedRoleName() throws Throwable {
		// Given
		Long userId = 1L;
		String userRoleName = "unwantedRoleName";
		Pageable pageable = Pageable.unpaged();

		// When
		Page<OrderEntity> result = underTest.getAllOrders(userId, userRoleName, pageable);

		// Then
		assertNotNull(result);
		assertNotNull(result.getContent());
		Assertions.assertEquals(0, result.getContent().size());
		Assertions.assertEquals(0, result.getTotalElements());
	}

	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber_purchaser_normal() throws Throwable {
		// Given
		OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
		order.setStatus(OrderStatus.SUBMITTED);
		order.setReviewedByPRCH(true);
		order.setReviewedByAPV(false);
		String orderNumber = "11-234-567";
		OrderStatus newStatus = OrderStatus.SUBMITTED; 
		String userRoleName = RoleType.ROLE_PURCHASER.toString(); 
		Boolean reviewedByPRCH = true;
		Boolean reviewedByAPV = false;
		String comments = "";
		boolean publicToMQ = true;
		doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
		doReturn(order).when(orderRepository).save(any());

		// When
		OrderEntity result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, 
				reviewedByPRCH, reviewedByAPV, comments, publicToMQ);

		// Then
		assertNotNull(result);
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(false, result.getReviewedByAPV());
	}
	
	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean) with NoPermissionException
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber_purchaser_exception_NoPermissionException() throws Throwable {
		// Given
		OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
		order.setStatus(OrderStatus.SUBMITTED);
		order.setReviewedByPRCH(true);
		order.setReviewedByAPV(false);
		String orderNumber = "11-234-567";
		OrderStatus newStatus = OrderStatus.DECLINED;
		String userRoleName = RoleType.ROLE_PURCHASER.toString(); 
		Boolean reviewedByPRCH = true;
		Boolean reviewedByAPV = false;
		String comments = "reject";
		boolean publicToMQ = true;
		doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
		doReturn(order).when(orderRepository).save(any());

		// When
		String message = "";
		try {
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, 
					reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
		} catch (Exception e){
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);
	}
	
	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean) with IncorrectOperationException
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber_purchaser_exception_IncorrectOperationExcetion() throws Throwable {
		// Given
		OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
		order.setStatus(OrderStatus.SUBMITTED);
		order.setReviewedByPRCH(true);
		order.setReviewedByAPV(false);
		String orderNumber = "11-234-567";
		OrderStatus newStatus = OrderStatus.SUBMITTED; 
		String userRoleName = RoleType.ROLE_PURCHASER.toString(); 
		Boolean reviewedByPRCH = false;
		Boolean reviewedByAPV = false;
		String comments = "";
		boolean publicToMQ = true;
		doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
		doReturn(order).when(orderRepository).save(any());

		// When
		String message = "";
		try {
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, 
					reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
		} catch (Exception e){
			message = e.getMessage();
		}

		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);
	}
	
	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean) with ParameterException
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber_exception_ParameterException_reviewedByPRCH() throws Throwable {
		// Given
		OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
		order.setStatus(OrderStatus.SUBMITTED);
		order.setReviewedByPRCH(true);
		order.setReviewedByAPV(false);
		String orderNumber = "11-234-567";
		OrderStatus newStatus = OrderStatus.SUBMITTED; 
		String userRoleName = RoleType.ROLE_PURCHASER.toString(); 
		Boolean reviewedByPRCH = null; // test point
		Boolean reviewedByAPV = false;
		String comments = "";
		boolean publicToMQ = true;
		doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
		doReturn(order).when(orderRepository).save(any());

		// When
		String message = "";
		try {
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, 
					reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
		} catch (Exception e){
			message = e.getMessage();
		}

		// Then
		assertEquals(OrderMessages.ORDER_REVIEW_STATUS_OF_PURCHASER_SHOULD_NOT_BE_NULL, message);
	}
	
	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean) with ParameterException
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber_exception_ParameterException_reviewedByAPV() throws Throwable {
		// Given
		OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
		order.setStatus(OrderStatus.SUBMITTED);
		order.setReviewedByPRCH(true);
		order.setReviewedByAPV(false);
		String orderNumber = "11-234-567";
		OrderStatus newStatus = OrderStatus.SUBMITTED; 
		String userRoleName = RoleType.ROLE_APPROVER.toString(); 
		Boolean reviewedByPRCH = true;
		Boolean reviewedByAPV = null; // test point
		String comments = "";
		boolean publicToMQ = true;
		doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
		doReturn(order).when(orderRepository).save(any());

		// When
		String message = "";
		try {
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, 
					reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
		} catch (Exception e){
			message = e.getMessage();
		}

		// Then
		assertEquals(OrderMessages.ORDER_REVIEW_STATUS_OF_APPROVER_SHOULD_NOT_BE_NULL, message);
	}
	
	/**
	 * helper for preparing order with ignoring submmitted status
	 * @return
	 */
	private OrderEntity prepareOrderWithIgnoringSubmmitedStatusHelper() {
		Long orderId = 11234567L;
		Long userId = 1L;
		CartItemEntity cartItem = mock(CartItemEntity.class);
		List<CartItemEntity> cartItems = new ArrayList<>();
		cartItems.add(cartItem);
		RegionType region = RegionType.JAPAN;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";
		OrderEntity order = new OrderEntity(userId, region, location, receiverId, eventId, eventNumber);
		order.setId(orderId);
		return order;
	}
}