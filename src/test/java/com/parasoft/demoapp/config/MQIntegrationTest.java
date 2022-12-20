package com.parasoft.demoapp.config;

import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.global.preferences.DemoBugEntity;
import com.parasoft.demoapp.model.global.preferences.DemoBugsType;
import com.parasoft.demoapp.model.global.preferences.MqType;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import com.parasoft.demoapp.service.*;
import com.parasoft.demoapp.utilfortest.OrderUtilForTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;
import java.util.stream.Collectors;

import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_APPROVER;
import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        })
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
public class MQIntegrationTest {

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ItemInventoryService itemInventoryService;

    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

    /**
    *  Use the work flow of placing an order to test MQs.
    */
    @Test
    public void testOrderFlowAgainstDifferentMQs() throws Throwable {
        // Test against default MQ when PDA starts up
        assertEquals(MqType.ACTIVE_MQ, MQConfig.currentMQType);
        testOrderFlow();

        // Test against Kafka
        GlobalPreferencesDTO globalPreferencesDTO = getDefaultGlobalPreferencesDTO();
        globalPreferencesDTO.setMqType(MqType.KAFKA);
        globalPreferencesService.updateGlobalPreferences(globalPreferencesDTO);
        assertEquals(MqType.KAFKA, MQConfig.currentMQType);
        testOrderFlow();

        // Test against ActiveMQ
        globalPreferencesDTO = getDefaultGlobalPreferencesDTO();
        globalPreferencesDTO.setMqType(MqType.ACTIVE_MQ);
        globalPreferencesService.updateGlobalPreferences(globalPreferencesDTO);
        assertEquals(MqType.ACTIVE_MQ, MQConfig.currentMQType);
        testOrderFlow();
    }

    private void testOrderFlow() throws Throwable {
        // Get necessary data
        UserEntity purchaser = userService.getUserByUsername(USERNAME_PURCHASER);
        UserEntity approver = userService.getUserByUsername(USERNAME_APPROVER);
        ItemEntity blueSleepingBag = itemService.getItemByName("Blue Sleeping Bag");
        int originalInventoryOfBlueSleepingBag = itemInventoryService.getInStockByItemId(blueSleepingBag.getId());

        // Add cart item into cart
        int quantity = 1;
        shoppingCartService.addCartItemInShoppingCart(purchaser.getId(), blueSleepingBag.getId(), quantity);

        // Place an order
        OrderEntity order = orderService.addNewOrderSynchronized(purchaser.getId(),
                purchaser.getUsername(),
                RegionType.LOCATION_1,
                "29.90° E, 54.41° N",
                "receiverId",
                "eventId",
                "eventNumber");
        OrderUtilForTest.waitChangeForOrderStatus(order.getOrderNumber(), orderRepository, OrderStatus.SUBMITTED, 5);

        // Check order status
        order = orderService.getOrderByOrderNumber(order.getOrderNumber());
        assertEquals(OrderStatus.PROCESSED, order.getStatus());

        // Check inventory
        int updatedInventoryOfBlueSleepingBag = itemInventoryService.getInStockByItemId(blueSleepingBag.getId());
        assertEquals(originalInventoryOfBlueSleepingBag - quantity, updatedInventoryOfBlueSleepingBag);

        // Decline the order
        orderService.updateOrderByOrderNumberSynchronized(order.getOrderNumber(),
                approver.getRole().getName(),
                OrderStatus.DECLINED,
                false,
                true,
                approver.getUsername(),
                "Decline the order to roll back the inventory",
                true);
        OrderUtilForTest.waitChangeForItemInventory(blueSleepingBag.getId(), itemInventoryRepository, 9, 5);

        // Check order status
        order = orderService.getOrderByOrderNumber(order.getOrderNumber());
        assertEquals(OrderStatus.DECLINED, order.getStatus());

        // Check inventory
        updatedInventoryOfBlueSleepingBag = itemInventoryService.getInStockByItemId(blueSleepingBag.getId());
        assertEquals(originalInventoryOfBlueSleepingBag, updatedInventoryOfBlueSleepingBag);
    }

    private GlobalPreferencesDTO getDefaultGlobalPreferencesDTO() {
        GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
        globalPreferencesDto.setIndustryType(globalPreferencesDefaultSettingsService.defaultIndustry());
        globalPreferencesDto.setWebServiceMode(globalPreferencesDefaultSettingsService.defaultWebServiceMode());
        globalPreferencesDto.setGraphQLEndpoint(globalPreferencesDefaultSettingsService.defaultGraphQLEndpoint());
        globalPreferencesDto.setAdvertisingEnabled(globalPreferencesDefaultSettingsService.defaultAdvertisingEnabled());
        Set<DemoBugEntity> defaultDemoBugs = globalPreferencesDefaultSettingsService.defaultDemoBugs();
        globalPreferencesDto.setDemoBugs(defaultDemoBugs
                .stream()
                .map(DemoBugEntity::getDemoBugsType)
                .collect(Collectors.toList()).toArray(new DemoBugsType[defaultDemoBugs.size()]));
        globalPreferencesDto.setCategoriesRestEndpoint(globalPreferencesDefaultSettingsService.defaultCategoriesEndpoint().getUrl());
        globalPreferencesDto.setItemsRestEndpoint(globalPreferencesDefaultSettingsService.defaultItemsEndpoint().getUrl());
        globalPreferencesDto.setCartItemsRestEndpoint(globalPreferencesDefaultSettingsService.defaultCartItemsEndpoint().getUrl());
        globalPreferencesDto.setOrdersRestEndpoint(globalPreferencesDefaultSettingsService.defaultOrdersEndpoint().getUrl());
        globalPreferencesDto.setLocationsRestEndpoint(globalPreferencesDefaultSettingsService.defaultLocationsEndpoint().getUrl());
        globalPreferencesDto.setUseParasoftJDBCProxy(globalPreferencesDefaultSettingsService.defaultUseParasoftJDBCProxy());
        globalPreferencesDto.setParasoftVirtualizeServerUrl(globalPreferencesDefaultSettingsService.defaultParasoftVirtualizeServerUrl());
        globalPreferencesDto.setParasoftVirtualizeServerPath(globalPreferencesDefaultSettingsService.defaultParasoftVirtualizeServerPath());
        globalPreferencesDto.setParasoftVirtualizeGroupId(globalPreferencesDefaultSettingsService.defaultParasoftVirtualizeGroupId());
        globalPreferencesDto.setMqType(globalPreferencesDefaultSettingsService.defaultMqType());
        globalPreferencesDto.setOrderServiceSendTo(globalPreferencesDefaultSettingsService.defaultOrderServiceActiveMqRequestQueue());
        globalPreferencesDto.setOrderServiceListenOn(globalPreferencesDefaultSettingsService.defaultOrderServiceActiveMqResponseQueue());
        return globalPreferencesDto;
    }
}
