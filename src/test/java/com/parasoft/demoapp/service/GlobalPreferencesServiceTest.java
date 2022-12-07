package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.activemq.InventoryRequestQueueListener;
import com.parasoft.demoapp.config.activemq.InventoryResponseQueueListener;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.config.kafka.InventoryRequestTopicListener;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.preferences.*;
import com.parasoft.demoapp.repository.global.GlobalPreferencesRepository;
import com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs;
import com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.MessageFormat;
import java.util.*;

import static com.parasoft.demoapp.config.activemq.ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST;
import static com.parasoft.demoapp.config.activemq.ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE;
import static com.parasoft.demoapp.config.kafka.KafkaConfig.DEFAULT_ORDER_SERVICE_REQUEST_TOPIC;
import static com.parasoft.demoapp.config.kafka.KafkaConfig.DEFAULT_ORDER_SERVICE_RESPONSE_TOPIC;
import static com.parasoft.demoapp.messages.GlobalPreferencesMessages.ORDER_SERVICE_REQUEST_TOPIC_CANNOT_BE_NULL;
import static com.parasoft.demoapp.messages.GlobalPreferencesMessages.ORDER_SERVICE_RESPONSE_TOPIC_CANNOT_BE_NULL;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * test class for GlobalPreferencesService
 *
 * @see GlobalPreferencesService
 */
public class GlobalPreferencesServiceTest {

	@InjectMocks
	GlobalPreferencesService underTest;

	@Mock
	GlobalPreferencesRepository globalPreferencesRepository;

	@Mock
	RestEndpointService restEndpointService;

	@Mock
	EndpointService endpointService;

	@Mock
	DemoBugService demoBugService;

	@Mock
	private ParasoftJDBCProxyService parasoftJDBCProxyDriverService;

	@Mock
	InventoryResponseQueueListener inventoryResponseQueueListener;

	@Mock
	InventoryRequestTopicListener inventoryRequestTopicListener;

	@Mock
	InventoryRequestQueueListener inventoryRequestQueueListener;

	@Mock
	WebConfig webConfig;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() {
		ActiveMQConfig.setOrderServiceListenToQueue(DEFAULT_QUEUE_INVENTORY_RESPONSE);
		ActiveMQConfig.setOrderServiceSendToQueue(new ActiveMQQueue(DEFAULT_QUEUE_INVENTORY_REQUEST));
		KafkaConfig.setOrderServiceSendToTopic(DEFAULT_ORDER_SERVICE_REQUEST_TOPIC);
		KafkaConfig.setOrderServiceListenToTopic(DEFAULT_ORDER_SERVICE_RESPONSE_TOPIC);
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_normal() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());
		IndustryType industryType = IndustryType.DEFENSE;
		WebServiceMode webServiceMode = WebServiceMode.REST_API;
		String graphQLEndpoint = "https://localhost:8080/graphql";
		DemoBugEntity demoBug = new DemoBugEntity(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS);
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		demoBugSet.add(demoBug);
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "inventory.request";
		String orderServiceReplyToQueue = "inventory.response";
		String orderServiceRequestTopic = "inventory.request";
		String orderServiceResponseTopic = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);

		// Then
		assertNotNull(result);
		assertEquals(DataAccessMode.JDBC.getValue(), result.getDataAccessMode().getValue());
		assertEquals("", result.getSoapEndPoint());
		assertNotNull(result.getDemoBugs());
		assertNotNull(result.getRestEndPoints());
		assertEquals(IndustryType.DEFENSE.getValue(), result.getIndustryType().getValue());
		assertEquals(false, result.getAdvertisingEnabled());
		assertEquals(mqType, result.getMqType());
		assertEquals(orderServiceDestinationQueue, result.getOrderServiceDestinationQueue());
		assertEquals(orderServiceReplyToQueue, result.getOrderServiceReplyToQueue());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_nullDemoBugs() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());
		IndustryType industryType = IndustryType.DEFENSE;
		WebServiceMode webServiceMode = WebServiceMode.REST_API;
		String graphQLEndpoint = "https://localhost:8080/graphql";
		Set<DemoBugEntity> demoBugSet = null;
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "inventory.request";
		String orderServiceReplyToQueue = "inventory.response";
		String orderServiceRequestTopic = "inventory.request";
		String orderServiceResponseTopic = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);

		// Then
		assertNotNull(result);
		assertNull(result.getDemoBugs());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_nullRestEndPoints() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = null;
		IndustryType industryType = IndustryType.DEFENSE;
		WebServiceMode webServiceMode = WebServiceMode.REST_API;
		String graphQLEndpoint = "https://localhost:8080/graphql";
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "inventory.request";
		String orderServiceReplyToQueue = "inventory.response";
		String orderServiceRequestTopic = "inventory.request";
		String orderServiceResponseTopic = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);

		// Then
		assertNotNull(result);
		assertEquals(restEndPoints, result.getRestEndPoints());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_nullAdvertisingEnabled() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());
		IndustryType industryType = IndustryType.DEFENSE;
		WebServiceMode webServiceMode = WebServiceMode.REST_API;
		String graphQLEndpoint = "https://localhost:8080/graphql";
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		Boolean advertisingEnabled = null;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "inventory.request";
		String orderServiceReplyToQueue = "inventory.response";
		String orderServiceRequestTopic = "inventory.request";
		String orderServiceResponseTopic = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints, industryType, webServiceMode, graphQLEndpoint,
					demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
					parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
					orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(GlobalPreferencesMessages.ADVERTISING_ENABLED_CANNOT_BE_NULL, message);
	}

	/**
	 * test for getCurrentGlobalPreferences()
	 *
	 * @see GlobalPreferencesService#getCurrentGlobalPreferences()
	 */
	@Test
	public void testGetCurrentGlobalPreferences_normal() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());
		IndustryType industryType = IndustryType.DEFENSE;
		WebServiceMode webServiceMode = WebServiceMode.REST_API;
		String graphQLEndpoint = "https://localhost:8080/graphql";
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "inventory.request";
		String orderServiceReplyToQueue = "inventory.response";
		String orderServiceRequestTopic = "inventory.request";
		String orderServiceResponseTopic = "inventory.response";

		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, orderServiceRequestTopic, orderServiceResponseTopic);

		when(globalPreferencesRepository.findAll()).thenReturn(Arrays.asList(globalPreferencesEntity));

		// When
		GlobalPreferencesEntity result = underTest.getCurrentGlobalPreferences();

		// Then
		assertNotNull(result);
	}

	/**
	 * test for getCurrentGlobalPreferences()
	 *
	 * @see GlobalPreferencesService#getCurrentGlobalPreferences()
	 */
	@Test
	public void testGetCurrentGlobalPreferences_globalPreferencesNotFoundException() throws Throwable {
		// Given
		when(globalPreferencesRepository.findAll()).thenReturn(new ArrayList<GlobalPreferencesEntity>());

		// When
		String message = "";
		try {
			underTest.getCurrentGlobalPreferences();
		} catch (GlobalPreferencesNotFoundException e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(GlobalPreferencesMessages.THERE_IS_NO_CURRENT_GLOBAL_PREFERENCES, message);
	}

	/**
	 * test for getCurrentGlobalPreferences()
	 *
	 * @see GlobalPreferencesService#getCurrentGlobalPreferences()
	 */
	@Test
	public void testGetCurrentGlobalPreferences_globalPreferencesMoreThanOneException() throws Throwable {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		findAllResult.add(new GlobalPreferencesEntity());
		findAllResult.add(new GlobalPreferencesEntity());
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);

		// When
		String message = "";
		try {
			underTest.getCurrentGlobalPreferences();
		} catch (GlobalPreferencesMoreThanOneException e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(GlobalPreferencesMessages.THERE_ARE_MORE_THAN_ONE_PREFERENCES,
				findAllResult.size()), message);
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_normal_with_jms() throws Throwable {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryRequestTopicListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceSendTo("proxy.queue.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("proxy.queue.inventory.response");

		GlobalPreferencesEntity result = underTest.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.AEROSPACE, IndustryRoutingDataSource.currentIndustry);
		assertEquals(IndustryType.AEROSPACE, globalPreferences.getIndustryType());
		assertEquals(5, globalPreferences.getRestEndPoints().size());


		Map<String, RestEndpointEntity> endpoints = new HashMap<>(); // Map<url,RestEndpointEntity>
		for (RestEndpointEntity endpoint : globalPreferences.getRestEndPoints()) {
			endpoints.put(endpoint.getUrl(), endpoint);
		}
		assertTrue(endpoints.containsKey(categoriesRestEndpointUrl));
		RestEndpointEntity categoriesRestEndpoint = endpoints.get(categoriesRestEndpointUrl);
		assertEquals(GlobalPreferencesDefaultSettingsService.CATEGORIES_ENDPOINT_ID, categoriesRestEndpoint.getRouteId());
		assertEquals(GlobalPreferencesDefaultSettingsService.CATEGORIES_ENDPOINT_PATH, categoriesRestEndpoint.getPath());

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

		assertEquals(MqType.ACTIVE_MQ, result.getMqType());
		assertEquals("proxy.queue.inventory.request", result.getOrderServiceDestinationQueue());
		assertEquals("proxy.queue.inventory.response", result.getOrderServiceReplyToQueue());
		assertEquals(globalPreferences.getOrderServiceRequestTopic(), result.getOrderServiceRequestTopic());
		assertEquals(globalPreferences.getOrderServiceResponseTopic(), result.getOrderServiceResponseTopic());
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_normal_with_kafka() throws Throwable {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(inventoryRequestQueueListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.KAFKA);
		globalPreferencesDto.setOrderServiceSendTo("request.topic");
		globalPreferencesDto.setOrderServiceListenOn("response.topic");

		GlobalPreferencesEntity result = underTest.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.AEROSPACE, IndustryRoutingDataSource.currentIndustry);
		assertEquals(IndustryType.AEROSPACE, globalPreferences.getIndustryType());
		assertEquals(5, globalPreferences.getRestEndPoints().size());


		Map<String, RestEndpointEntity> endpoints = new HashMap<>(); // Map<url,RestEndpointEntity>
		for (RestEndpointEntity endpoint : globalPreferences.getRestEndPoints()) {
			endpoints.put(endpoint.getUrl(), endpoint);
		}
		assertTrue(endpoints.containsKey(categoriesRestEndpointUrl));
		RestEndpointEntity categoriesRestEndpoint = endpoints.get(categoriesRestEndpointUrl);
		assertEquals(GlobalPreferencesDefaultSettingsService.CATEGORIES_ENDPOINT_ID, categoriesRestEndpoint.getRouteId());
		assertEquals(GlobalPreferencesDefaultSettingsService.CATEGORIES_ENDPOINT_PATH, categoriesRestEndpoint.getPath());

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

		assertEquals(MqType.KAFKA, result.getMqType());
		assertEquals(globalPreferences.getOrderServiceDestinationQueue(), result.getOrderServiceDestinationQueue());
		assertEquals(globalPreferences.getOrderServiceReplyToQueue(), result.getOrderServiceReplyToQueue());
		assertEquals("request.topic", result.getOrderServiceRequestTopic());
		assertEquals("response.topic", result.getOrderServiceResponseTopic());
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_normal_blankEndpointUrl1() throws Throwable {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryRequestTopicListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = ""; // test point
		String itemsRestEndpointUrl = ""; // test point
		String cartItemsRestEndpointUrl = ""; // test point
		String ordersRestEndpointUrl = ""; // test point
		String locationsRestEndpointUrl = ""; // test point
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceSendTo("proxy.queue.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("proxy.queue.inventory.response");

		GlobalPreferencesEntity result = underTest.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(0, globalPreferences.getRestEndPoints().size());
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_normal_blankEndpointUrl2() throws Throwable {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryRequestTopicListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = " "; // test point
		String itemsRestEndpointUrl = " "; // test point
		String cartItemsRestEndpointUrl = " "; // test point
		String ordersRestEndpointUrl = " "; // test point
		String locationsRestEndpointUrl = " "; // test point
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceSendTo("proxy.queue.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("proxy.queue.inventory.response");

		GlobalPreferencesEntity result = underTest.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(0, globalPreferences.getRestEndPoints().size());
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_normal_nullEndpointUrl() throws Throwable {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);

		doNothing().when(restEndpointService).removeAllEndpoints();
		doNothing().when(demoBugService).removeByGlobalPreferencesId(anyLong());
		doNothing().when(inventoryRequestTopicListener).refreshDestination(any());
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = null; // test point
		String itemsRestEndpointUrl = null; // test point
		String cartItemsRestEndpointUrl = null; // test point
		String ordersRestEndpointUrl = null; // test point
		String locationsRestEndpointUrl = null; // test point
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceSendTo("proxy.queue.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("proxy.queue.inventory.response");

		GlobalPreferencesEntity result = underTest.updateGlobalPreferences(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertEquals(0, globalPreferences.getRestEndPoints().size());
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_exception_nullIndustry() {
		// Given
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(null);
		globalPreferencesDto.setAdvertisingEnabled(false);

		// When
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(GlobalPreferencesMessages.INDUSTRY_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_exception_nullMqType() {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryRequestTopicListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(null);
		globalPreferencesDto.setOrderServiceSendTo("proxy.queue.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("proxy.queue.inventory.response");
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(GlobalPreferencesMessages.MQTYPE_MUST_NOT_BE_NULL, message);
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_exception_nullOrderServiceDestinationQueue() {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryRequestTopicListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceSendTo(null);
		globalPreferencesDto.setOrderServiceListenOn("proxy.queue.inventory.response");
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals("orderServiceDestinationQueue cannot be null or empty.", message);
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_exception_nullOrderServiceReplyToQueue() {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryRequestTopicListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.ACTIVE_MQ);
		globalPreferencesDto.setOrderServiceSendTo("proxy.queue.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn(null);
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals("orderServiceReplyToQueue cannot be null or empty.", message);
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_exception_nullOrderServiceRequestTopic() {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(inventoryRequestQueueListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.KAFKA);
		globalPreferencesDto.setOrderServiceSendTo(null);
		globalPreferencesDto.setOrderServiceListenOn("response.topic");
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(ORDER_SERVICE_REQUEST_TOPIC_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_exception_nullOrderServiceResponseTopic() {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(inventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(inventoryRequestQueueListener).refreshDestination(any());

		doNothing().when(restEndpointService).removeAllEndpoints();
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = "http://localhost:8080/v1/assets/categories/";
		String itemsRestEndpointUrl = "http://localhost:8080/v1/assets/items/";
		String cartItemsRestEndpointUrl = "http://localhost:8080/v1/cartItems/";
		String ordersRestEndpointUrl = "http://localhost:8080/v1/orders/";
		String locationsRestEndpointUrl = "http://localhost:8080/v1/locations/";
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setWebServiceMode(WebServiceMode.REST_API);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);
		globalPreferencesDto.setMqType(MqType.KAFKA);
		globalPreferencesDto.setOrderServiceSendTo("request.topic");
		globalPreferencesDto.setOrderServiceListenOn(null);
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(ORDER_SERVICE_RESPONSE_TOPIC_CANNOT_BE_NULL, message);
	}
}