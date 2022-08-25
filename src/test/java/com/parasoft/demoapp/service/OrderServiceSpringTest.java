package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.MessageFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.OrderRepository;

/**
 * Test for OrderService
 *
 * @see com.parasoft.demoapp.service.OrderService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class OrderServiceSpringTest {
    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService underTest;

    /**
     * Test for addNewOrder(Long, String, RegionType, String, String, String, String)
     *
     * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrder() throws Throwable {
        CategoryEntity category = null;
        ItemEntity item = null;
        UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
        Long userId = user.getId();
        String requestedBy = user.getUsername();
        try {
            category = categoryService.addNewCategory("name", "description", "imagePath");
            item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
            shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 10);

            // When
            RegionType region = RegionType.LOCATION_1;
            String location = "JAPAN 82.8628° S, 135.0000° E";
            String receiverId = "345-6789-21";
            String eventId = "45833-ORG-7834";
            String eventNumber = "55-444-33-22";
            OrderEntity result = underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);

            // Then
            assertNotNull(result);
            assertEquals("name", result.getOrderItems().get(0).getName());
            assertEquals(requestedBy, result.getRequestedBy());
            assertEquals(OrderStatus.SUBMITTED.getStatus(), result.getStatus().getStatus());
            assertEquals(1, result.getOrderItems().size());
            assertEquals(RegionType.LOCATION_1, result.getRegion());
            assertEquals(receiverId, result.getReceiverId());
            assertEquals(location, result.getLocation());
            assertEquals(eventId, result.getEventId());
            assertEquals(eventNumber, result.getEventNumber());
        } finally {
            itemService.removeItemById(item.getId());
            categoryService.removeCategory(category.getId());
            orderRepository.deleteAll();
        }
    }

    /**
     * Test for OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     *
     * @see OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    @Test
    public void testUpdateOrderByOrderNumber1() throws Throwable {
        // When
        Long userId = null;
        String requestedBy = null;
        CategoryEntity category = null;
        ItemEntity item = null;
        OrderEntity order = null;
        try {
            // Given
            UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_APPROVER);
            userId = user.getId();
            requestedBy = user.getUsername();
            category = categoryService.addNewCategory("name", "description", "imagePath");
            item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
            shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 20);

            // When
            RegionType region = RegionType.LOCATION_1;
            String location = "JAPAN 82.8628° S, 135.0000° E";
            String receiverId = "345-6789-21";
            String eventId = "45833-ORG-7834";
            String eventNumber = "55-444-33-22";
            order = underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);

            String orderNumber = order.getOrderNumber();
            String userRoleName = RoleType.ROLE_APPROVER.toString();
            OrderStatus newStatus = OrderStatus.DECLINED;
            Boolean reviewedByPRCH = true;
            Boolean reviewedByAPV = true;
            String comments = "reject";
			boolean publicToMQ = true;
			String respondedBy = "approver";

            OrderEntity result = underTest.updateOrderByOrderNumber(
                    orderNumber, userRoleName, newStatus, reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

            // Then
            assertEquals(newStatus, result.getStatus());
            assertEquals(comments, result.getComments());
			assertEquals(respondedBy, result.getRespondedBy());
            assertEquals(30, (int)itemService.getInStockById(item.getId()));

            // When
            shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 20);
            order = underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
            orderNumber = order.getOrderNumber();
            newStatus = OrderStatus.APPROVED;
            comments = "approved";
            OrderEntity result2 = underTest.updateOrderByOrderNumber(
                    orderNumber, userRoleName, newStatus, reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

            // Then
            assertEquals(newStatus, result2.getStatus());
            assertEquals(comments, result2.getComments());
			assertEquals(respondedBy, result2.getRespondedBy());
            assertEquals(10, (int)itemService.getInStockById(item.getId()));
        } finally {
            itemService.removeItemById(item.getId());
            categoryService.removeCategory(category.getId());
            orderRepository.deleteAll();
        }
    }

	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber2() throws Throwable {
		Long userId = null;
		String requestedBy = null;
		CategoryEntity category = null;
		ItemEntity item = null;
		OrderEntity order = null;
		OrderEntity result = null;
		OrderStatus newStatus = null;
		Boolean reviewedByPRCH = null;
		Boolean reviewedByAPV = null;
		String userRoleName = "";
		String message = "";
		boolean publicToMQ = true;
		String respondedBy = null;

		// Given
		UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
		userId = user.getId();
        requestedBy = user.getUsername();
		category = categoryService.addNewCategory("name", "description", "imagePath");
		item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
		// add item into cart, the quantity of item is 20.
		shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 20);
		RegionType region = RegionType.LOCATION_1;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";
		order = underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
		String orderNumber = order.getOrderNumber();
		String comments = "";
		userRoleName = RoleType.ROLE_PURCHASER.toString();

		// When
		newStatus = OrderStatus.SUBMITTED; // test point
		reviewedByPRCH = true; // test point
		reviewedByAPV = false; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.SUBMITTED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(false, result.getReviewedByAPV());
		assertNull(result.getRespondedBy());

		// When
		try {
			newStatus = OrderStatus.APPROVED; // test point
			comments = "approved";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		// When
		try {
			newStatus = OrderStatus.DECLINED; // test point
			comments = "reject";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		// When
		try {
			newStatus = OrderStatus.SUBMITTED;
			comments = "";
			reviewedByPRCH = false; // test point
			reviewedByAPV = false;
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByPRCH = true;
		reviewedByAPV = true; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.SUBMITTED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(false, result.getReviewedByAPV());

		// When
		userRoleName = RoleType.ROLE_APPROVER.toString(); // test point
		reviewedByPRCH = true; // test point
		reviewedByAPV = true; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.SUBMITTED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());

		// When
		try {
			reviewedByAPV = false; // test point
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByPRCH = false; // test point
		reviewedByAPV = true;
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.SUBMITTED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());

		// When
		newStatus = OrderStatus.APPROVED;
		respondedBy = "approver";
		comments = "approved";
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.APPROVED, result.getStatus());
		assertEquals(false, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(comments, result.getComments());

		// When
		try {
			newStatus = OrderStatus.DECLINED;
			comments = "reject";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.ALREADY_MODIFIED_THIS_ORDER, message);

		// When
		try {
			newStatus = OrderStatus.APPROVED;
			comments = "approved";
			reviewedByPRCH = false;
			reviewedByAPV = false; // test point
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByPRCH = true; // test point
		reviewedByAPV = true;
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.APPROVED, result.getStatus());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(false, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());

		// When
		userRoleName = RoleType.ROLE_PURCHASER.toString();
		newStatus = OrderStatus.APPROVED; // test point
		reviewedByPRCH = true; // test point
		reviewedByAPV = true; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.APPROVED, result.getStatus());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());

		// When
		try {
			reviewedByPRCH = false; // test point
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByPRCH = true;
		reviewedByAPV = false; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.APPROVED, result.getStatus());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());

		// When
		try {
			newStatus = OrderStatus.SUBMITTED; // test point
			comments = "";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		// When
		try {
			newStatus = OrderStatus.DECLINED; // test point
			comments = "reject";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		itemService.removeItemById(item.getId());
		categoryService.removeCategory(category.getId());
		orderRepository.deleteAll();
	}

	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber3() throws Throwable {
		Long userId = null;
		String requestedBy = null;
		CategoryEntity category = null;
		ItemEntity item = null;
		OrderEntity order = null;
		OrderEntity result = null;
		OrderStatus newStatus = null;
		Boolean reviewedByPRCH = null;
		Boolean reviewedByAPV = null;
		String userRoleName = "";
		String message = "";
		boolean publicToMQ= true;
		String respondedBy = null;

		// Given
		UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
		userId = user.getId();
        requestedBy = user.getUsername();
		category = categoryService.addNewCategory("name1", "description", "imagePath");
		item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
		// add item into cart, the quantity of item is 20.
		shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 20);
		RegionType region = RegionType.LOCATION_1;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";
		order = underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
		String orderNumber = order.getOrderNumber();
		String comments = "";
		userRoleName = RoleType.ROLE_PURCHASER.toString();

		// When
		newStatus = OrderStatus.SUBMITTED; // test point
		reviewedByPRCH = true; // test point
		reviewedByAPV = false; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.SUBMITTED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(false, result.getReviewedByAPV());
		assertNull(result.getRespondedBy());

		// When
		userRoleName = RoleType.ROLE_APPROVER.toString();
		newStatus = OrderStatus.DECLINED; // test point
		comments = "reject";
		respondedBy = "approver";
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV,respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(false, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));
		assertEquals(comments, result.getComments());

		// When
		try {
			newStatus = OrderStatus.APPROVED; // test point
			comments = "approved";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.ALREADY_MODIFIED_THIS_ORDER, message);

		// When
		try {
			newStatus = OrderStatus.DECLINED;
			comments = "reject";
			reviewedByAPV = false; // test point
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByAPV = true;
		reviewedByPRCH = true; // test point
		underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
				reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(false, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));

		// When
		userRoleName = RoleType.ROLE_PURCHASER.toString(); // test point
		reviewedByPRCH = true; // test point
		reviewedByAPV = true; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV,respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));

		// When
		try {
			reviewedByPRCH = false; // test point
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByPRCH = true;
		reviewedByAPV = false; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV,respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));

		// When
		try {
			newStatus = OrderStatus.SUBMITTED; // test point
			comments = "";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, comments,respondedBy, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		// When
		try {
			newStatus = OrderStatus.APPROVED; // test point
			comments = "approved";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, comments,respondedBy, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		itemService.removeItemById(item.getId());
		categoryService.removeCategory(category.getId());
		orderRepository.deleteAll();
	}

	/**
	 * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumber4() throws Throwable {
		Long userId = null;
		String requestedBy = null;
		CategoryEntity category = null;
		ItemEntity item = null;
		OrderEntity order = null;
		OrderEntity result = null;
		OrderStatus newStatus = null;
		Boolean reviewedByPRCH = null;
		Boolean reviewedByAPV = null;
		String userRoleName = "";
		String message = "";
		String respondedBy = null;
		boolean publicToMQ = true;

		// Given
		UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
		userId = user.getId();
        requestedBy = user.getUsername();
		category = categoryService.addNewCategory("name1", "description", "imagePath");
		item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
		// add item into cart, the quantity of item is 20.
		shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 20);
		RegionType region = RegionType.LOCATION_1;
		String location = "JAPAN 82.8628° S, 135.0000° E";
		String receiverId = "345-6789-21";
		String eventId = "45833-ORG-7834";
		String eventNumber = "55-444-33-22";
		order = underTest.addNewOrder(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
		String orderNumber = order.getOrderNumber();
		String comments = "";
		userRoleName = RoleType.ROLE_PURCHASER.toString();

		// When
		newStatus = OrderStatus.SUBMITTED; // test point
		reviewedByPRCH = true; // test point
		reviewedByAPV = false; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.SUBMITTED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(false, result.getReviewedByAPV());
		assertNull(result.getRespondedBy());

		// When
		userRoleName = RoleType.ROLE_APPROVER.toString();
		newStatus = OrderStatus.DECLINED; // test point
		comments = "reject";
		respondedBy = "approver";
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(false, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));

		// When
		try {
			newStatus = OrderStatus.APPROVED; // test point
			comments = "approved";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.ALREADY_MODIFIED_THIS_ORDER, message);

		// When
		try {
			newStatus = OrderStatus.DECLINED;
			comments = "reject";
			reviewedByAPV = false; // test point
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, respondedBy, comments, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByAPV = true;
		reviewedByPRCH = true; // test point
		underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
				reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(false, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));

		// When
		userRoleName = RoleType.ROLE_PURCHASER.toString(); // test point
		reviewedByPRCH = true; // test point
		reviewedByAPV = true; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));

		// When
		try {
			reviewedByPRCH = false; // test point
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, comments, respondedBy, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(OrderMessages.CANNOT_SET_TRUE_TO_FALSE, message);

		// When
		reviewedByPRCH = true;
		reviewedByAPV = false; // test point
		result = underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus,
				reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

		// Then
		assertEquals(OrderStatus.DECLINED, result.getStatus());
		assertEquals(true, result.getReviewedByPRCH());
		assertEquals(true, result.getReviewedByAPV());
		assertEquals(respondedBy, result.getRespondedBy());
		assertEquals(30, (int)itemService.getInStockById(item.getId()));

		// When
		try {
			newStatus = OrderStatus.SUBMITTED; // test point
			comments = "";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, comments, respondedBy, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		// When
		try {
			newStatus = OrderStatus.APPROVED; // test point
			comments = "approved";
			underTest.updateOrderByOrderNumber(orderNumber, userRoleName, newStatus, reviewedByPRCH,
					reviewedByAPV, comments, respondedBy, publicToMQ);
		}catch(Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}
		// Then
		assertEquals(MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus), message);

		itemService.removeItemById(item.getId());
		categoryService.removeCategory(category.getId());
		orderRepository.deleteAll();
	}
}
