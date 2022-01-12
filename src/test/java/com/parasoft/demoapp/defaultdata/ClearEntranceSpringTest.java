package com.parasoft.demoapp.defaultdata;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.global.GlobalPreferencesCreator;
import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.model.global.DatabaseInitResultEntity;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.global.DatabaseInitResultRepository;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
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
public class ClearEntranceSpringTest {
    @Autowired
    private DatabaseInitResultRepository databaseInitResultRepository;

    @Autowired
    UserService userService;

    @Autowired
    ResetEntrance resetEntrance;
    
    @Autowired
    ClearEntrance clearEntrance;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    OrderService orderService;

    @Autowired
    GlobalPreferencesService globalPreferencesService;

    @Test
    public void testRun() throws Exception {
        // Given
    	// switch database to DEFENSE
    	IndustryRoutingDataSource.currentIndustry = IndustryType.DEFENSE;
        // Prepare some data, and these data need to be reset when running tests method is done.
        String categoryName_defense = "categoryName";
        String categoryDescription_defense = "categoryDescription";
        String categoryImagePath_defense = "categoryImagePath";
        CategoryEntity category_defense = categoryService.addNewCategory(categoryName_defense, categoryDescription_defense, categoryImagePath_defense);

        String itemName_defense = "itemName";
        String itemDescription_defense = "itemDescription";
        Long itemCategoryId_defense = category_defense.getId();
        Integer itemInStock_defense = 10;
        String itemImagePath_defense = "itemImagePath";
        RegionType itemRegion_defense = RegionType.JAPAN;
        ItemEntity item_defense = itemService.addNewItem(
                itemName_defense, itemDescription_defense, itemCategoryId_defense, itemInStock_defense, itemImagePath_defense, itemRegion_defense);

        UserEntity user_defense = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
        int quantity_defense = 5;
        CartItemEntity cartItem_defense = shoppingCartService.addCartItemInShoppingCart(user_defense.getId(), item_defense.getId(), quantity_defense);
        
        RegionType region_defense = RegionType.JAPAN;
        String location_defense = "xxx";
        String receiverId_defense = "xxx";
        String eventId_defense = "xxx";
        String eventNumber_defense = "xxx";
        OrderEntity order_defense = orderService.addNewOrder(user_defense.getId(), region_defense, location_defense, receiverId_defense, eventId_defense, eventNumber_defense);
        // Prepare some data, and these data need to be reset when running tests method is done.
        // switch database to AEROSPACE
        IndustryRoutingDataSource.currentIndustry = IndustryType.AEROSPACE;
        String categoryName_aerospace = "categoryName";
        String categoryDescription_aerospace = "categoryDescription";
        String categoryImagePath_aerospace = "categoryImagePath";
        CategoryEntity category_aerospace = categoryService.addNewCategory(categoryName_aerospace, categoryDescription_aerospace, categoryImagePath_aerospace);

        String itemName_aerospace = "itemName";
        String itemDescription_aerospace = "itemDescription";
        Long itemCategoryId_aerospace = category_aerospace.getId();
        Integer itemInStock_aerospace = 10;
        String itemImagePath_aerospace = "itemImagePath";
        RegionType itemRegion_aerospace = RegionType.EARTH;
        ItemEntity item_aerospace = itemService.addNewItem(
                itemName_aerospace, itemDescription_aerospace, itemCategoryId_aerospace, itemInStock_aerospace, itemImagePath_aerospace, itemRegion_aerospace);

        UserEntity user_aerospace = userService.getUserByUsername(GlobalUsersCreator.USERNAME_PURCHASER);
        int quantity_aerospace = 5;
        CartItemEntity cartItem_aerospace = shoppingCartService.addCartItemInShoppingCart(user_aerospace.getId(), item_aerospace.getId(), quantity_aerospace);

        RegionType region_aerospace = RegionType.EARTH;
        String location_aerospace = "xxx";
        String receiverId_aerospace = "xxx";
        String eventId_aerospace = "xxx";
        String eventNumber_aerospace = "xxx";
        OrderEntity order_aerospace = orderService.addNewOrder(user_aerospace.getId(), region_aerospace, location_aerospace, receiverId_aerospace, eventId_aerospace, eventNumber_aerospace);

        // make sure all data is saved into database.
        assertNotNull(category_defense);
        assertNotNull(item_defense);
        assertNotNull(user_defense);
        assertNotNull(cartItem_defense);
        assertNotNull(order_defense);
        assertNotNull(category_aerospace);
        assertNotNull(item_aerospace);
        assertNotNull(user_aerospace);
        assertNotNull(cartItem_aerospace);
        assertNotNull(order_aerospace);

        // When
        // switch database to DEFENSE
    	IndustryRoutingDataSource.currentIndustry = IndustryType.DEFENSE;
        clearEntrance.run();

        // Then
        // Assert database is empty for current industry.
        String message = "";
        try{
            categoryService.getByCategoryId(category_defense.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);

        message = "";
        try{
            item_defense = itemService.getItemById(item_defense.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);

        message = "";
        try{
            shoppingCartService.getCartItemByUserIdAndItemId(user_defense.getId(), item_defense.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);

        message = "";
        try{
            orderService.getOrderByOrderNumber(order_defense.getOrderNumber());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertNotEquals("", message);
        
        // switch database to AEROSPACE
        // Assert database have no any changes for other industry.
    	IndustryRoutingDataSource.currentIndustry = IndustryType.AEROSPACE;
    	
    	message = "";
        try{
            categoryService.getByCategoryId(category_aerospace.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertEquals("", message);

        message = "";
        try{
            item_defense = itemService.getItemById(item_aerospace.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertEquals("", message);

        message = "";
        try{
            shoppingCartService.getCartItemByUserIdAndItemId(user_aerospace.getId(), item_aerospace.getId());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertEquals("", message);

        message = "";
        try{
            orderService.getOrderByOrderNumber(order_aerospace.getOrderNumber());
        }catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        assertEquals("", message);

        // to reset database
        globalPreferencesService.resetAllIndustriesDatabase();
        DatabaseInitResultEntity result = databaseInitResultRepository.findFirstByOrderByCreatedTimeDesc();
        assertNotNull(result.getLatestRecreatedTime());
    }

}
