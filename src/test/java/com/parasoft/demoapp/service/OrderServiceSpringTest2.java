package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;

import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.*;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import com.parasoft.demoapp.utilfortest.OrderUtilForTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test for OrderService
 *
 * @see com.parasoft.demoapp.service.OrderService
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class OrderServiceSpringTest2 {
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
	 * <p>
	 * Test for addNewOrder(Long, String, RegionType, String, String, String, String, String) with transaction.<br/>
	 * Exception happens when clearing shopping cart, validate the database whether it is rollback or not.<br/>
	 * This test doesn't run automatically, we need insert a RuntimeException(or an exception that is tracked by a transaction)
	 * into business code(before return statement).
	 *
	 * like:
	 * </p>
	 *    <pre>&nbsp;&nbsp;@Transactional(value = "industryTransactionManager")
     *  public OrderEntity addNewOrder(xxx){
     *    //do some operations
     *    // ...
     *    throw new CartItemNotFoundException("on purpose"); // insert an exception before return.
     *    //return newOrder;  // comment the return statement.
     *  }
	 * </pre>
	 * Uncomment&nbsp;@Test and&nbsp;@RunWith(SpringJUnit4ClassRunner.class), then run this test.
	 * @see OrderService#addNewOrder(Long, String, RegionType, String, String, String, String, String)
	 */
    //@Test
    public void testAddNewOrder_rollbackWhenExceptionHappens() throws Throwable {
        CategoryEntity category = null;
        ItemEntity item = null;
        CartItemEntity cartItem = null;
        UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
        Long userId = user.getId();
        String requestedBy = user.getUsername();
        try {
        	// Given
            category = categoryService.addNewCategory("name", "description", "imagePath");
            item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.LOCATION_1);
            cartItem = shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 10);

            // When
            RegionType region = RegionType.LOCATION_1;
            String location = "JAPAN 82.8628° S, 135.0000° E";
            String shippingType = "Standard (1 - 2 weeks)";
            String receiverId = "345-6789-21";
            String eventId = "45833-ORG-7834";
            String eventNumber = "55-444-33-22";
            underTest.addNewOrder(userId, requestedBy, region, location, shippingType, receiverId, eventId, eventNumber);
        }catch(Exception e) {
        	e.printStackTrace();
        } finally {
        	assertEquals(cartItem.getQuantity(), shoppingCartService.getCartItemByUserIdAndItemId(userId, item.getId()).getQuantity());
        	assertEquals(1, shoppingCartService.getCartItemsByUserId(userId).size());
        	itemService.removeItemById(item.getId());
            categoryService.removeCategory(category.getId());
            shoppingCartService.clearShoppingCart(userId);
        }
    }
}
