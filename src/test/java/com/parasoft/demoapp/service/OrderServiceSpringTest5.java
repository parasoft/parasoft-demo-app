package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

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
//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class OrderServiceSpringTest5 {
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
     * Test for updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean) with transaction.<br/>
     * This test doesn't run automatically, we need insert a RuntimeException into business code(before return statement).
     *
     * like:
     * </p>
     *    <pre>&nbsp;&nbsp;@Transactional(value = "industryTransactionManager")
     *  public OrderEntity updateOrderByOrderNumber(xxx){
     *    //do some operations
     *    // ...
     *    throw new RuntimeException("on purpose"); // insert an exception before return.
     *    //return newOrder;  // comment the return statement.
     *  }
     * </pre>
     *Uncomment&nbsp;@Test and&nbsp;@RunWith(SpringJUnit4ClassRunner.class), then run this test.
     * @see com.parasoft.demoapp.service.OrderService#updateOrderByOrderNumber(String, String, OrderStatus, Boolean, Boolean, String, String, boolean)
     */
    //@Test
    public void testUpdateOrderByOrderNumber_rollbackWhenExceptionHappens() throws Throwable {
        // When
        Long userId = null;
        String requestedBy = null;
        CategoryEntity category = null;
        ItemEntity item = null;
        OrderEntity order = null;
        try {
            // Given
            UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
            userId = user.getId();
            requestedBy = user.getUsername();
            category = categoryService.addNewCategory("name", "description", "imagePath");
            item = itemService.addNewItem("name", "description", category.getId(), 30, "imagePath", RegionType.JAPAN);
            // add item into cart, the quantity of item is 20.
            shoppingCartService.addCartItemInShoppingCart(userId, item.getId(), 20);

            // When
            RegionType region = RegionType.JAPAN;
            String location = "JAPAN 82.8628° S, 135.0000° E";
            String shippingType = "Standard (1 - 2 weeks)";
            String receiverId = "345-6789-21";
            String eventId = "45833-ORG-7834";
            String eventNumber = "55-444-33-22";
            // after add a new order, the in stock of item is 10.
            order = underTest.addNewOrder(userId, requestedBy, region, location, shippingType, receiverId, eventId, eventNumber);

            String orderNumber = order.getOrderNumber();
            String userRoleName = RoleType.ROLE_APPROVER.toString();
            OrderStatus newStatus = OrderStatus.DECLINED;
            Boolean reviewedByPRCH = true;
            Boolean reviewedByAPV = true;
            String respondedBy = null;
            String comments = "reject";
            boolean publicToMQ = true;

            underTest.updateOrderByOrderNumber(
                    orderNumber, userRoleName, newStatus, reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            // Then
            assertEquals(1, orderRepository.findAll().size());
            assertEquals(OrderStatus.SUBMITTED, orderRepository.findOrderByOrderNumber(order.getOrderNumber()).getStatus());
            assertEquals(10, (int)itemService.getInStockById(item.getId()));
            itemService.removeItemById(item.getId());
            categoryService.removeCategory(category.getId());
            orderRepository.deleteAll();
        }
    }
}
