package com.parasoft.demoapp.config.kafka;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.global.preferences.DemoBugEntity;
import com.parasoft.demoapp.model.global.preferences.DemoBugsType;
import com.parasoft.demoapp.model.global.preferences.MqType;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import com.parasoft.demoapp.service.*;
import com.parasoft.demoapp.utilfortest.OrderUtilForTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator.USERNAME_PURCHASER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://127.0.0.1:9092",
                "port=9092"
        })
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
public class KafkaIntegrationTest {

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
    KafkaTemplate<String, InventoryOperationRequestMessageDTO> operationRequestKafkaTemplate;

    /**
    *  Use the work flow of placing an order to test the topics of Kafka.
    */
    @Test
    public void testOrderFlowAgainstKafka() throws Throwable {
        GlobalPreferencesDTO globalPreferencesDTO = getDefaultGlobalPreferencesDTO();
        globalPreferencesDTO.setMqType(MqType.KAFKA);
        globalPreferencesService.updateGlobalPreferences(globalPreferencesDTO);

       /* System.out.println("++++++++++++++++++++");
        operationRequestKafkaTemplate.send(KafkaConfig.DEFAULT_ORDER_SERVICE_REQUEST_TOPIC,
                new InventoryOperationRequestMessageDTO(InventoryOperation.DECREASE,
                        "123",
                        Collections.singletonList(new InventoryInfoDTO(1L, 1)),
                        ""));
        System.out.println("++++++++++++++++++++");*/

        // Change to use Kafka
        assertEquals(MqType.KAFKA, MQConfig.currentMQType);

        // Get necessary data
        UserEntity purchaser = userService.getUserByUsername(USERNAME_PURCHASER);
        ItemEntity blueSleepingBag = itemService.getItemByName("Blue Sleeping Bag");
        Integer originalInventoryOfBlueSleepingBag  = itemInventoryService.getInStockByItemId(blueSleepingBag.getId());

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
        Integer updatedInventoryOfBlueSleepingBag  = itemInventoryService.getInStockByItemId(blueSleepingBag.getId());
        assertEquals(originalInventoryOfBlueSleepingBag - quantity, updatedInventoryOfBlueSleepingBag);
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
        globalPreferencesDto.setUseParasoftJDBCProxy(globalPreferencesDefaultSettingsService.defaultUseParasoftJDBCProxy());
        globalPreferencesDto.setParasoftVirtualizeServerUrl(globalPreferencesDefaultSettingsService.defaultParasoftVirtualizeServerUrl());
        globalPreferencesDto.setParasoftVirtualizeServerPath(globalPreferencesDefaultSettingsService.defaultParasoftVirtualizeServerPath());
        globalPreferencesDto.setParasoftVirtualizeGroupId(globalPreferencesDefaultSettingsService.defaultParasoftVirtualizeGroupId());
        globalPreferencesDto.setMqType(globalPreferencesDefaultSettingsService.defaultMqType());
        globalPreferencesDto.setOrderServiceDestinationQueue(globalPreferencesDefaultSettingsService.defaultOrderServiceDestinationQueue());
        globalPreferencesDto.setOrderServiceReplyToQueue(globalPreferencesDefaultSettingsService.defaultOrderServiceReplyToQueue());
        globalPreferencesDto.setInventoryServiceRequestTopic(globalPreferencesDefaultSettingsService.defaultOrderServiceRequestTopic());
        globalPreferencesDto.setInventoryServiceResponseTopic(globalPreferencesDefaultSettingsService.defaultOrderServiceResponseTopic());
        return globalPreferencesDto;
    }
}
