/**
 * 
 */
package com.parasoft.demoapp.model.global.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

/**
 * Test class for RestEndpointEntity
 *
 * @see com.parasoft.demoapp.model.global.preferences.RestEndpointEntity
 */
public class RestEndpointEntityTest {

	/**
	 * Test for toRealZuulRoute()
	 *
	 * @see com.parasoft.demoapp.model.global.preferences.RestEndpointEntity#toRealZuulRoute()
	 */
	@Test
	public void testToRealZuulRoute() throws Throwable {
		// Given
		String routeId = "routeId";
		String path = "/v1/assets/**";
		String url = "http://localhost:8080/v1/assets/";
		GlobalPreferencesEntity globalPreferences = mock(GlobalPreferencesEntity.class);
		RestEndpointEntity underTest = new RestEndpointEntity(routeId, path, url, globalPreferences);

		// When
		ZuulRoute result = underTest.toRealZuulRoute();

		// Then
		assertNotNull(result);
		assertNull(result.getServiceId());
		assertNotNull(result.getSensitiveHeaders());
		assertEquals(0, result.getSensitiveHeaders().size());
		assertEquals(routeId, result.getId());
		assertEquals(path, result.getPath());
		assertEquals(url, result.getUrl());
		assertTrue(result.isStripPrefix());
		assertFalse(result.getRetryable());
	}
}