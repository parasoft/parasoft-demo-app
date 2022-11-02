/**
 *
 */
package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.*;
import com.parasoft.demoapp.exception.LocationNotFoundException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.industry.*;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;


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

    @Mock
    OrderMQService orderMQService;

    @Mock
    ItemInventoryMQService itemInventoryMQService;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     *
     * @see OrderService#handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testHandleMessageFromResponseQueue_normal() {
        // Given
        String orderNumber = "123-456-789";
        OrderEntity order = new OrderEntity();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findOrderByOrderNumber(anyString())).thenReturn(order);
        when(orderRepository.save((OrderEntity) any())).thenReturn(order);

        // When
        InventoryOperationRequestMessageDTO requestMessage = underTest.handleMessageFromResponseQueue(
                new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, orderNumber, InventoryOperationStatus.SUCCESS, null));

        // Then
        Mockito.verify(orderMQService, times(1)).sendToApprover(any());
        assertNull(requestMessage);
        assertEquals(OrderStatus.PROCESSED, order.getStatus());
    }

    /**
     * Test for handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     *
     * @see OrderService#handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testHandleMessageFromResponseQueue_inventoryNotEnough() {
        // Given
        String orderNumber = "123-456-789";
        OrderEntity order = new OrderEntity();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findOrderByOrderNumber(anyString())).thenReturn(order);
        when(orderRepository.save((OrderEntity) any())).thenReturn(order);
        String cancelledInfo = "Inventory item with id 1 is out of stock.";

        // When
        InventoryOperationRequestMessageDTO requestMessage = underTest.handleMessageFromResponseQueue(
                new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, orderNumber,
                        InventoryOperationStatus.FAIL, cancelledInfo));

        // Then
        Mockito.verify(orderMQService, times(1)).sendToApprover(any());
        assertEquals(requestMessage, null);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        assertEquals(cancelledInfo, order.getComments());
    }

    /**
     * Test for handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     *
     * @see OrderService#handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testHandleMessageFromResponseQueue_reversedProcess() {
        // Given
        String orderNumber = "123-456-789";
        OrderEntity order = new OrderEntity();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.CANCELED);
        order.setOrderItems(new ArrayList<>());
        when(orderRepository.findOrderByOrderNumber(anyString())).thenReturn(order);
        when(orderRepository.save((OrderEntity) any())).thenReturn(order);

        // When
        InventoryOperationRequestMessageDTO requestMessage = underTest.handleMessageFromResponseQueue(
                new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, orderNumber, InventoryOperationStatus.SUCCESS, null));

        // Then
        InventoryOperationRequestMessageDTO expectedRequestMessage =
                new InventoryOperationRequestMessageDTO(InventoryOperation.NONE, orderNumber,
                        "Can not change order status from CANCELED to PROCESSED");
        assertEquals(requestMessage, expectedRequestMessage);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    /**
     * Test for handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     *
     * @see OrderService#handleMessageFromResponseQueue(InventoryOperationResultMessageDTO)
     */
    @Test
    public void testHandleMessageFromResponseQueue_orderNotExist() {
        // Given
        String orderNumber = "123-456-789";
        when(orderRepository.findOrderByOrderNumber(anyString())).thenReturn(null);

        // When
        InventoryOperationRequestMessageDTO requestMessage = underTest.handleMessageFromResponseQueue(
                new InventoryOperationResultMessageDTO(InventoryOperation.DECREASE, orderNumber, InventoryOperationStatus.SUCCESS, null));

        // Then
        InventoryOperationRequestMessageDTO expectedRequestMessage =
                new InventoryOperationRequestMessageDTO(InventoryOperation.NONE, orderNumber,
                        "There is no order corresponding to " + orderNumber + ".");
        assertEquals(requestMessage, expectedRequestMessage);
    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String)
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_normal() throws Throwable {
        // Given
        Long orderId = 10L;
        Long userId = 1L;
        String requestedBy = "testUser";
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

        OrderEntity saveResult = new OrderEntity(requestedBy, region, location, receiverId, eventId, eventNumber);
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
        OrderEntity result = underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);

        // Then
        assertNotNull(result);
        assertEquals(requestedBy, result.getRequestedBy());
        assertEquals(OrderStatus.SUBMITTED.getStatus(), result.getStatus().getStatus());
        assertEquals(OrderStatus.SUBMITTED.getPriority(), result.getStatus().getPriority());
        assertEquals(1, result.getOrderItems().size());
        assertEquals(RegionType.JAPAN, result.getRegion());
        assertEquals(receiverId, result.getReceiverId());
        assertEquals(location, result.getLocation());
        assertEquals(eventId, result.getEventId());
        assertEquals(imagePath, result.getOrderImage());
        assertEquals(eventNumber, result.getEventNumber());
        assertEquals("23-456-010", result.getOrderNumber());
        assertEquals(submissionDate, result.getSubmissionDate());
        Mockito.verify(orderMQService).sendToInventoryRequestQueue(InventoryOperation.DECREASE, "23-456-010", orderItems);
    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String)
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_regionTypeNotFound() throws Throwable {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "55-444-33-22";

        when(locationService.getLocationByRegion(any(RegionType.class))).thenThrow(LocationNotFoundException.class);

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(MessageFormat.format(OrderMessages.LOCATION_NOT_FOUND_FOR_REGION, region.toString()), message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String)
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_noCartItemsInCart() throws Throwable {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
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
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(AssetMessages.NO_CART_ITEMS, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with NullUserIdException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_nullUserIdException() {
        // Given
        Long userId = null;
        String requestedBy = "";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "55-444-33-22";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.USER_ID_CANNOT_BE_NULL, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with NullUserNameException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_nullUserNameException() {
        // Given
        Long userId = 1L;
        String requestedBy = null;
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "55-444-33-22";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.USERNAME_CANNOT_BE_NULL, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with NullRegionException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_nullRegionException() throws Exception {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = null;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "55-444-33-22";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.REGION_CANNOT_BE_NULL, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with BlankReceiverIdException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_nullReceiverIdException() throws Exception {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = null;
        String eventId = "45833-ORG-7834";
        String eventNumber = "55-444-33-22";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.RECEIVER_ID_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with BlankReceiverIdException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_blankReceiverIdException() throws Exception {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "";
        String eventId = "45833-ORG-7834";
        String eventNumber = "55-444-33-22";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.RECEIVER_ID_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with BlankEventIdException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_nullEventIdException() throws Exception {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = null;
        String eventNumber = "55-444-33-22";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.EVENT_ID_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with BlankEventIdException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_blankEventIdException() throws Exception {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "";
        String eventNumber = "55-444-33-22";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.EVENT_ID_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with BlankEventNumberException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_nullEventNumberException() throws Exception {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = null;

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.EVENT_NUMBER_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

    }

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String) with BlankEventNumberException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_blankEventNumberException() throws Exception {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.EVENT_NUMBER_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

    }

    /**
     * Test for OrderService#addNewOrder(Long, String, RegionType, String, String, String, String) with BlankLocationException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_nullLocationException() {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = null;
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "23-456-010";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.LOCATION_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

    }

    /**
     * Test for OrderService#addNewOrder(Long, String, RegionType, String, String, String, String) with BlankLocationException
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder_exception_blankLocationException() {
        // Given
        Long userId = 1L;
        String requestedBy = "testUser";
        RegionType region = RegionType.JAPAN;
        String location = "";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "23-456-010";

        // When
        String message = "";
        try {
            underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.LOCATION_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

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
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with NullStatusException
     *
     * @see OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
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
        String respondedBy = null;
        String comments = "reject";
        boolean publicToMQ = true;
        try {
            underTest.updateOrderByOrderNumber(
                    orderNumber, roleTypeName, newStatus, reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.STATUS_CANNOT_BE_NULL, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with NullOrderNumberException
     *
     * @see OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
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
        String respondedBy = null;
        String comments = "reject";
        boolean publicToMQ = true;
        try {
            underTest.updateOrderByOrderNumber(
                    orderNumber, roleTypeName, newStatus, reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with NullOrderNumberException
     *
     * @see OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateStatusOfOrderByOrderNumber_exception_blankOrderNumberException() throws Throwable {
        // Given
        OrderStatus newStatus = OrderStatus.DECLINED;
        String orderNumber = "";
        String roleTypeName = "";
        Boolean reviewedByPRCH = false;
        Boolean reviewedByAPV = true;
        String respondedBy = null;
        String comments = "reject";
        boolean publicToMQ = true;

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(
                    orderNumber, roleTypeName, newStatus, reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for getAllOrders(String requestedBy, String userRoleName)
     *
     * @see OrderService#getAllOrders(String requestedBy, String userRoleName)
     */
    @Test
    public void testGetAllOrders_normal_approver() throws Throwable {
        // Given
        String requestedBy = "testUser";
        String userRoleName = RoleType.ROLE_APPROVER.toString();
        OrderEntity orderEntity = mock(OrderEntity.class);
        List<OrderEntity> findResult = new ArrayList<>();
        findResult.add(orderEntity);

        doReturn(findResult).when(orderRepository).findAll();

        // When
        List<OrderEntity> result = underTest.getAllOrders(requestedBy, userRoleName);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    /**
     * Test for getAllOrders(String requestedBy, String userRoleName)
     *
     * @see OrderService#getAllOrders(String requestedBy, String userRoleName)
     */
    @Test
    public void testGetAllOrders_normal_purchaser() throws Throwable {
        // Given
        String requestedBy = "testUser";
        String userRoleName = RoleType.ROLE_PURCHASER.toString();
        OrderEntity orderEntity = mock(OrderEntity.class);
        orderEntity.setRequestedBy(requestedBy);
        List<OrderEntity> findResult = new ArrayList<>();
        findResult.add(orderEntity);

        doReturn(findResult).when(orderRepository).findAllByRequestedBy(anyString());

        // When
        List<OrderEntity> result = underTest.getAllOrders(requestedBy, userRoleName);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    /**
     * Test for getAllOrders(String requestedBy, String userRoleName) with NullUserIdException
     *
     * @see OrderService#getAllOrders(String requestedBy, String userRoleName)
     */
    @Test
    public void testGetAllOrders_exception_nullUserNameException() {
        // Given
        String requestedBy = null;
        String userRoleName = RoleType.ROLE_APPROVER.toString();

        // When
        String message = "";
        try {
            underTest.getAllOrders(requestedBy, userRoleName);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.USERNAME_CANNOT_BE_NULL, message);
    }

    /**
     * Test for getAllOrders(String requestedBy, String userRoleName)
     *
     * @see OrderService#getAllOrders(String requestedBy, String userRoleName)
     * @throws Throwable
     */
    @Test
    public void testGetAllOrders_normal_nullUserRoleName() throws Throwable {
        // Given
        String requestedBy = "testUser";
        String userRoleName = null; // test point
        List<OrderEntity> findResult = new ArrayList<>();

        doReturn(findResult).when(orderRepository).findAllByRequestedBy(anyString());

        // When
        String message = "";
        try {
            underTest.getAllOrders(requestedBy, userRoleName);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.USER_ROLE_NAME_CANNOT_BE_BLANK, message);	}

    /**
     * Test for getAllOrders(String, String, Pageable)
     *
     * @see com.parasoft.demoapp.service.OrderService#getAllOrders(String, String, Pageable)
     */
    @Test
    public void testGetAllOrders_withPageable_approver() throws Throwable {
        // Given
        Pageable pageable = Pageable.unpaged();
        List<OrderEntity> content = new ArrayList<>();
        content.add(new OrderEntity());
        int totalElement = 2;
        Page<OrderEntity> page = new PageImpl<>(content, pageable, totalElement);

        doReturn(page).when(orderRepository).findAllByStatusNotIn(any(), nullable(Pageable.class));

        //doReturn(page).when(orderRepository).findAllByUserId(nullable(Long.class),
        //		nullable(Pageable.class));

        // When
        String requestedBy = "testUser";
        String userRoleName = RoleType.ROLE_APPROVER.toString();
        Page<OrderEntity> result = underTest.getAllOrders(requestedBy, userRoleName, pageable);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(content, result.getContent());
        Assertions.assertEquals(totalElement, result.getTotalElements());
    }

    /**
     * Test for getAllOrders(String, String, Pageable)
     *
     * @see com.parasoft.demoapp.service.OrderService#getAllOrders(String, String, Pageable)
     */
    @Test
    public void testGetAllOrders_withPageable_purcher() throws Throwable {
        // Given
        Pageable pageable = Pageable.unpaged();
        List<OrderEntity> content = new ArrayList<>();
        content.add(new OrderEntity());
        int totalElement = 2;
        Page<OrderEntity> page = new PageImpl<>(content, pageable, totalElement);

        doReturn(page).when(orderRepository).findAllByRequestedBy(nullable(String.class),
                nullable(Pageable.class));

        // When
        String requestedBy = "testUser";
        String userRoleName = RoleType.ROLE_PURCHASER.toString();
        Page<OrderEntity> result = underTest.getAllOrders(requestedBy, userRoleName, pageable);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(content, result.getContent());
        Assertions.assertEquals(totalElement, result.getTotalElements());
    }

    /**
     * Test for getAllOrders(String, String, Pageable)
     *
     * @see com.parasoft.demoapp.service.OrderService#getAllOrders(String, String, Pageable)
     */
    @Test
    public void testGetAllOrders_withPageable_nullUserName() throws Throwable {
        // Given
        String requestedBy = null;
        String userRoleName = RoleType.ROLE_PURCHASER.toString();
        Pageable pageable = Pageable.unpaged();

        // When
        String message = "";
        try {
            underTest.getAllOrders(requestedBy, userRoleName, pageable);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        Assertions.assertEquals(OrderMessages.USERNAME_CANNOT_BE_NULL, message);
    }

    /**
     * Test for getAllOrders(String, String, Pageable)
     *
     * @see com.parasoft.demoapp.service.OrderService#getAllOrders(String, String, Pageable)
     */
    @Test
    public void testGetAllOrders_withPageable_unwantedRoleName() throws Throwable {
        // Given
        String requestedBy = "testUser";
        String userRoleName = "unwantedRoleName";
        Pageable pageable = Pageable.unpaged();

        // When
        Page<OrderEntity> result = underTest.getAllOrders(requestedBy, userRoleName, pageable);

        // Then
        assertNotNull(result);
        assertNotNull(result.getContent());
        Assertions.assertEquals(0, result.getContent().size());
        Assertions.assertEquals(0, result.getTotalElements());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_purchaser_normal() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.PROCESSED);
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.PROCESSED;
        String userRoleName = RoleType.ROLE_PURCHASER.toString();
        Boolean reviewedByPRCH = true;
        Boolean reviewedByAPV = false;
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
        doReturn(order).when(orderRepository).save(any());

        // When
        OrderEntity result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

        // Then
        assertNotNull(result);
        assertEquals(true, result.getReviewedByPRCH());
        assertEquals(false, result.getReviewedByAPV());
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with NoPermissionException
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_purchaser_exception_NoPermissionException() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.PROCESSED);
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.DECLINED;
        String userRoleName = RoleType.ROLE_PURCHASER.toString();
        Boolean reviewedByPRCH = true;
        Boolean reviewedByAPV = false;
        String respondedBy = null;
        String comments = "reject";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
        doReturn(order).when(orderRepository).save(any());

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with IncorrectOperationException
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_purchaser_exception_IncorrectOperationExcetion() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.PROCESSED);
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.PROCESSED;
        String userRoleName = RoleType.ROLE_PURCHASER.toString();
        Boolean reviewedByPRCH = false;
        Boolean reviewedByAPV = false;
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
        doReturn(order).when(orderRepository).save(any());

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with ParameterException
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_exception_ParameterException_reviewedByPRCH() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.PROCESSED);
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.PROCESSED;
        String userRoleName = RoleType.ROLE_PURCHASER.toString();
        Boolean reviewedByPRCH = null; // test point
        Boolean reviewedByAPV = false;
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
        doReturn(order).when(orderRepository).save(any());

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(OrderMessages.ORDER_REVIEW_STATUS_OF_PURCHASER_SHOULD_NOT_BE_NULL, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with ParameterException
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_exception_ParameterException_reviewedByAPV() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.PROCESSED);
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.PROCESSED;
        String userRoleName = RoleType.ROLE_APPROVER.toString();
        Boolean reviewedByPRCH = true;
        Boolean reviewedByAPV = null; // test point
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
        doReturn(order).when(orderRepository).save(any());

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(OrderMessages.ORDER_REVIEW_STATUS_OF_APPROVER_SHOULD_NOT_BE_NULL, message);
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with ParameterException
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_exception_ParameterException_orderHasBeenCancelled() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.CANCELED); // test point
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.APPROVED;
        String userRoleName = RoleType.ROLE_APPROVER.toString();
        Boolean reviewedByPRCH = true;
        Boolean reviewedByAPV = false;
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(OrderMessages.ORDER_INFO_CANNOT_CHANGE_FROM_CANCELED, message);
        Mockito.verify(orderRepository, times(1)).findOrderByOrderNumber(anyString());
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with ParameterException
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_exception_ParameterException_orderHasNotPrepared() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.SUBMITTED); // test point
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.APPROVED;
        String userRoleName = RoleType.ROLE_APPROVER.toString();
        Boolean reviewedByPRCH = true;
        Boolean reviewedByAPV = false;
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(OrderMessages.ORDER_INFO_CANNOT_CHANGE_FROM_SUBMITTED, message);
        Mockito.verify(orderRepository, times(1)).findOrderByOrderNumber(anyString());
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with ParameterException
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_exception_ParameterException_changeToInternalStatus() throws Throwable {
        // Given
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.PROCESSED);
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        String orderNumber = "11-234-567";
        OrderStatus newStatus = OrderStatus.SUBMITTED; // test point
        String userRoleName = RoleType.ROLE_APPROVER.toString();
        Boolean reviewedByPRCH = true;
        Boolean reviewedByAPV = false;
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);

        // When
        String message = "";
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(OrderMessages.ORDER_STATUS_CHANGED_BACK_ERROR, order.getStatus(), newStatus), message);
        Mockito.verify(orderRepository, times(1)).findOrderByOrderNumber(anyString());
        Mockito.verify(orderMQService, times(0)).sendToInventoryRequestQueue(any(InventoryOperation.class), anyString(), anyList());

        // Given
        newStatus = OrderStatus.CANCELED; // test point

        // When
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(OrderMessages.ORDER_STATUS_CHANGED_BACK_ERROR, order.getStatus(), newStatus), message);

        // Given
        newStatus = OrderStatus.CANCELED; // test point

        // When
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(OrderMessages.ORDER_STATUS_CHANGED_BACK_ERROR, order.getStatus(), newStatus), message);

        // Then
        assertEquals(MessageFormat.format(OrderMessages.ORDER_STATUS_CHANGED_BACK_ERROR, order.getStatus(), newStatus), message);

        // Given
        newStatus = OrderStatus.SUBMITTED; // test point

        // When
        try {
            underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                    reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        } catch (Exception e){
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(OrderMessages.ORDER_STATUS_CHANGED_BACK_ERROR, order.getStatus(), newStatus), message);
    }

    /**
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) declined
     *
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber_declined_normal() throws Throwable {
        // Given
        String orderNumber = "11-234-567";
        OrderEntity order = prepareOrderWithIgnoringSubmmitedStatusHelper();
        order.setStatus(OrderStatus.PROCESSED);
        order.setOrderNumber(orderNumber);
        order.setReviewedByPRCH(true);
        order.setReviewedByAPV(false);
        OrderStatus newStatus = OrderStatus.DECLINED;
        String userRoleName = RoleType.ROLE_APPROVER.toString();
        Boolean reviewedByPRCH = true;
        Boolean reviewedByAPV = null;
        String respondedBy = null;
        String comments = "";
        boolean publicToMQ = true;
        doReturn(order).when(orderRepository).findOrderByOrderNumber(orderNumber);
        doReturn(order).when(orderRepository).save(any());

        // When
        underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
                reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
        // Then
        Mockito.verify(orderMQService).sendToInventoryRequestQueue(InventoryOperation.INCREASE, "11-234-567", null);
    }

    /**
     * helper for preparing order with ignoring submmitted status
     * @return
     */
    private OrderEntity prepareOrderWithIgnoringSubmmitedStatusHelper() {
        Long orderId = 11234567L;
        String requestedBy = "testUser";
        CartItemEntity cartItem = mock(CartItemEntity.class);
        List<CartItemEntity> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        RegionType region = RegionType.JAPAN;
        String location = "JAPAN 82.8628° S, 135.0000° E";
        String receiverId = "345-6789-21";
        String eventId = "45833-ORG-7834";
        String eventNumber = "55-444-33-22";
        OrderEntity order = new OrderEntity(requestedBy, region, location, receiverId, eventId, eventNumber);
        order.setId(orderId);
        return order;
    }
}