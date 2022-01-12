package com.parasoft.demoapp.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;

/**
 * Test class for RouteIdSortOfRestEndpoint
 *
 * @see com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint
 */
public class RouteIdSortOfRestEndpointTest {

	/**
	 * Test for compare(RestEndpointEntity, RestEndpointEntity) 'o1 equals to o2'
	 *
	 * @see com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint#compare(RestEndpointEntity, RestEndpointEntity)
	 */
	@Test
	public void testCompare_equal() throws Throwable {
		// Given
		RouteIdSortOfRestEndpoint underTest = new RouteIdSortOfRestEndpoint();
		RestEndpointEntity o1 = mock(RestEndpointEntity.class);
		when(o1.getRouteId()).thenReturn("orders");
		RestEndpointEntity o2 = mock(RestEndpointEntity.class);
		when(o2.getRouteId()).thenReturn("orders");

		// When
		int result = underTest.compare(o1, o2);

		// Then
		assertEquals(0, result);
		
	}

	/**
	 * Test for compare(RestEndpointEntity, RestEndpointEntity) 'o1 is less than o2'
	 *
	 * @see com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint#compare(RestEndpointEntity, RestEndpointEntity)
	 */
	@Test
	public void testCompare_less_than() throws Throwable {
		// Given
		RouteIdSortOfRestEndpoint underTest = new RouteIdSortOfRestEndpoint();
		RestEndpointEntity o1 = mock(RestEndpointEntity.class);
		when(o1.getRouteId()).thenReturn("orders1");
		RestEndpointEntity o2 = mock(RestEndpointEntity.class);
		when(o2.getRouteId()).thenReturn("orders2");

		// When
		int result = underTest.compare(o1, o2);

		// Then
		assertEquals(-1, result);
		
	}
	
	/**
	 * Test for compare(RestEndpointEntity, RestEndpointEntity) 'o1 is greater than o2'
	 *
	 * @see com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint#compare(RestEndpointEntity, RestEndpointEntity)
	 */
	@Test
	public void testCompare_greater_than() throws Throwable {
		// Given
		RouteIdSortOfRestEndpoint underTest = new RouteIdSortOfRestEndpoint();
		RestEndpointEntity o1 = mock(RestEndpointEntity.class);
		when(o1.getRouteId()).thenReturn("orders2");
		RestEndpointEntity o2 = mock(RestEndpointEntity.class);
		when(o2.getRouteId()).thenReturn("orders1");

		// When
		int result = underTest.compare(o1, o2);

		// Then
		assertEquals(1, result);
		
	}
}