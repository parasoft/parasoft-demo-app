package com.parasoft.demoapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.repository.global.GlobalPreferencesRepository;

/**
 * test class for GlobalPreferencesService
 *
 * @see GlobalPreferencesService
 */
public class GlobalPreferencesServiceTest2 {

	@InjectMocks
	GlobalPreferencesService underTest;

	@Mock
	GlobalPreferencesRepository globalPreferencesRepository;

	@Mock
	GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService;

	@BeforeEach
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for getDefaultIndustry()
	 *
	 * @see GlobalPreferencesService#getDefaultIndustry()
	 */
	@Test
	public void testGetDefaultIndustry_normal() throws Throwable {
		// Given
		when(globalPreferencesDefaultSettingsService.defaultIndustry()).thenReturn(IndustryType.DEFENSE);

		// When
		IndustryType result = underTest.getDefaultIndustry();

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.DEFENSE, result);
	}

	/**
	 * test for getCurrentIndustry()
	 *
	 * @see GlobalPreferencesService#getCurrentIndustry()
	 */
	@Test
	public void testGetCurrentIndustry_normal() throws Throwable {
		// Given
		GlobalPreferencesEntity current = mock(GlobalPreferencesEntity.class);
		when(globalPreferencesRepository.findAll()).thenReturn(Arrays.asList(current));
		when(current.getIndustryType()).thenReturn(IndustryType.DEFENSE);

		// When
		IndustryType result = underTest.getCurrentIndustry();

		// Then
		assertNotNull(result);
		assertEquals(IndustryType.DEFENSE, result);
	}

}