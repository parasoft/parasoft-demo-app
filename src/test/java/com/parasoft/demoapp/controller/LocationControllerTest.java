/**
 * 
 */
package com.parasoft.demoapp.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.exception.LocationNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.UnsupportedOperationInCurrentIndustryException;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.LocationService;

/**
 * Test for LocationController
 *
 * @see LocationController
 */
public class LocationControllerTest {

	@InjectMocks
	LocationController underTest;

	@Mock
	LocationService locationService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for getLocation(RegionType)
	 *
	 * @see LocationController#getLocation(RegionType)
	 */
	@Test
	public void testGetLocation_normal() throws Throwable {
		// Given
		RegionType region = RegionType.UNITED_STATES;
		String locationInfo = "19.9450° E, 50.0647° N";
		LocationEntity locationEntity = new LocationEntity();
		locationEntity.setLocationInfo(locationInfo);
		locationEntity.setRegion(region);

		when(locationService.getLocationByRegion(nullable(RegionType.class)))
				.thenReturn(locationEntity);

		// When
		ResponseResult<LocationEntity> result = underTest.getLocation(region);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(1, result.getStatus());
		assertEquals("success", result.getMessage());
		assertEquals(region, result.getData().getRegion());
		assertEquals(locationInfo, result.getData().getLocationInfo());
	}

	/**
	 * Test for getLocation(RegionType)
	 *
	 * @see LocationController#getLocation(RegionType)
	 */
	@Test(expected = ParameterException.class)
	public void testGetLocation_parameterException() throws Throwable {
		// Given
		when(locationService.getLocationByRegion(nullable(RegionType.class)))
				.thenThrow(ParameterException.class);

		// When
		RegionType region = RegionType.UNITED_STATES;

		underTest.getLocation(region);
	}

	/**
	 * Test for getLocation(RegionType)
	 *
	 * @see LocationController#getLocation(RegionType)
	 */
	@Test(expected = LocationNotFoundException.class)
	public void testGetLocation_locationNotFoundException() throws Throwable {
		// Given
		when(locationService.getLocationByRegion(nullable(RegionType.class)))
				.thenThrow(LocationNotFoundException.class);

		// When
		RegionType region = RegionType.UNITED_STATES;

		underTest.getLocation(region);
	}

	/**
	 * Test for getAllRegionTypesOfCurrentIndustry()
	 *
	 * @see LocationController#getAllRegionTypesOfCurrentIndustry()
	 */
	@Test
	public void testGetAllRegionTypesOfCurrentIndustry_normal() throws Throwable {
		// Given
		List<RegionType> regions = new ArrayList<>();
		RegionType region = RegionType.UNITED_STATES;
		regions.add(region);
		doReturn(regions).when(locationService).getRegionsOfCurrentIndustry();

		// When
		ResponseResult<List<RegionType>> result = underTest.getAllRegionTypesOfCurrentIndustry();

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(1, result.getStatus());
		assertEquals("success", result.getMessage());
		assertEquals(1, result.getData().size());
		assertEquals(region, result.getData().get(0));
	}

	/**
	 * Test for getAllRegionTypesOfCurrentIndustry()
	 *
	 * @see LocationController#getAllRegionTypesOfCurrentIndustry()
	 */
	@Test(expected = UnsupportedOperationInCurrentIndustryException.class)
	public void testGetAllRegionTypesOfCurrentIndustry_unsupportedOperationInCurrentIndustryException()
			throws Throwable {
		// Given
		when(locationService.getRegionsOfCurrentIndustry())
				.thenThrow(UnsupportedOperationInCurrentIndustryException.class);

		// When
		underTest.getAllRegionTypesOfCurrentIndustry();
	}

}