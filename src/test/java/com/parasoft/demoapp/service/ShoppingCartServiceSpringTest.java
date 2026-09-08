/**
 * 
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;

/**
 * Test for  ShoppingCartService
 *
 * @see com.parasoft.demoapp.service.ShoppingCartService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class ShoppingCartServiceSpringTest {

	@Autowired
	ShoppingCartService service;
	
	@Autowired
	ItemService itemService;
	
	@Autowired
	CategoryService categoryService;

	@Autowired
	UserService userService;

	@Autowired
	GlobalPreferencesService globalPreferencesService;

	/**
	 * Test for addCartItemInShoppingCart(Long, Long, Integer) under concurrency condition.
	 *
	 * @see com.parasoft.demoapp.service.ShoppingCartService#addCartItemInShoppingCart(Long, Long, Integer)
	 */
	@Test
	public void testAddCartItemInShoppingCart_concurrency() throws Throwable {
		// reset database of industry
		globalPreferencesService.resetAllIndustriesDatabase();

		CategoryEntity category = null;
		ItemEntity item = null;
		Long userId = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER).getId();
		ExecutorService executorService = Executors.newFixedThreadPool(30);
		CountDownLatch tasksReady = new CountDownLatch(30);
		CountDownLatch startTasks = new CountDownLatch(1);
		List<Future<Void>> tasks = new ArrayList<>();
		try {
			category = categoryService.addNewCategory("name", "description", "imagePath");
			item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
			final Long itemId = item.getId();

			// When
			Integer quantity = 1;
			for(int i = 0; i < 30; i++) {
				tasks.add(executorService.submit(() -> {
					tasksReady.countDown();
					if (!startTasks.await(30, TimeUnit.SECONDS)) {
						throw new IllegalStateException("Timed out waiting to start cart updates.");
					}
					service.addCartItemInShoppingCart(userId, itemId, quantity);
					return null;
				}));
			}

			assertTrue("Timed out waiting for concurrent cart updates to be ready.",
					tasksReady.await(30, TimeUnit.SECONDS));
			startTasks.countDown();
			for (Future<Void> task : tasks) {
				task.get(30, TimeUnit.SECONDS);
			}

			CartItemEntity cartItemEntity = service.getCartItemByUserIdAndItemId(userId, item.getId());

			// then
			assertEquals((Integer)30, cartItemEntity.getQuantity());
		}finally {
			startTasks.countDown();
			executorService.shutdownNow();
			executorService.awaitTermination(30, TimeUnit.SECONDS);
			if (item != null) {
				service.removeCartItemByUserIdAndItemId(userId, item.getId());
				itemService.removeItemById(item.getId());
			}
			if (category != null) {
				categoryService.removeCategory(category.getId());
			}
		}
	}
}
