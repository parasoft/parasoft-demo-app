/**
 * 
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.LocationRepository;

/**
 * Test for LocationService
 *
 * @see com.parasoft.demoapp.service.LocationService
 */
public class LocationServiceTest {

	@InjectMocks
	LocationService underTest;

	@Mock
	LocationRepository locationRepository;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for getLocationByRegion(RegionType)
	 *
	 * @see com.parasoft.demoapp.service.LocationService#getLocationByRegion(RegionType)
	 */
	@Test
	public void testGetLocationByRegion_normal() throws Throwable {
		// Given
		RegionType region = RegionType.UNITED_STATES;
		String locationInfo = "19.9450° E, 50.0647° N";
		String locationImage = "/path/to/image";
		LocationEntity locationEntity = new LocationEntity();
		locationEntity.setLocationInfo(locationInfo);
		locationEntity.setRegion(region);
		locationEntity.setLocationImage(locationImage);

		when(locationRepository.findByRegion(nullable(RegionType.class))).thenReturn(locationEntity);

		// When
		LocationEntity result = underTest.getLocationByRegion(region);

		// Then
		assertNotNull(result);
		assertEquals(region, result.getRegion());
		assertEquals(locationInfo, result.getLocationInfo());
		assertEquals(locationImage, result.getLocationImage());
	}

	/**
	 * Test for getLocationByRegion(RegionType)
	 *
	 * @see com.parasoft.demoapp.service.LocationService#getLocationByRegion(RegionType)
	 */
	@Test
	public void testGetLocationByRegion_nullRegion() throws Throwable {
		// Given
		RegionType region = null;

		// When
		String message = "";
		try {
			underTest.getLocationByRegion(region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(OrderMessages.REGION_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for getLocationByRegion(RegionType)
	 *
	 * @see com.parasoft.demoapp.service.LocationService#getLocationByRegion(RegionType)
	 */
	@Test
	public void testGetLocationByRegion_locationNotFound() throws Throwable {
		// Given
		RegionType region = RegionType.UNITED_STATES;
		LocationEntity locationEntity = null;

		when(locationRepository.findByRegion(nullable(RegionType.class))).thenReturn(locationEntity);

		// When
		String message = "";
		try {
			underTest.getLocationByRegion(region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(OrderMessages.LOCATION_NOT_FOUND, message);
	}

	/**
	 * Test for getAllLocations()
	 *
	 * @see com.parasoft.demoapp.service.LocationService#getAllLocations()
	 */
	@Test
	public void testGetAllLocations_normal() throws Throwable {
		// Given
		List<LocationEntity> findAllResult = new ArrayList<>();

		RegionType region = RegionType.UNITED_STATES;
		String locationInfo = "19.9450° E, 50.0647° N";
		String locationImage = "/path/to/image";
		LocationEntity locationEntity = new LocationEntity();
		locationEntity.setLocationInfo(locationInfo);
		locationEntity.setRegion(region);
		locationEntity.setLocationImage(locationImage);

		findAllResult.add(locationEntity);
		doReturn(findAllResult).when(locationRepository).findAll();

		// When
		List<LocationEntity> result = underTest.getAllLocations();

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(region, result.get(0).getRegion());
		assertEquals(locationInfo, result.get(0).getLocationInfo());
		assertEquals(locationImage, result.get(0).getLocationImage());
	}

	/**
	 * Test for getRegionsOfCurrentIndustry()
	 *
	 * @see com.parasoft.demoapp.service.LocationService#getRegionsOfCurrentIndustry()
	 */
	@Test
	public void testGetRegionsOfCurrentIndustry_normal1() throws Throwable {
		// Given
		IndustryType currentIndustry = IndustryType.DEFENSE;
		when(globalPreferencesService.getCurrentIndustry()).thenReturn(currentIndustry);

		// When
		List<RegionType> result = underTest.getRegionsOfCurrentIndustry();

		// Then
		assertNotNull(result);
		assertEquals(8, result.size());
	}

	/**
	 * Test for getRegionsOfCurrentIndustry()
	 *
	 * @see com.parasoft.demoapp.service.LocationService#getRegionsOfCurrentIndustry()
	 */
	@Test
	public void testGetRegionsOfCurrentIndustry_normal2() throws Throwable {
		// Given
		IndustryType currentIndustry = IndustryType.AEROSPACE;
		when(globalPreferencesService.getCurrentIndustry()).thenReturn(currentIndustry);

		// When
		List<RegionType> result = underTest.getRegionsOfCurrentIndustry();

		// Then
		assertNotNull(result);
		assertEquals(8, result.size());
	}

	/**
	 * Test for getRegionsOfCurrentIndustry()
	 *
	 * @see com.parasoft.demoapp.service.LocationService#getRegionsOfCurrentIndustry()
	 */
	@Test
	public void testGetRegionsOfCurrentIndustry_unsupportedOperationInCurrentIndustryException() throws Throwable {
		// Given
		IndustryType currentIndustry = IndustryType.GOVERNMENT; // do not support
		IndustryRoutingDataSource.currentIndustry = currentIndustry;

		// When
		String message = "";
		try {
			underTest.getRegionsOfCurrentIndustry();
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(OrderMessages.UNSUPPORTED_OPERATION_IN_CURRENT_INDUSTRY, currentIndustry),
				message);
	}

	/**
	 * Parasoft Jtest UTA: test for isCorrectRegionInCurrentIndustry(RegionType)
	 *
	 * @see com.parasoft.demoapp.service.LocationService#isCorrectRegionInCurrentIndustry(RegionType)
	 */
	@Test
	public void testIsCorrectRegionInCurrentIndustry_correctRegionInCurrentIndustry() throws Throwable {
		// Given
		IndustryType currentIndustry = IndustryType.DEFENSE;
		IndustryRoutingDataSource.currentIndustry = currentIndustry;

		// When
		RegionType region = RegionType.UNITED_STATES;
		boolean result = underTest.isCorrectRegionInCurrentIndustry(region);

		// Then
	    assertTrue(result);
	}
	
	/**
	 * Parasoft Jtest UTA: test for isCorrectRegionInCurrentIndustry(RegionType)
	 *
	 * @see com.parasoft.demoapp.service.LocationService#isCorrectRegionInCurrentIndustry(RegionType)
	 */
	@Test
	public void testIsCorrectRegionInCurrentIndustry_incorrectRegion() throws Throwable {
		// Given
		IndustryType currentIndustry = IndustryType.DEFENSE;
		IndustryRoutingDataSource.currentIndustry = currentIndustry;
		
		// When
		RegionType region = RegionType.EARTH; // incorrect region
		boolean result = underTest.isCorrectRegionInCurrentIndustry(region);
		
		// Then
		assertFalse(result);
	}
}