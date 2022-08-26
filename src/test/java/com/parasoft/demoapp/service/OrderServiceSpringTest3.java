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
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
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
public class OrderServiceSpringTest3 {
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
     * Test for addNewOrderSynchronized(Long, String, RegionType, String, String, String, String) under concurrency condition.
     *
     * @see OrderService#addNewOrderSynchronized(Long, String, RegionType, String, String, String, String)
     */
    @Test
    public void testAddNewOrderSynchronized_concurrency() throws Throwable {
		Long userId = null;
		String requestedBy = null;
		CategoryEntity category = null;
		ItemEntity item = null;
		try {
			// Given
			userId = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER).getId();
			requestedBy = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER).getUsername();
			category = categoryService.addNewCategory("name", "description", "imagePath");
			item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
			// add item into cart, the quantity of item is 20.
			shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 10);

			// When
			RegionType region = RegionType.LOCATION_1;
			String location = "JAPAN 82.8628° S, 135.0000° E";
			String receiverId = "345-6789-21";
			String eventId = "45833-ORG-7834";
			String eventNumber = "55-444-33-22";

			ExecutorService es = Executors.newCachedThreadPool();

			// use 3 threads to simulate the concurrency situation, submit orders repeatedly in the same time.
			for (int i = 0; i < 3; i++) {
				es.submit(new AddNewOrderRunnable(underTest, userId, requestedBy, region, location, receiverId, eventId, eventNumber));
			}

			Thread.sleep(2000);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			// Then
			assertEquals(1, orderRepository.findAll().size());
			assertEquals(20, (int)itemService.getInStockById(item.getId()));
			itemService.removeItemById(item.getId());
			categoryService.removeCategory(category.getId());
			orderRepository.deleteAll();
		}
    }
    
    private class AddNewOrderRunnable implements Runnable {
    	private final OrderService orderService;
        private final Long userId;
    	private final String requestedBy;
    	private final RegionType region;
    	private final String location;
    	private final String receiverId;
    	private final String eventId;
    	private final String eventNumber;
        
		public AddNewOrderRunnable(OrderService orderService, Long userId, String requestedBy, RegionType region, String location,
									String receiverId, String eventId, String eventNumber) {
			
			this.orderService = orderService;
			this.userId = userId;
			this.requestedBy = requestedBy;
			this.region = region;
			this.location = location;
			this.receiverId = receiverId;
			this.eventId = eventId;
			this.eventNumber = eventNumber;
		}

		@Override
		public void run() {
			try {
				orderService.addNewOrderSynchronized(userId, requestedBy, region, location, receiverId, eventId, eventNumber);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	
    }
}
