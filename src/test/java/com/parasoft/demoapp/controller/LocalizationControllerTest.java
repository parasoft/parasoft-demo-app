package com.parasoft.demoapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.HashMap;

import com.parasoft.demoapp.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.Gson;
import com.parasoft.demoapp.exception.LocalizationException;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.service.LocalizationService;

/**
 * Test class for LocalizationController
 *
 * @see com.parasoft.demoapp.controller.LocalizationController
 */
public class LocalizationControllerTest {
	@InjectMocks
	LocalizationController underTest;

	@Mock
	LocalizationService localizationService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for getLocalization(LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.controller.LocalizationController#getLocalization(LocalizationLanguageType)
	 */
	@Test
	public void testGetLocalization() throws Throwable {
		// Given
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		HashMap<String, String> map = new HashMap<>();
		String key = "TEST";
		String value = "test";
		map.put(key, value);
		Gson gSon = new Gson();
		String json = gSon.toJson(map);
		doReturn(json).when(localizationService).getLocalization(any(LocalizationLanguageType.class));

		// When
		ResponseResult<String> result = underTest.getLocalization(languageType);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(value, gSon.fromJson(result.getData(), HashMap.class).get(key));
	}

	/**
	 * Test for getLocalization(LocalizationLanguageType) with LocalizationException
	 *
	 * @see com.parasoft.demoapp.controller.LocalizationController#getLocalization(LocalizationLanguageType)
	 */
	@Test(expected = LocalizationException.class)
	public void testGetLocalization_exception() throws Throwable {
		// Given
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		doThrow(LocalizationException.class).when(localizationService).getLocalization(any(LocalizationLanguageType.class));

		// When
		underTest.getLocalization(languageType);
	}

	/**
	 * Test for getLocalization(String, LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.controller.LocalizationController#getLocalization(String, LocalizationLanguageType)
	 */
	@Test
	public void testGetLocalizationForKey() throws Throwable {
		// Given
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		String key = "TEST";
		String value = "test";
		doReturn(value).when(localizationService).getLocalization(anyString(), any(LocalizationLanguageType.class));

		// When
		ResponseResult<String> result = underTest.getLocalization(key, languageType);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(value, result.getData());
	}

	/**
	 * Test for getLocalization(String, LocalizationLanguageType) with ResourceNotFoundException
	 *
	 * @see com.parasoft.demoapp.controller.LocalizationController#getLocalization(String, LocalizationLanguageType)
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void testGetLocalizationForKey_ResourceNotFoundException() throws Throwable {
		// Given
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		String key = "TEST";
		doThrow(ResourceNotFoundException.class).when(localizationService).getLocalization(anyString(), any(LocalizationLanguageType.class));

		// When
		underTest.getLocalization(key, languageType);
	}
}