package com.parasoft.demoapp.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.VirtualizeServerUrlException;
import com.parasoft.demoapp.model.global.preferences.DataAccessMode;
import com.parasoft.demoapp.model.global.preferences.DemoBugsType;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesMQService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ParasoftJDBCProxyService;
import com.parasoft.demoapp.service.RestEndpointService;

/**
 * test class for GlobalPreferencesController
 *
 * @see GlobalPreferencesController
 */
public class GlobalPreferencesControllerTest {

	@InjectMocks
	GlobalPreferencesController underTest;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	@Mock
	RestEndpointService restEndpointService;

	@Mock
	ParasoftJDBCProxyService parasoftJDBCProxyService;

	@Mock
	GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService;

	@Mock
	GlobalPreferencesMQService globalPreferencesMQService;
	
	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for doSaveChanges(IndustryType)
	 *
	 * @see GlobalPreferencesController#doSaveChanges(GlobalPreferencesDTO)
	 */
	@Test
	public void testDoSaveChanges_normal() throws Throwable {
		// Given
		GlobalPreferencesEntity updateGlobalPreferencesResult = new GlobalPreferencesEntity();
		updateGlobalPreferencesResult.setIndustryType(IndustryType.DEFENSE);

		when(globalPreferencesService.updateGlobalPreferences(any(GlobalPreferencesDTO.class)))
				.thenReturn(updateGlobalPreferencesResult);

		// When
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		IndustryType industryType = IndustryType.DEFENSE;
		DemoBugsType[] demoBugs = { DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS };
		Boolean advertisingEnabled = false;
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setDataAccessMode(dataAccessMode);
		globalPreferencesDto.setSoapEndPoint(soapEndPoint);
		globalPreferencesDto.setIndustryType(industryType);
		globalPreferencesDto.setDemoBugs(demoBugs);
		globalPreferencesDto.setAdvertisingEnabled(advertisingEnabled);

		ResponseResult<GlobalPreferencesEntity> result = underTest.doSaveChanges(globalPreferencesDto);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(industryType, result.getData().getIndustryType());
	}

	/**
	 * test for doSaveChanges(IndustryType)
	 *
	 * @see GlobalPreferencesController#doSaveChanges(GlobalPreferencesDTO)
	 */
	@Test(expected = GlobalPreferencesNotFoundException.class)
	public void testDoSaveChanges_globalPreferencesNotFoundException() throws Throwable {
		// Given
		when(globalPreferencesService.updateGlobalPreferences(any(GlobalPreferencesDTO.class)))
				.thenThrow(GlobalPreferencesNotFoundException.class);

		// When
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		IndustryType industryType = IndustryType.DEFENSE;
		DemoBugsType[] demoBugs = { DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS };
		Boolean advertisingEnabled = false;
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setDataAccessMode(dataAccessMode);
		globalPreferencesDto.setSoapEndPoint(soapEndPoint);
		globalPreferencesDto.setIndustryType(industryType);
		globalPreferencesDto.setDemoBugs(demoBugs);
		globalPreferencesDto.setAdvertisingEnabled(advertisingEnabled);

		underTest.doSaveChanges(globalPreferencesDto);

	}

	/**
	 * test for doSaveChanges(IndustryType)
	 *
	 * @see GlobalPreferencesController##doSaveChanges(GlobalPreferencesDTO)
	 */
	@Test(expected = GlobalPreferencesMoreThanOneException.class)
	public void testDoSaveChanges_globalPreferencesMoreThanOneException() throws Throwable {
		// Given
		when(globalPreferencesService.updateGlobalPreferences(any(GlobalPreferencesDTO.class)))
						.thenThrow(GlobalPreferencesMoreThanOneException.class);

		// When
		DataAccessMode dataAccessMode = DataAccessMode.JDBC;
		String soapEndPoint = "";
		IndustryType industryType = IndustryType.DEFENSE;
		DemoBugsType[] demoBugs = { DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS };
		Boolean advertisingEnabled = false;
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setDataAccessMode(dataAccessMode);
		globalPreferencesDto.setSoapEndPoint(soapEndPoint);
		globalPreferencesDto.setIndustryType(industryType);
		globalPreferencesDto.setDemoBugs(demoBugs);
		globalPreferencesDto.setAdvertisingEnabled(advertisingEnabled);

		underTest.doSaveChanges(globalPreferencesDto);

	}

	/**
	 * test for doSaveChanges(IndustryType)
	 *
	 * @see GlobalPreferencesController##doSaveChanges(GlobalPreferencesDTO)
	 */
	@Test(expected = ParameterException.class)
	public void testDoSaveChanges_parameterException() throws Throwable {
		// Given
		when(globalPreferencesService.updateGlobalPreferences(any(GlobalPreferencesDTO.class)))
				.thenThrow(ParameterException.class);

		// When
		IndustryType industryType = null;
		Boolean advertisingEnabled = null;
		GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
		globalPreferencesDto.setIndustryType(industryType);
		globalPreferencesDto.setAdvertisingEnabled(advertisingEnabled);

		underTest.doSaveChanges(globalPreferencesDto);
	}

	/**
	 * test for getCurrentIndustry()
	 *
	 * @see GlobalPreferencesController#getCurrentIndustry()
	 */
	@Test
	public void testGetCurrentIndustry_normal() throws Throwable {
		// Given
		IndustryType getCurrentIndustryResult = IndustryType.DEFENSE;
		when(globalPreferencesService.getCurrentIndustry()).thenReturn(getCurrentIndustryResult);

		// When
		ResponseResult<IndustryType> result = underTest.getCurrentIndustry();

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(IndustryType.DEFENSE, result.getData());
	}

	/**
	 * test for getCurrentIndustry()
	 *
	 * @see GlobalPreferencesController#getCurrentIndustry()
	 */
	@Test(expected = GlobalPreferencesNotFoundException.class)
	public void testGetCurrentIndustry_globalPreferencesNotFoundException() throws Throwable {
		// Given
		when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

		// When
		underTest.getCurrentIndustry();

	}

	/**
	 * test for getCurrentIndustry()
	 *
	 * @see GlobalPreferencesController#getCurrentIndustry()
	 */
	@Test(expected = GlobalPreferencesMoreThanOneException.class)
	public void testGetCurrentIndustry_globalPreferencesMoreThanOneException() throws Throwable {
		// Given
		when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

		// When
		underTest.getCurrentIndustry();

	}

	/**
	 * test for getCurrentPreferences()
	 *
	 * @see GlobalPreferencesController#getCurrentPreferences()
	 */
	@Test
	public void testGetCurrentPreferences_normal() throws Throwable {
		// Given
		GlobalPreferencesEntity getCurrentGlobalPreferencesResult = new GlobalPreferencesEntity();
		getCurrentGlobalPreferencesResult.setIndustryType(IndustryType.DEFENSE);
		when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(getCurrentGlobalPreferencesResult);

		// When
		ResponseResult<GlobalPreferencesEntity> result = underTest.getCurrentPreferences();

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(IndustryType.DEFENSE, result.getData().getIndustryType());
	}

	/**
	 * test for getCurrentPreferences()
	 *
	 * @see GlobalPreferencesController#getCurrentPreferences()
	 */
	@Test(expected = GlobalPreferencesNotFoundException.class)
	public void testGetCurrentPreferences_globalPreferencesNotFoundException() throws Throwable {
		// Given
		when(globalPreferencesService.getCurrentGlobalPreferences())
				.thenThrow(GlobalPreferencesNotFoundException.class);

		// When
		underTest.getCurrentPreferences();

	}

	/**
	 * test for getCurrentPreferences()
	 *
	 * @see GlobalPreferencesController#getCurrentPreferences()
	 */
	@Test(expected = GlobalPreferencesMoreThanOneException.class)
	public void testGetCurrentPreferences_globalPreferencesMoreThanOneException() throws Throwable {
		// Given
		when(globalPreferencesService.getCurrentGlobalPreferences())
				.thenThrow(GlobalPreferencesMoreThanOneException.class);

		// When
		underTest.getCurrentPreferences();

	}

	/**
	 * Test for resetAllIndustriesDatabase()
	 *
	 * @see com.parasoft.demoapp.controller.GlobalPreferencesController#resetAllIndustriesDatabase()
	 */
	@Test
	public void testResetAllIndustriesDatabase() throws Throwable {
		// Given
		doNothing().when(globalPreferencesService).resetAllIndustriesDatabase();
		// When
		ResponseResult<Void> result = underTest.resetAllIndustriesDatabase();

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for clearCurrentIndustryDatabase()
	 *
	 * @see com.parasoft.demoapp.controller.GlobalPreferencesController#clearCurrentIndustryDatabase()
	 */
	@Test
	public void testClearCurrentIndustryDatabase() throws Throwable {
		// Given
		doNothing().when(globalPreferencesService).clearCurrentIndustryDatabase();
		// When
		ResponseResult<Void> result = underTest.clearCurrentIndustryDatabase();

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for validateUrl(String)
	 *
	 * @see com.parasoft.demoapp.controller.GlobalPreferencesController#validateParasoftVirtualizeServerUrl(String)
	 */
	@Test
	public void testValidateUrl() throws Throwable {
		// Given
		doNothing().when(parasoftJDBCProxyService).validateVirtualizeServerUrl(anyString());

		// When
		String url = "http://www.baidu.com";
		ResponseResult<Void> result = underTest.validateParasoftVirtualizeServerUrl(url);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for validateSpecificUrl(String)
	 *
	 * @see com.parasoft.demoapp.controller.GlobalPreferencesController#validateParasoftVirtualizeServerUrl(String)
	 */
	@Test(expected = VirtualizeServerUrlException.class)
	public void testValidateUrl_VirtualizeServerUrlException() throws Throwable {
		// Given
		doThrow(VirtualizeServerUrlException.class).when(parasoftJDBCProxyService).validateVirtualizeServerUrl(anyString());

		// When
		String url = "url";
		underTest.validateParasoftVirtualizeServerUrl(url);
	}

	/**
	 * Test for getDefaultPreferences()
	 *
	 * @see com.parasoft.demoapp.controller.GlobalPreferencesController#getDefaultPreferences()
	 */
	@Test
	public void testGetDefaultPreferences_normal() throws Throwable {
		// Given
		GlobalPreferencesEntity globalPreferencesEntity = mock(GlobalPreferencesEntity.class);
		when(globalPreferencesDefaultSettingsService.defaultPreferences()).thenReturn(globalPreferencesEntity);

		// When
		ResponseResult<GlobalPreferencesEntity> result = underTest.getDefaultPreferences();

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(globalPreferencesEntity, result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}
}