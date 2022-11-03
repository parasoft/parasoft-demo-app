package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.model.global.preferences.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.repository.global.GlobalPreferencesRepository;
import com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs;
import com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint;

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
	DemoBugService demoBugService;

	@Mock
	private ParasoftJDBCProxyService parasoftJDBCProxyDriverService;

	@Mock
	WebConfig webConfig;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_normal() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());
		IndustryType industryType = IndustryType.DEFENSE;
		DemoBugEntity demoBug = new DemoBugEntity(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS);
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		demoBugSet.add(demoBug);
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		Boolean proxyMqEnabled = false;
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "queue.inventory.request";
		String orderServiceReplyToQueue = "queue.inventory.response";
		String inventoryServiceDestinationQueue = "queue.inventory.request";
		String inventoryServiceReplyToQueue = "queue.inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);

		// Then
		assertNotNull(result);
		assertEquals(DataAccessMode.JDBC.getValue(), result.getDataAccessMode().getValue());
		assertEquals("", result.getSoapEndPoint());
		assertNotNull(result.getDemoBugs());
		assertNotNull(result.getRestEndPoints());
		assertEquals(IndustryType.DEFENSE.getValue(), result.getIndustryType().getValue());
		assertEquals(false, result.getAdvertisingEnabled());
		assertEquals(false, result.getMqProxyEnabled());
		assertEquals(MqType.ACTIVE_MQ, result.getMqType());
		assertEquals(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST, result.getOrderServiceDestinationQueue());
		assertEquals(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE, result.getOrderServiceReplyToQueue());
		assertEquals(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST, result.getInventoryServiceDestinationQueue());
		assertEquals(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE, result.getInventoryServiceReplyToQueue());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_nullDemoBugs() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());
		IndustryType industryType = IndustryType.DEFENSE;
		Set<DemoBugEntity> demoBugSet = null;
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		Boolean proxyMqEnabled = false;
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "queue.inventory.request";
		String orderServiceReplyToQueue = "queue.inventory.response";
		String inventoryServiceDestinationQueue = "queue.inventory.request";
		String inventoryServiceReplyToQueue = "queue.inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);

		// Then
		assertNotNull(result);
		assertNull(result.getDemoBugs());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_nullRestEndPoints() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = null;
		IndustryType industryType = IndustryType.DEFENSE;
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		Boolean proxyMqEnabled = false;
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "queue.inventory.request";
		String orderServiceReplyToQueue = "queue.inventory.response";
		String inventoryServiceDestinationQueue = "queue.inventory.request";
		String inventoryServiceReplyToQueue = "queue.inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		GlobalPreferencesEntity result = underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);

		// Then
		assertNotNull(result);
		assertEquals(restEndPoints, result.getRestEndPoints());
	}

	/**
	 * test for addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 *
	 * @see GlobalPreferencesService#addNewGlobalPreferences(DataAccessMode, String, Set, IndustryType, Set, Boolean, Boolean, String, String, String,
	 * Boolean, MqType, String, String, String, String)
	 */
	@Test
	public void testAddNewGlobalPreferences_nullAdvertisingEnabled() throws Throwable {
		// Given
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		Set<RestEndpointEntity> restEndPoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());
		IndustryType industryType = IndustryType.DEFENSE;
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		Boolean advertisingEnabled = null;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		Boolean proxyMqEnabled = false;
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "queue.inventory.request";
		String orderServiceReplyToQueue = "queue.inventory.response";
		String inventoryServiceDestinationQueue = "queue.inventory.request";
		String inventoryServiceReplyToQueue = "queue.inventory.response";

		GlobalPreferencesEntity saveResult = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);
		doReturn(saveResult).when(globalPreferencesRepository).save((GlobalPreferencesEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewGlobalPreferences(dataAccessMode, soapEndPoint, restEndPoints, industryType, demoBugSet,
					advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
					parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
					orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);
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
		Set<DemoBugEntity> demoBugSet = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		Boolean advertisingEnabled = false;
		Boolean useParasoftJDBCProxy = false;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";
		Boolean proxyMqEnabled = false;
		MqType mqType = MqType.ACTIVE_MQ;
		String orderServiceDestinationQueue = "queue.inventory.request";
		String orderServiceReplyToQueue = "queue.inventory.response";
		String inventoryServiceDestinationQueue = "queue.inventory.request";
		String inventoryServiceReplyToQueue = "queue.inventory.response";

		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity(dataAccessMode, soapEndPoint, restEndPoints,
				industryType, demoBugSet, advertisingEnabled, useParasoftJDBCProxy, parasoftVirtualizeServerUrl,
				parasoftVirtualizeServerPath, parasoftVirtualizeGroupId, proxyMqEnabled, mqType, orderServiceDestinationQueue,
				orderServiceReplyToQueue, inventoryServiceDestinationQueue, inventoryServiceReplyToQueue);

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
	public void testUpdateGlobalPreferences_normal() throws Throwable {
		// Given
		List<GlobalPreferencesEntity> findAllResult = new ArrayList<>();
		GlobalPreferencesEntity globalPreferences = new GlobalPreferencesEntity();
		findAllResult.add(globalPreferences);
		when(globalPreferencesRepository.findAll()).thenReturn(findAllResult);

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
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);

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
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);

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
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);

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
		when(globalPreferencesRepository.save(any(GlobalPreferencesEntity.class))).thenReturn(globalPreferences);

		// When
		String categoriesRestEndpointUrl = null; // test point
		String itemsRestEndpointUrl = null; // test point
		String cartItemsRestEndpointUrl = null; // test point
		String ordersRestEndpointUrl = null; // test point
		String locationsRestEndpointUrl = null; // test point
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(IndustryType.AEROSPACE);
		globalPreferencesDto.setCategoriesRestEndpoint(categoriesRestEndpointUrl);
		globalPreferencesDto.setItemsRestEndpoint(itemsRestEndpointUrl);
		globalPreferencesDto.setCartItemsRestEndpoint(cartItemsRestEndpointUrl);
		globalPreferencesDto.setOrdersRestEndpoint(ordersRestEndpointUrl);
		globalPreferencesDto.setLocationsRestEndpoint(locationsRestEndpointUrl);
		globalPreferencesDto.setAdvertisingEnabled(false);

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
	public void testUpdateGlobalPreferences_exception_nullIndustry() throws Throwable {
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
}