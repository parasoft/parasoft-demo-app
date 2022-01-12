package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
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
public class OrderServiceSpringTest4 {
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

	@Autowired
	GlobalPreferencesService globalPreferencesService;

	/**
	 * Test for updateOrderByOrderNumberSynchronized(String, String, OrderStatus, Boolean, Boolean, String, boolean) under concurrency condition.
	 *
	 * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumberSynchronized(String, String, OrderStatus, Boolean, Boolean, String, boolean)
	 */
	@Test
	public void testUpdateOrderByOrderNumberSynchronized_concurrency() throws Throwable {
		// When
		// reset database of industry
		globalPreferencesService.resetAllIndustriesDatabase();

		Long userId = null;
		CategoryEntity category = null;
		ItemEntity item = null;
		OrderEntity order = null;
		try {
			// Given
			UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
			userId = user.getId();
			category = categoryService.addNewCategory("name", "description", "imagePath");
			item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
			// add item into cart, the quantity of item is 20.
			shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 20);

			// When
			RegionType region = RegionType.LOCATION_1;
			String location = "JAPAN 82.8628° S, 135.0000° E";
			String receiverId = "345-6789-21";
			String eventId = "45833-ORG-7834";
			String eventNumber = "55-444-33-22";
			order = underTest.addNewOrder(userId, region, location, receiverId, eventId, eventNumber);

			String orderNumber = order.getOrderNumber();
			String userRoleName = RoleType.ROLE_APPROVER.toString();
			OrderStatus newStatus = OrderStatus.DECLINED;
			Boolean reviewedByPRCH = true;
			Boolean reviewedByAPV = true;
			String comments = "reject";
			boolean publicToMQ = true;
			ExecutorService es = Executors.newCachedThreadPool();

			// use 3 threads to simulate the concurrency situation, decline orders repeatedly in the same time.
			for (int i = 0; i < 3; i++) {
				es.submit(new UpdateOrderByOrderNumberRunnable(underTest, orderNumber, userRoleName, newStatus,
						reviewedByPRCH, reviewedByAPV, comments, publicToMQ));
			}

			Thread.sleep(2000);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			// Then
			assertEquals(1, orderRepository.findAll().size());
			assertEquals(OrderStatus.DECLINED, orderRepository.findOrderByOrderNumber(order.getOrderNumber()).getStatus());
			assertEquals(30, (int)itemService.getInStockById(item.getId()));
			itemService.removeItemById(item.getId());
			categoryService.removeCategory(category.getId());
			orderRepository.deleteAll();
		}
	}

	private class UpdateOrderByOrderNumberRunnable implements Runnable {
		private final OrderService orderService;
		private final String orderNumber;
		private final String userRoleName;
		private final OrderStatus newStatus;
		private final Boolean reviewedByPRCH;
		private final Boolean reviewedByAPV;
		private final String comments;
		private final boolean publicToMQ;

		public UpdateOrderByOrderNumberRunnable(OrderService orderService, String orderNumber, String userRoleName,
												OrderStatus newStatus, Boolean reviewedByPRCH, Boolean reviewedByAPV,
												String comments, boolean publicToMQ) {
			this.orderService = orderService;
			this.orderNumber = orderNumber;
			this.userRoleName = userRoleName;
			this.newStatus = newStatus;
			this.reviewedByPRCH = reviewedByPRCH;
			this.reviewedByAPV = reviewedByAPV;
			this.comments = comments;
			this.publicToMQ = publicToMQ;
		}

		@Override
		public void run() {
			try {
				orderService.updateOrderByOrderNumberSynchronized(
						orderNumber, userRoleName, newStatus, reviewedByPRCH, reviewedByAPV, comments, publicToMQ);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
