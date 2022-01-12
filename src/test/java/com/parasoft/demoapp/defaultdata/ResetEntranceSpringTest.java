package com.parasoft.demoapp.defaultdata;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.defaultdata.global.GlobalPreferencesCreator;
import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.model.global.DatabaseInitResultEntity;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.global.DatabaseInitResultRepository;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.OrderService;
import com.parasoft.demoapp.service.ShoppingCartService;
import com.parasoft.demoapp.service.UserService;

/**
 * test class ResetEntrance
 *
 * @see GlobalPreferencesCreator
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class ResetEntranceSpringTest {
    @Autowired
    private DatabaseInitResultRepository databaseInitResultRepository;

    @Autowired
    UserService userService;

    @Autowired
    ResetEntrance resetEntrance;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    OrderService orderService;

    @Test
    public void testRun() throws Exception {

        // Given
        // Prepare some data, and these data need to be reset when running tests method is done.
        String categoryName = "categoryName";
        String categoryDescription = "categoryDescription";
        String categoryImagePath = "categoryImagePath";
        CategoryEntity category = categoryService.addNewCategory(categoryName, categoryDescription, categoryImagePath);

        String itemName = "itemName";
        String itemDescription = "itemDescription";
        Long itemCategoryId = category.getId();
        Integer itemInStock = 10;
        String itemImagePath = "itemImagePath";
        RegionType itemRegion = RegionType.LOCATION_1;
        ItemEntity item = itemService.addNewItem(
                itemName, itemDescription, itemCategoryId, itemInStock, itemImagePath, itemRegion);

        UserEntity user = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
        int quantity = 5;
        CartItemEntity cartItem = shoppingCartService.addCartItemInShoppingCart(user.getId(), item.getId(), quantity);

        RegionType region = RegionType.LOCATION_1;
        String location = "xxx";
        String receiverId = "xxx";
        String eventId = "xxx";
        String eventNumber = "xxx";
        OrderEntity order = orderService.addNewOrder(user.getId(), region, location, receiverId, eventId, eventNumber);

        // make sure all data is saved into database.
        assertNotNull(category);
        assertNotNull(item);
        assertNotNull(user);
        assertNotNull(cartItem);
        assertNotNull(order);

        // When
        resetEntrance.run();

        // Then
        // Assert data is reset.
        String message = "";
        try{
            categoryService.getByCategoryId(category.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);

        message = "";
        try{
            item = itemService.getItemById(item.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);

        message = "";
        try{
            shoppingCartService.getCartItemByUserIdAndItemId(user.getId(), item.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);

        message = "";
        try{
            orderService.getOrderByOrderNumber(order.getOrderNumber());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);

        DatabaseInitResultEntity result = databaseInitResultRepository.findFirstByOrderByCreatedTimeDesc();
        assertNotNull(result.getLatestRecreatedTime());
    }

}
