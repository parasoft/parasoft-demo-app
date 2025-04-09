package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.OpenApiConfig;
import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQInventoryRequestQueueListener;
import com.parasoft.demoapp.config.activemq.ActiveMQInventoryResponseQueueListener;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.config.kafka.KafkaInventoryRequestTopicListener;
import com.parasoft.demoapp.config.kafka.KafkaInventoryResponseTopicListener;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQConfig;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQInventoryRequestQueueListener;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQInventoryResponseQueueListener;
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
import org.mockito.*;

import java.text.MessageFormat;
import java.util.*;

import static com.parasoft.demoapp.config.activemq.ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST;
import static com.parasoft.demoapp.config.activemq.ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE;
import static com.parasoft.demoapp.messages.GlobalPreferencesMessages.*;
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

	@Spy
	@InjectMocks
	GlobalPreferencesService underTest;

	@Mock
	OpenApiConfig.SchemaPropertyCustomizer schemaPropertyCustomizer;

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
	ActiveMQInventoryResponseQueueListener activeMQInventoryResponseQueueListener;

	@Mock
	ActiveMQInventoryRequestQueueListener activeMQInventoryRequestQueueListener;

	@Mock
	KafkaInventoryResponseTopicListener kafkaInventoryResponseTopicListener;

	@Mock
	KafkaInventoryRequestTopicListener kafkaInventoryRequestTopicListener;

	@Mock
	private RabbitMQInventoryRequestQueueListener rabbitMQInventoryRequestQueueListener;

	@Mock
	private RabbitMQInventoryResponseQueueListener rabbitMQInventoryResponseQueueListener;

	@Mock
	WebConfig webConfig;

	@Mock
	RabbitMQConfig rabbitMQConfig;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() {
		ActiveMQConfig.setOrderServiceListenToQueue(DEFAULT_QUEUE_INVENTORY_RESPONSE);
		ActiveMQConfig.setOrderServiceSendToQueue(new ActiveMQQueue(DEFAULT_QUEUE_INVENTORY_REQUEST));
		KafkaConfig.setOrderServiceSendToTopic(KafkaConfig.DEFAULT_ORDER_SERVICE_REQUEST_TOPIC);
		KafkaConfig.setOrderServiceListenToTopic(KafkaConfig.DEFAULT_ORDER_SERVICE_RESPONSE_TOPIC);
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String, String, String)
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
		String orderServiceActiveMqRequestQueue = "inventory.request";
		String orderServiceActiveMqResponseQueue = "inventory.response";
		String orderServiceKafkaRequestTopic = "inventory.request";
		String orderServiceKafkaResponseTopic = "inventory.response";
		String orderServiceRabbitMqRequestQueue = "inventory.request";
		String orderServiceRabbitMqResponseQueue = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);

		// Then
		assertNotNull(result);
		assertEquals(DataAccessMode.JDBC.getValue(), result.getDataAccessMode().getValue());
		assertEquals("", result.getSoapEndPoint());
		assertNotNull(result.getDemoBugs());
		assertNotNull(result.getRestEndPoints());
		assertEquals(IndustryType.DEFENSE.getValue(), result.getIndustryType().getValue());
		assertEquals(false, result.getAdvertisingEnabled());
		assertEquals(mqType, result.getMqType());
		assertEquals(orderServiceActiveMqRequestQueue, result.getOrderServiceActiveMqRequestQueue());
		assertEquals(orderServiceActiveMqResponseQueue, result.getOrderServiceActiveMqResponseQueue());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String, String, String)
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
		String orderServiceActiveMqRequestQueue = "inventory.request";
		String orderServiceActiveMqResponseQueue = "inventory.response";
		String orderServiceKafkaRequestTopic = "inventory.request";
		String orderServiceKafkaResponseTopic = "inventory.response";
		String orderServiceRabbitMqRequestQueue = "inventory.request";
		String orderServiceRabbitMqResponseQueue = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);

		// Then
		assertNotNull(result);
		assertNull(result.getDemoBugs());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String, String, String)
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
		String orderServiceActiveMqRequestQueue = "inventory.request";
		String orderServiceActiveMqResponseQueue = "inventory.response";
		String orderServiceKafkaRequestTopic = "inventory.request";
		String orderServiceKafkaResponseTopic = "inventory.response";
		String orderServiceRabbitMqRequestQueue = "inventory.request";
		String orderServiceRabbitMqResponseQueue = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);

		// Then
		assertNotNull(result);
		assertEquals(restEndPoints, result.getRestEndPoints());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, WebServiceMode, String, Set, Boolean, Boolean, String, String, String,
	 * MqType, String, String, String, String, String, String)
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
		String orderServiceActiveMqRequestQueue = "inventory.request";
		String orderServiceActiveMqResponseQueue = "inventory.response";
		String orderServiceKafkaRequestTopic = "inventory.request";
		String orderServiceKafkaResponseTopic = "inventory.response";
		String orderServiceRabbitMqRequestQueue = "inventory.request";
		String orderServiceRabbitMqResponseQueue = "inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints, industryType, webServiceMode, graphQLEndpoint,
					demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
					parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
					orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);
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
		String orderServiceActiveMqRequestQueue = "inventory.request";
		String orderServiceActiveMqResponseQueue = "inventory.response";
		String orderServiceKafkaRequestTopic = "inventory.request";
		String orderServiceKafkaResponseTopic = "inventory.response";
		String orderServiceRabbitMqRequestQueue = "inventory.request";
		String orderServiceRabbitMqResponseQueue = "inventory.response";

		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, webServiceMode, graphQLEndpoint, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, mqType, orderServiceActiveMqRequestQueue,
				orderServiceActiveMqResponseQueue, orderServiceKafkaRequestTopic, orderServiceKafkaResponseTopic, orderServiceRabbitMqRequestQueue, orderServiceRabbitMqResponseQueue);

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
		doNothing().when(activeMQInventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(activeMQInventoryRequestQueueListener).refreshDestination(any());

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
		globalPreferencesDto.setOrderServiceSendTo("test.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("test.inventory.response");

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
		assertEquals("test.inventory.request", result.getOrderServiceActiveMqRequestQueue());
		assertEquals("test.inventory.response", result.getOrderServiceActiveMqResponseQueue());
		assertEquals(globalPreferences.getOrderServiceKafkaRequestTopic(), result.getOrderServiceKafkaRequestTopic());
		assertEquals(globalPreferences.getOrderServiceKafkaResponseTopic(), result.getOrderServiceKafkaResponseTopic());
        assertEquals(globalPreferences.getOrderServiceRabbitMqRequestQueue(), result.getOrderServiceRabbitMqRequestQueue());
        assertEquals(globalPreferences.getOrderServiceRabbitMqResponseQueue(), result.getOrderServiceRabbitMqResponseQueue());
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
		doNothing().when(kafkaInventoryRequestTopicListener).refreshDestination(any());
		doNothing().when(kafkaInventoryResponseTopicListener).refreshDestination(any());
		doNothing().when(underTest).validateKafkaBrokerUrl();

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
		assertEquals(globalPreferences.getOrderServiceActiveMqRequestQueue(), result.getOrderServiceActiveMqRequestQueue());
		assertEquals(globalPreferences.getOrderServiceActiveMqResponseQueue(), result.getOrderServiceActiveMqResponseQueue());
        assertEquals(globalPreferences.getOrderServiceRabbitMqRequestQueue(), result.getOrderServiceRabbitMqRequestQueue());
        assertEquals(globalPreferences.getOrderServiceRabbitMqResponseQueue(), result.getOrderServiceRabbitMqResponseQueue());
		assertEquals("request.topic", result.getOrderServiceKafkaRequestTopic());
		assertEquals("response.topic", result.getOrderServiceKafkaResponseTopic());
	}

    /**
     * Test for updateGlobalPreferences(GlobalPreferencesDTO)
     *
     * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
     */
    @Test
    public void testUpdateGlobalPreferences_normal_with_rabbitmq() throws Throwable {
        // Given
        List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
        GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
        findAllResult.add(globalPreferences);
        when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
        doNothing().when(rabbitMQInventoryRequestQueueListener).refreshDestination(any());
        doNothing().when(rabbitMQInventoryResponseQueueListener).refreshDestination(any());
        doNothing().when(underTest).validateRabbitMQServerUrl();

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
        globalPreferencesDto.setMqType(MqType.RABBIT_MQ);
        globalPreferencesDto.setOrderServiceSendTo("request.queue");
        globalPreferencesDto.setOrderServiceListenOn("response.queue");

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

        assertEquals(MqType.RABBIT_MQ, result.getMqType());
        assertEquals(globalPreferences.getOrderServiceActiveMqRequestQueue(), result.getOrderServiceActiveMqRequestQueue());
        assertEquals(globalPreferences.getOrderServiceActiveMqResponseQueue(), result.getOrderServiceActiveMqResponseQueue());
        assertEquals(globalPreferences.getOrderServiceKafkaRequestTopic(), result.getOrderServiceKafkaRequestTopic());
        assertEquals(globalPreferences.getOrderServiceKafkaResponseTopic(), result.getOrderServiceKafkaResponseTopic());
        assertEquals("request.queue", result.getOrderServiceRabbitMqRequestQueue());
        assertEquals("response.queue", result.getOrderServiceRabbitMqResponseQueue());
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
		doNothing().when(activeMQInventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(activeMQInventoryRequestQueueListener).refreshDestination(any());

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
		globalPreferencesDto.setOrderServiceSendTo("test.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("test.inventory.response");

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
		doNothing().when(activeMQInventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(activeMQInventoryRequestQueueListener).refreshDestination(any());

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
		globalPreferencesDto.setOrderServiceSendTo("test.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("test.inventory.response");

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
		doNothing().when(activeMQInventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(activeMQInventoryRequestQueueListener).refreshDestination(any());
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
		globalPreferencesDto.setOrderServiceSendTo("test.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("test.inventory.response");

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
		doNothing().when(activeMQInventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(activeMQInventoryRequestQueueListener).refreshDestination(any());

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
		globalPreferencesDto.setOrderServiceSendTo("test.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn("test.inventory.response");
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
	public void testUpdateGlobalPreferences_exception_nullOrderServiceSendTo() {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(activeMQInventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(activeMQInventoryRequestQueueListener).refreshDestination(any());

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
		globalPreferencesDto.setOrderServiceListenOn("test.inventory.response");
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(ORDER_SERVICE_SEND_TO_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for updateGlobalPreferences(GlobalPreferencesDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesService#updateGlobalPreferences(GlobalPreferencesDTO)
	 */
	@Test
	public void testUpdateGlobalPreferences_exception_nullOrderServiceListenOn() {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(activeMQInventoryResponseQueueListener).refreshDestination(any());
		doNothing().when(activeMQInventoryRequestQueueListener).refreshDestination(any());

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
		globalPreferencesDto.setOrderServiceSendTo("test.inventory.request");
		globalPreferencesDto.setOrderServiceListenOn(null);
		String message = "";
		try {
			underTest.updateGlobalPreferences(globalPreferencesDto);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(ORDER_SERVICE_LISTEN_ON_CANNOT_BE_NULL, message);
	}
}