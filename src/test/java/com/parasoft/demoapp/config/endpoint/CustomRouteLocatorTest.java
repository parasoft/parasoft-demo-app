package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.RestEndpointService;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test class for CustomRouteLocator
 *
 * @see com.parasoft.demoapp.config.endpoint.CustomRouteLocator
 */
public class CustomRouteLocatorTest {

	@Test
	public void testRefresh() throws Throwable {
		// Given
		String servletPath = "/";
		ZuulProperties properties = new ZuulProperties();
		RestEndpointService restEndpointService = mock(RestEndpointService.class);
		GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService =
				mock(GlobalPreferencesDefaultSettingsService.class);
		GlobalPreferencesService globalPreferencesService = mock(GlobalPreferencesService.class);

		List<RestEndpointEntity> endpoints = getDefaultRestEndpoints();
		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);

		when(restEndpointService.getAllEndpoints()).thenReturn(endpoints);
		when(globalPreferences.getGraphQLEndpoint()).thenReturn("");
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferences);

		// When
		CustomRouteLocator underTest = new CustomRouteLocator(servletPath, properties, restEndpointService,
				globalPreferencesDefaultSettingsService, globalPreferencesService);

		underTest.refresh();

		// Then
		int port = 8080;
		Map<String, String> expectedRouteRestEndpointsMap = Stream.of(new String[][]{
				{CATEGORIES_ENDPOINT_ID, HOST + port + CATEGORIES_ENDPOINT_REAL_PATH},
				{ITEMS_ENDPOINT_ID, HOST + port + ITEMS_ENDPOINT_REAL_PATH},
				{CART_ENDPOINT_ID, HOST + port + CART_ENDPOINT_REAL_PATH},
				{ORDERS_ENDPOINT_ID, HOST + port + ORDERS_ENDPOINT_REAL_PATH},
				{LOCATIONS_ENDPOINT_ID, HOST + port + LOCATIONS_ENDPOINT_REAL_PATH}
		}).collect(Collectors.toMap(p -> p[0], p -> p[1]));
		verify(restEndpointService)
				.refreshRouteRestEndpointsSnapshot(argThat(new MapMatcher(expectedRouteRestEndpointsMap)));
	}

	/**
	 * Test for locateRoutes()
	 *
	 * @see com.parasoft.demoapp.config.endpoint.CustomRouteLocator#locateRoutes()
	 */
	@Test
	public void testLocateRoutes_normal() throws Throwable {
		// Given
		String servletPath = "/";
		ZuulProperties properties = new ZuulProperties();
		RestEndpointService restEndpointService = mock(RestEndpointService.class);

		List<RestEndpointEntity> endpoints = getDefaultRestEndpoints();

		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);
		GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService =
				mock(GlobalPreferencesDefaultSettingsService.class);
		GlobalPreferencesService globalPreferencesService = mock(GlobalPreferencesService.class);

		String routeId = "routeId";
		String path = "/v1/assets/**";
		String url = "http://localhost:8080/v1/assets/";

		RestEndpointEntity testedEndpoint = new RestEndpointEntity(routeId, path, url, globalPreferences);
		endpoints.add(testedEndpoint);
		
		when(restEndpointService.getAllEndpoints()).thenReturn(endpoints);
		when(globalPreferences.getGraphQLEndpoint()).thenReturn("");
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferences);

		// When
		CustomRouteLocator underTest = new CustomRouteLocator(servletPath, properties, restEndpointService,
				globalPreferencesDefaultSettingsService, globalPreferencesService);

		Map<String, ZuulRoute> result = underTest.locateRoutes();

		// Then
		assertNotNull(result);
		assertEquals(7, result.size());
		assertTrue(result.containsKey(path));
	}
	
	/**
	 * Test for locateRoutes()
	 *
	 * @see com.parasoft.demoapp.config.endpoint.CustomRouteLocator#locateRoutes()
	 */
	@Test
	public void testLocateRoutes_normal_pathWithoutSlash() throws Throwable {
		// Given
		String servletPath = "/";
		ZuulProperties properties = new ZuulProperties();
		RestEndpointService restEndpointService = mock(RestEndpointService.class);

		List<RestEndpointEntity> endpoints = getDefaultRestEndpoints();
		String routeId = "routeId";
		String path = "v1/assets/**"; // test point: not prepend with slash
		String url = "http://localhost:8080/v1/assets/";
		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);
		GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService =
				mock(GlobalPreferencesDefaultSettingsService.class);
		GlobalPreferencesService globalPreferencesService = mock(GlobalPreferencesService.class);

		RestEndpointEntity endpoint = new RestEndpointEntity(routeId, path, url, globalPreferences);
		endpoints.add(endpoint);
		
		when(restEndpointService.getAllEndpoints()).thenReturn(endpoints);
		when(globalPreferences.getGraphQLEndpoint()).thenReturn("");
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferences);
		
		// When
		CustomRouteLocator underTest = new CustomRouteLocator(servletPath, properties, restEndpointService,
				globalPreferencesDefaultSettingsService, globalPreferencesService);
		Map<String, ZuulRoute> result = underTest.locateRoutes();

		// Then
		assertNotNull(result);
		assertEquals(7, result.size());
		assertTrue(result.containsKey("/" + path));
	}
	
	/**
	 * Test for locateRoutes()
	 *
	 * @see com.parasoft.demoapp.config.endpoint.CustomRouteLocator#locateRoutes()
	 */
	@Test
	public void testLocateRoutes_normal_emptyPath() throws Throwable {
		// Given
		String servletPath = "/";
		ZuulProperties properties = new ZuulProperties();
		RestEndpointService restEndpointService = mock(RestEndpointService.class);

		List<RestEndpointEntity> endpoints = getDefaultRestEndpoints();
		String routeId = "routeId";
		String path = ""; // test point: empty path, path.length() == 0
		String url = "http://localhost:8080/v1/assets/";
		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);
		GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService =
				mock(GlobalPreferencesDefaultSettingsService.class);
		GlobalPreferencesService globalPreferencesService = mock(GlobalPreferencesService.class);

		RestEndpointEntity endpoint = new RestEndpointEntity(routeId, path, url, globalPreferences);
		endpoints.add(endpoint);
		
		when(restEndpointService.getAllEndpoints()).thenReturn(endpoints);
		when(globalPreferences.getGraphQLEndpoint()).thenReturn("");
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferences);
		
		// When
		CustomRouteLocator underTest = new CustomRouteLocator(servletPath, properties, restEndpointService,
				globalPreferencesDefaultSettingsService, globalPreferencesService);
		Map<String, ZuulRoute> result = underTest.locateRoutes();

		// Then
		assertNotNull(result);
		assertEquals(6, result.size());
	}
	
	/**
	 * Test for locateRoutes()
	 *
	 * @see com.parasoft.demoapp.config.endpoint.CustomRouteLocator#locateRoutes()
	 */
	@Test
	public void testLocateRoutes_normal_nullPath() throws Throwable {
		// Given
		String servletPath = "/";
		ZuulProperties properties = new ZuulProperties();
		RestEndpointService restEndpointService = mock(RestEndpointService.class);

		List<RestEndpointEntity> endpoints = getDefaultRestEndpoints();
		String routeId = "routeId";
		String path = null; // test point: null path
		String url = "http://localhost:8080/v1/assets/";
		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);
		GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService =
				mock(GlobalPreferencesDefaultSettingsService.class);
		GlobalPreferencesService globalPreferencesService = mock(GlobalPreferencesService.class);

		RestEndpointEntity endpoint = new RestEndpointEntity(routeId, path, url, globalPreferences);
		endpoints.add(endpoint);
		
		when(restEndpointService.getAllEndpoints()).thenReturn(endpoints);
		when(globalPreferences.getGraphQLEndpoint()).thenReturn("");
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferences);
		
		// When
		CustomRouteLocator underTest = new CustomRouteLocator(servletPath, properties, restEndpointService,
				globalPreferencesDefaultSettingsService, globalPreferencesService);
		Map<String, ZuulRoute> result = underTest.locateRoutes();

		// Then
		assertNotNull(result);
		assertEquals(6, result.size());
	}
	
	/**
	 * Test for locateRoutes()
	 *
	 * @see com.parasoft.demoapp.config.endpoint.CustomRouteLocator#locateRoutes()
	 */
	@Test
	public void testLocateRoutes_normal_emptyUrl() throws Throwable {
		// Given
		String servletPath = "/";
		ZuulProperties properties = new ZuulProperties();
		RestEndpointService restEndpointService = mock(RestEndpointService.class);

		List<RestEndpointEntity> endpoints = getDefaultRestEndpoints();
		String routeId = "routeId";
		String path = "/v1/assets/**";
		String url = ""; // test point: empty url, url.length() == 0
		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);
		GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService =
				mock(GlobalPreferencesDefaultSettingsService.class);
		GlobalPreferencesService globalPreferencesService = mock(GlobalPreferencesService.class);

		RestEndpointEntity endpoint = new RestEndpointEntity(routeId, path, url, globalPreferences);
		endpoints.add(endpoint);
		
		when(restEndpointService.getAllEndpoints()).thenReturn(endpoints);
		when(globalPreferences.getGraphQLEndpoint()).thenReturn("");
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferences);
		
		// When
		CustomRouteLocator underTest = new CustomRouteLocator(servletPath, properties, restEndpointService,
				globalPreferencesDefaultSettingsService, globalPreferencesService);
		Map<String, ZuulRoute> result = underTest.locateRoutes();

		// Then
		assertNotNull(result);
		assertEquals(6, result.size());
	}
	
	/**
	 * Test for locateRoutes()
	 *
	 * @see com.parasoft.demoapp.config.endpoint.CustomRouteLocator#locateRoutes()
	 */
	@Test
	public void testLocateRoutes_normal_nullUrl() throws Throwable {
		// Given
		String servletPath = "/";
		ZuulProperties properties = new ZuulProperties();
		RestEndpointService restEndpointService = mock(RestEndpointService.class);

		List<RestEndpointEntity> endpoints = getDefaultRestEndpoints();
		String routeId = "routeId";
		String path = "/v1/assets/**";
		String url = null; // test point: null url
		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);
		GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService =
				mock(GlobalPreferencesDefaultSettingsService.class);
		GlobalPreferencesService globalPreferencesService = mock(GlobalPreferencesService.class);

		RestEndpointEntity endpoint = new RestEndpointEntity(routeId, path, url, globalPreferences);
		endpoints.add(endpoint);
		
		when(restEndpointService.getAllEndpoints()).thenReturn(endpoints);
		when(globalPreferences.getGraphQLEndpoint()).thenReturn("");
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferences);
		
		// When
		CustomRouteLocator underTest = new CustomRouteLocator(servletPath, properties, restEndpointService,
				globalPreferencesDefaultSettingsService, globalPreferencesService);
		Map<String, ZuulRoute> result = underTest.locateRoutes();

		// Then
		assertNotNull(result);
		assertEquals(6, result.size());
	}

	private List<RestEndpointEntity> getDefaultRestEndpoints(){

		List<RestEndpointEntity> endpoints = new ArrayList<>();
		int serverPort = 8080;
		endpoints.add(new RestEndpointEntity(CATEGORIES_ENDPOINT_ID, CATEGORIES_ENDPOINT_PATH,
				HOST + serverPort + CATEGORIES_ENDPOINT_REAL_PATH));
		endpoints.add(new RestEndpointEntity(ITEMS_ENDPOINT_ID, ITEMS_ENDPOINT_PATH,
				HOST + serverPort + ITEMS_ENDPOINT_REAL_PATH));
		endpoints.add(new RestEndpointEntity(CART_ENDPOINT_ID, CART_ENDPOINT_PATH,
				HOST + serverPort + CART_ENDPOINT_REAL_PATH));
		endpoints.add(new RestEndpointEntity(ORDERS_ENDPOINT_ID, ORDERS_ENDPOINT_PATH,
				HOST + serverPort + ORDERS_ENDPOINT_REAL_PATH));
		endpoints.add(new RestEndpointEntity(LOCATIONS_ENDPOINT_ID, LOCATIONS_ENDPOINT_PATH,
				HOST + 8080 + LOCATIONS_ENDPOINT_REAL_PATH));

		return endpoints;
	}

	@RequiredArgsConstructor
	private static class MapMatcher implements ArgumentMatcher<Map<String, String>> {

		private final Map<String, String>  left;

		@Override
		public boolean matches(Map<String, String>  right) {
			if (left == null) {
				return right == null;
			}

			if (left.size() != right.size()) {
				return false;
			}

			return left.entrySet().stream()
					.allMatch(e -> e.getValue().equals(right.get(e.getKey())));
		}
	}

}