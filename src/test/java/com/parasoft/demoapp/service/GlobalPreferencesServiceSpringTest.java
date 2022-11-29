/**
 *
 */
package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.preferences.*;
import com.parasoft.demoapp.repository.industry.*;
import com.parasoft.demoapp.util.UrlUtil;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;
import static com.parasoft.demoapp.config.activemq.ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST;
import static com.parasoft.demoapp.config.activemq.ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * Test class for GlobalPreferencesService
 *
 * @see com.parasoft.demoapp.service.GlobalPreferencesService
 */
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.*", "javax.xml.*", "org.xml.*", "javax.management.*", "javax.net.*" })
@PrepareForTest(UrlUtil.class)
@SpringBootTest
public class GlobalPreferencesServiceSpringTest {

	// Component under test
	@Autowired
	GlobalPreferencesService service;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	ShoppingCartRepository shoppingCartRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	RouteLocator routeLocator;

    @Autowired
    GlobalPreferencesDefaultSettingsService defaultSettingsService;

	@MockBean
	ParasoftJDBCProxyService parasoftJDBCProxyService;

	@Autowired
	private WebConfig webConfig;

	/**
	 * Test for industry change
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	@Transactional(value = "globalTransactionManager")
	public void testUpdateGlobalPreferences_industryChange() throws Throwable {
		// Give
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		IndustryType industry = IndustryType.AEROSPACE;
		globalPreferencesDto.setIndustryType(industry);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
		globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);

		// When
		GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.AEROSPACE, result.getIndustryType());

		// Give
		industry = IndustryType.HEALTHCARE; // test point, HEALTHCARE is not implemented.
		globalPreferencesDto.setIndustryType(industry);

		// When
		String message = "";
		try {
			service.updateGlobalPreferences(globalPreferencesDto);
		}catch (Exception e){
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(GlobalPreferencesMessages.INDUSTRY_HAS_NOT_IMPLEMENTED, industry), message);
	}

	/**
	 * Test for refreshEndpoint()
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	@Transactional(value = "globalTransactionManager")
	public void testUpdateGlobalPreferences_refreshEndpoint() throws Throwable {

		PowerMockito.mockStatic(UrlUtil.class);
		PowerMockito.doReturn(true).when(UrlUtil.class, "isGoodHttpForm", anyString());

		// Give
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.DEFENSE);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
		globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);

		// When
		GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.DEFENSE, IndustryRoutingDataSource.currentIndustry);
		assertEquals(IndustryType.DEFENSE, result.getIndustryType());
		assertEquals(5, result.getRestEndPoints().size());

		Map<String, RestEndpointEntity> endpoints = new HashMap<>(); // Map<url,RestEndpointEntity>
		for (RestEndpointEntity endpoint : result.getRestEndPoints()) {
			endpoints.put(endpoint.getUrl(), endpoint);
		}
		assertTrue(endpoints.containsKey(categoriesRestEndpointUrl));
		RestEndpointEntity categoriesRestEndpoint = endpoints.get(categoriesRestEndpointUrl);
		assertEquals(GlobalPreferencesDefaultSettingsService.CATEGORIES_ENDPOINT_ID,
				categoriesRestEndpoint.getRouteId());
		assertEquals(GlobalPreferencesDefaultSettingsService.CATEGORIES_ENDPOINT_PATH,
				categoriesRestEndpoint.getPath());

		assertTrue(endpoints.containsKey(itemsRestEndpointUrl));
		RestEndpointEntity itemsRestEndpoint = endpoints.get(itemsRestEndpointUrl);
		assertEquals(GlobalPreferencesDefaultSettingsService.ITEMS_ENDPOINT_ID, itemsRestEndpoint.getRouteId());
		assertEquals(GlobalPreferencesDefaultSettingsService.ITEMS_ENDPOINT_PATH, itemsRestEndpoint.getPath());

		assertTrue(endpoints.containsKey(cartItemsRestEndpointUrl));
		RestEndpointEntity cartItemsRestEndpoint = endpoints.get(cartItemsRestEndpointUrl);
		assertEquals(GlobalPreferencesDefaultSettingsService.CART_ENDPOINT_ID, cartItemsRestEndpoint.getRouteId());
		assertEquals(GlobalPreferencesDefaultSettingsService.CART_ENDPOINT_PATH, cartItemsRestEndpoint.getPath());

		assertTrue(endpoints.containsKey(ordersRestEndpointUrl));
		RestEndpointEntity ordersRestEndpoint = endpoints.get(ordersRestEndpointUrl);
		assertEquals(GlobalPreferencesDefaultSettingsService.ORDERS_ENDPOINT_ID, ordersRestEndpoint.getRouteId());
		assertEquals(GlobalPreferencesDefaultSettingsService.ORDERS_ENDPOINT_PATH, ordersRestEndpoint.getPath());

		assertTrue(endpoints.containsKey(locationsRestEndpointUrl));
		RestEndpointEntity locationsRestEndpoint = endpoints.get(locationsRestEndpointUrl);
		assertEquals(GlobalPreferencesDefaultSettingsService.LOCATIONS_ENDPOINT_ID, locationsRestEndpoint.getRouteId());
		assertEquals(GlobalPreferencesDefaultSettingsService.LOCATIONS_ENDPOINT_PATH, locationsRestEndpoint.getPath());

		List<Route> routes = routeLocator.getRoutes();
		assertNotNull(routes);

		Map<String, Route> routeMap = new HashMap<>();
		for (Route route : routes) {
			routeMap.put(route.getId(), route);
		}

		Route locationsRoute = routeMap.get(LOCATIONS_ENDPOINT_ID);
		assertNotNull(locationsRoute);
		assertEquals(LOCATIONS_ENDPOINT_PATH, locationsRoute.getFullPath());
		assertEquals(LOCATIONS_ENDPOINT_PATH, locationsRoute.getFullPath());
		assertEquals(locationsRestEndpointUrl, locationsRoute.getLocation());
		assertEquals(0, locationsRoute.getSensitiveHeaders().size());
		assertFalse(locationsRoute.getRetryable());
		assertTrue(locationsRoute.isCustomSensitiveHeaders());
		assertTrue(locationsRoute.isPrefixStripped());

		Route ordersRoute = routeMap.get(ORDERS_ENDPOINT_ID);
		assertNotNull(ordersRoute);
		assertEquals(ORDERS_ENDPOINT_PATH, ordersRoute.getFullPath());
		assertEquals(ORDERS_ENDPOINT_PATH, ordersRoute.getFullPath());
		assertEquals(ordersRestEndpointUrl, ordersRoute.getLocation());
		assertEquals(0, ordersRoute.getSensitiveHeaders().size());
		assertFalse(ordersRoute.getRetryable());
		assertTrue(ordersRoute.isCustomSensitiveHeaders());
		assertTrue(ordersRoute.isPrefixStripped());

		Route cartItemsRoute = routeMap.get(CART_ENDPOINT_ID);
		assertNotNull(cartItemsRoute);
		assertEquals(CART_ENDPOINT_PATH, cartItemsRoute.getFullPath());
		assertEquals(CART_ENDPOINT_PATH, cartItemsRoute.getFullPath());
		assertEquals(cartItemsRestEndpointUrl, cartItemsRoute.getLocation());
		assertEquals(0, cartItemsRoute.getSensitiveHeaders().size());
		assertFalse(cartItemsRoute.getRetryable());
		assertTrue(cartItemsRoute.isCustomSensitiveHeaders());
		assertTrue(cartItemsRoute.isPrefixStripped());

		Route categoriesRoute = routeMap.get(CATEGORIES_ENDPOINT_ID);
		assertNotNull(categoriesRoute);
		assertEquals(CATEGORIES_ENDPOINT_PATH, categoriesRoute.getFullPath());
		assertEquals(CATEGORIES_ENDPOINT_PATH, categoriesRoute.getFullPath());
		assertEquals(categoriesRestEndpointUrl, categoriesRoute.getLocation());
		assertEquals(0, categoriesRoute.getSensitiveHeaders().size());
		assertFalse(categoriesRoute.getRetryable());
		assertTrue(categoriesRoute.isCustomSensitiveHeaders());
		assertTrue(categoriesRoute.isPrefixStripped());

		Route itemsRoute = routeMap.get(ITEMS_ENDPOINT_ID);
		assertNotNull(itemsRoute);
		assertEquals(ITEMS_ENDPOINT_PATH, itemsRoute.getFullPath());
		assertEquals(itemsRestEndpointUrl, itemsRoute.getLocation());
		assertEquals(0, itemsRoute.getSensitiveHeaders().size());
		assertFalse(itemsRoute.getRetryable());
		assertTrue(itemsRoute.isCustomSensitiveHeaders());
		assertTrue(itemsRoute.isPrefixStripped());
	}

	/**
	 * Test for refreshEndpoint()
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void UpdateGlobalPreferences_refreshEndpoint_useDefaultEndpointsWhenNoRestEndpointInDatabase()
			throws Throwable {
		// Given
		// Remove all existing endpoints
		String categoriesRestEndpointUrl = ""; // Application will not save route with empty url.
		String itemsRestEndpointUrl = "";
		String cartItemsRestEndpointUrl = "";
		String ordersRestEndpointUrl = "";
		String locationsRestEndpointUrl = "";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.DEFENSE);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
		globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);

		// When
		// After updating the preferences, project will apply default endpoint routes to Zuul
		GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

		// Then
		// No rest endpoints in db
		assertEquals(0, result.getRestEndPoints().size());

		// Get all routes and do assertion
		List<Route> routes = routeLocator.getRoutes();
		assertNotNull(routes);

		Map<String, Route> routeMap = new HashMap<>();
		for (Route route : routes) {
			routeMap.put(route.getId(), route);
		}

		String host = HOST + webConfig.getServerPort();

		Route locationsRoute = routeMap.get(LOCATIONS_ENDPOINT_ID);
		assertNotNull(locationsRoute);
		assertEquals(LOCATIONS_ENDPOINT_PATH, locationsRoute.getFullPath());
		assertEquals(LOCATIONS_ENDPOINT_PATH, locationsRoute.getFullPath());
		assertEquals(host + LOCATIONS_ENDPOINT_REAL_PATH, locationsRoute.getLocation());
		assertEquals(0, locationsRoute.getSensitiveHeaders().size());
		assertFalse(locationsRoute.getRetryable());
		assertTrue(locationsRoute.isCustomSensitiveHeaders());
		assertTrue(locationsRoute.isPrefixStripped());

		Route ordersRoute = routeMap.get(ORDERS_ENDPOINT_ID);
		assertNotNull(ordersRoute);
		assertEquals(ORDERS_ENDPOINT_PATH, ordersRoute.getFullPath());
		assertEquals(ORDERS_ENDPOINT_PATH, ordersRoute.getFullPath());
		assertEquals(host + ORDERS_ENDPOINT_REAL_PATH, ordersRoute.getLocation());
		assertEquals(0, ordersRoute.getSensitiveHeaders().size());
		assertFalse(ordersRoute.getRetryable());
		assertTrue(ordersRoute.isCustomSensitiveHeaders());
		assertTrue(ordersRoute.isPrefixStripped());

		Route cartItemsRoute = routeMap.get(CART_ENDPOINT_ID);
		assertNotNull(cartItemsRoute);
		assertEquals(CART_ENDPOINT_PATH, cartItemsRoute.getFullPath());
		assertEquals(CART_ENDPOINT_PATH, cartItemsRoute.getFullPath());
		assertEquals(host + CART_ENDPOINT_REAL_PATH, cartItemsRoute.getLocation());
		assertEquals(0, cartItemsRoute.getSensitiveHeaders().size());
		assertFalse(cartItemsRoute.getRetryable());
		assertTrue(cartItemsRoute.isCustomSensitiveHeaders());
		assertTrue(cartItemsRoute.isPrefixStripped());

		Route categoriesRoute = routeMap.get(CATEGORIES_ENDPOINT_ID);
		assertNotNull(categoriesRoute);
		assertEquals(CATEGORIES_ENDPOINT_PATH, categoriesRoute.getFullPath());
		assertEquals(CATEGORIES_ENDPOINT_PATH, categoriesRoute.getFullPath());
		assertEquals(host + CATEGORIES_ENDPOINT_REAL_PATH, categoriesRoute.getLocation());
		assertEquals(0, categoriesRoute.getSensitiveHeaders().size());
		assertFalse(categoriesRoute.getRetryable());
		assertTrue(categoriesRoute.isCustomSensitiveHeaders());
		assertTrue(categoriesRoute.isPrefixStripped());

		Route itemsRoute = routeMap.get(ITEMS_ENDPOINT_ID);
		assertNotNull(itemsRoute);
		assertEquals(ITEMS_ENDPOINT_PATH, itemsRoute.getFullPath());
		assertEquals(host + ITEMS_ENDPOINT_REAL_PATH, itemsRoute.getLocation());
		assertEquals(0, itemsRoute.getSensitiveHeaders().size());
		assertFalse(itemsRoute.getRetryable());
		assertTrue(itemsRoute.isCustomSensitiveHeaders());
		assertTrue(itemsRoute.isPrefixStripped());
	}

	/**
	 * Test for demoBugs introduction
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	@Transactional(value = "globalTransactionManager")
	public void testUpdateGlobalPreferences_demoBugsIntroduction() throws Throwable {
		// Give
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		DemoBugsType[] demoBugsTypes = { DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS };
		globalPreferencesDto.setDemoBugs(demoBugsTypes);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
		globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);

		// When
		GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.AEROSPACE, result.getIndustryType());
		assertEquals(1, result.getDemoBugs().size());

		assertEquals(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS,
				result.getDemoBugs().iterator().next().getDemoBugsType());

		// Give
		demoBugsTypes = null;
		globalPreferencesDto.setDemoBugs(demoBugsTypes); // test point

		// When
		result = service.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(0, result.getDemoBugs().size());
	}

	/**
	 * Test for using Parasoft JDBC Proxy
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	@Transactional(value = "globalTransactionManager")
	public void testUpdateGlobalPreferences_useParasoftJDBCProxy() throws Throwable {

		PowerMockito.mockStatic(UrlUtil.class);
		PowerMockito.doReturn(true).when(UrlUtil.class, "isGoodHttpForm", anyString());

		// Give
		boolean useParasoftJDBCProxy = true;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		doNothing().when(parasoftJDBCProxyService).validateVirtualizeServerUrl(anyString());

		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.DEFENSE);
		globalPreferencesDto.setUseParasoftJDBCProxy(useParasoftJDBCProxy);
		globalPreferencesDto.setParasoftVirtualizeServerUrl(parasoftVirtualizeServerUrl);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
		globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);

		// When
		GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.DEFENSE, IndustryRoutingDataSource.currentIndustry);
		assertEquals(IndustryType.DEFENSE, result.getIndustryType());
		assertTrue(IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected);
		assertEquals(parasoftVirtualizeServerUrl, IndustryRoutingDataSource.parasoftVirtualizeServerUrl);
		assertTrue(IndustryRoutingDataSource.useParasoftJDBCProxy);

		// Finally
		IndustryRoutingDataSource.useParasoftJDBCProxy = false;
	}

	/**
	 * Test for using Parasoft JDBC Proxy
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	@Transactional(value = "globalTransactionManager")
	public void testUpdateGlobalPreferences_useParasoftJDBCProxy_defaultVirtualizeServerUrl() throws Throwable {

		PowerMockito.mockStatic(UrlUtil.class);
		PowerMockito.doReturn(true).when(UrlUtil.class, "isGoodHttpForm", anyString());

		// Give
		boolean useParasoftJDBCProxy = true;
		String parasoftVirtualizeServerUrl = ""; // test point, when blank using default url.
		doNothing().when(parasoftJDBCProxyService).validateVirtualizeServerUrl(anyString());

		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.DEFENSE);
		globalPreferencesDto.setUseParasoftJDBCProxy(useParasoftJDBCProxy);
		globalPreferencesDto.setParasoftVirtualizeServerUrl(parasoftVirtualizeServerUrl);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
		globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);

		// When
		GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertTrue(IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected);
		assertTrue(IndustryRoutingDataSource.useParasoftJDBCProxy);
		assertEquals(PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE, IndustryRoutingDataSource.parasoftVirtualizeServerUrl);

		// Finally
		IndustryRoutingDataSource.useParasoftJDBCProxy = false;
	}

	/**
	 * Test for demoBugs introduction
	 *
	 * @see GlobalPreferencesService#clearCurrentIndustryDatabase()
	 */
	@Test
	public void testClearCurrentIndustryDatabase() throws Throwable {
		// Given
		service.resetAllIndustriesDatabase();

		// When
		service.clearCurrentIndustryDatabase();

		// Then
		assertEquals(0, categoryRepository.findAll().size());
		assertEquals(0, itemRepository.findAll().size());
		assertEquals(0, shoppingCartRepository.findAll().size());
		assertEquals(0, orderRepository.findAll().size());
		assertNotEquals(0, locationRepository.findAll().size()); // location data is not clear

		// Finally
		service.resetAllIndustriesDatabase();
	}

	/**
	 * Test for Label Overrided.
	 *
	 * @see GlobalPreferencesService#updateLabelOverridedStatus(Boolean)
	 */
	@Test
	@Transactional(value = "globalTransactionManager")
	public void testUpdateLabelOverridedStatus() throws Throwable {
		// Given
		Boolean labelOverridedStatus = true;

		// When
		GlobalPreferencesEntity globalPreferencesEntity = service.updateLabelOverridedStatus(labelOverridedStatus);

		// Then
		assertEquals(labelOverridedStatus, globalPreferencesEntity.isLabelsOverrided());

		// When
		boolean labelOverridedStatusFromDB = service.getLabelOverridedStatus();

		// Then
		assertEquals(labelOverridedStatus, labelOverridedStatusFromDB);
	}

    /**
     * Test for updateGraphQLEndpoint()
     *
     * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
     */
    @Test
    @Transactional(value = "globalTransactionManager")
    public void testUpdateGlobalPreferences_updateGraphQLEndpoint() throws Throwable {
        // Give
        String graphQLEndPoint = "http://localhost:8081/other";
        GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
        globalPreferencesDto.setIndustryType(IndustryType.DEFENSE);
        globalPreferencesDto.setGraphQLEndpoint(graphQLEndPoint);
        globalPreferencesDto.setAdvertisingEnabled(false);
        globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
        globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
        globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
        globalPreferencesDto.setWebServiceMode(WebServiceMode.GRAPHQL);

        // When
        GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

        // Then
        assertNotNull(result);
        assertEquals(IndustryType.DEFENSE, IndustryRoutingDataSource.currentIndustry);
        assertEquals(IndustryType.DEFENSE, result.getIndustryType());
        assertEquals(graphQLEndPoint, result.getGraphQLEndpoint());
    }

    /**
     * Test for updateGraphQLEndpoint()
     *
     * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
     */
    @Test
    public void UpdateGlobalPreferences_updateGraphQLEndpoint_emptyEndpoint()
            throws Throwable {
        // Give
        String graphQLEndPoint = "";
        GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
        globalPreferencesDto.setIndustryType(IndustryType.DEFENSE);
        globalPreferencesDto.setGraphQLEndpoint(graphQLEndPoint);
        globalPreferencesDto.setAdvertisingEnabled(false);
        globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
        globalPreferencesDto.setOrderServiceDestinationQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
        globalPreferencesDto.setOrderServiceReplyToQueue(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
        globalPreferencesDto.setWebServiceMode(WebServiceMode.GRAPHQL);

        // When
        GlobalPreferencesEntity result = service.updateGlobalPreferences(globalPreferencesDto);

        // Then
        assertNotNull(result);
        assertEquals(IndustryType.DEFENSE, IndustryRoutingDataSource.currentIndustry);
        assertEquals(IndustryType.DEFENSE, result.getIndustryType());
        assertEquals(defaultSettingsService.defaultGraphQLEndpoint(), result.getGraphQLEndpoint());
    }
}