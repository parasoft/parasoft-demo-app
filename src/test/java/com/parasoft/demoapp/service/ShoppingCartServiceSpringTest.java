/**
 * 
 */
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
		try {
			category = categoryService.addNewCategory("name", "description", "imagePath");
			item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);

			// When
			Integer quantity = 1;
			ExecutorService es = Executors.newCachedThreadPool();

			for(int i = 0; i < 30; i++) {
				es.submit(new AddCartItemInShoppingCartRunnable(userId, item.getId(), quantity));
			}

			Thread.sleep(5000);

			CartItemEntity cartItemEntity = service.getCartItemByUserIdAndItemId(userId, item.getId());

			// then
			assertEquals((Integer)30, cartItemEntity.getQuantity());
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			service.removeCartItemByUserIdAndItemId(userId, item.getId());
			itemService.removeItemById(item.getId());
			categoryService.removeCategory(category.getId());
		}
	}

	private class AddCartItemInShoppingCartRunnable implements Runnable{
		private final Integer quantity;
		private final Long userId;
		private final Long itemId;

		public AddCartItemInShoppingCartRunnable(Long userId, Long itemId, Integer quantity) {
			this.quantity = quantity;
			this.userId = userId;
			this.itemId = itemId;
		}

		@Override
		public void run() {
			try {
				service.addCartItemInShoppingCart(userId, itemId, quantity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}