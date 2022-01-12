/**
 * 
 */
package com.parasoft.demoapp.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.dto.LabelsRequestDTO;
import com.parasoft.demoapp.dto.LabelsResponseDTO;
import com.parasoft.demoapp.exception.LocalizationException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.industry.LabelEntity;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.LabelService;
import com.parasoft.demoapp.service.LocalizationService;

/**
 * Test class for LabelController
 *
 * @see com.parasoft.demoapp.controller.LabelController
 */
public class LabelControllerTest {

	// Object under test
	@InjectMocks
	LabelController underTest;

	@Mock
	LabelService labelService;

	@Mock
	LocalizationService localizationService;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for updateLabels(LabelsRequestDTO)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#updateLabels(LabelsRequestDTO)
	 */
	@Test
	public void testUpdateLabels_normal() throws Throwable {
		// Given
		List<LabelEntity> updateLabelsInDBResult = new ArrayList<>();

		String key = "LABEL_NAME";
		String value = "label value";
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		LabelEntity item = new LabelEntity(key, value, languageType);
		updateLabelsInDBResult.add(item);
		doReturn(updateLabelsInDBResult).when(labelService).updateLabelsInDB((List) any(),
				nullable(LocalizationLanguageType.class));

		GlobalPreferencesEntity globalPreferencesEntity = mock(GlobalPreferencesEntity.class);
		when(globalPreferencesService.updateLabelOverridedStatus(anyBoolean())).thenReturn(globalPreferencesEntity);
		boolean isLabelsOverrided = true;
		when(globalPreferencesEntity.isLabelsOverrided()).thenReturn(isLabelsOverrided);

		// When
		LabelsRequestDTO labelsRequestDTO = new LabelsRequestDTO();
		labelsRequestDTO.setLabelPairs(new HashMap<String, String>());

		labelsRequestDTO.setLanguageType(languageType);
		labelsRequestDTO.setLabelsOverrided(isLabelsOverrided);
		ResponseResult<LabelsResponseDTO> result = underTest.updateLabels(labelsRequestDTO);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertTrue(result.getData().getLabelPairs().containsKey(key));
		assertTrue(result.getData().getLabelPairs().containsValue(value));
		assertEquals(languageType, result.getData().getLanguageType());
		assertEquals(isLabelsOverrided, result.getData().isLabelsOverrided());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for updateLabels(LabelsRequestDTO)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#updateLabels(LabelsRequestDTO)
	 */
	@Test(expected = ParameterException.class)
	public void testUpdateLabels_parameterException() throws Throwable {
		// Given
		List<LabelEntity> updateLabelsInDBResult = new ArrayList<>();
		LabelEntity item = new LabelEntity();
		updateLabelsInDBResult.add(item);
		doThrow(ParameterException.class).when(labelService).updateLabelsInDB((List) any(),
				nullable(LocalizationLanguageType.class));

		// When
		LabelsRequestDTO labelsRequestDTO = new LabelsRequestDTO();
		labelsRequestDTO.setLabelPairs(new HashMap<String, String>());
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		labelsRequestDTO.setLanguageType(languageType);
		underTest.updateLabels(labelsRequestDTO);
	}

	/**
	 * Test for updateLabels(LabelsRequestDTO)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#updateLabels(LabelsRequestDTO)
	 */
	@Test(expected = LocalizationException.class)
	public void testUpdateLabels_localizationException() throws Throwable {
		// Given
		List<LabelEntity> updateLabelsInDBResult = new ArrayList<>();
		LabelEntity item = new LabelEntity();
		updateLabelsInDBResult.add(item);
		doThrow(LocalizationException.class).when(labelService).updateLabelsInDB((List) any(),
				nullable(LocalizationLanguageType.class));

		// When
		LabelsRequestDTO labelsRequestDTO = new LabelsRequestDTO();
		labelsRequestDTO.setLabelPairs(new HashMap<String, String>());
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		labelsRequestDTO.setLanguageType(languageType);
		underTest.updateLabels(labelsRequestDTO);
	}

	/**
	 * Test for getOverridedLabels(LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#getOverridedLabels(LocalizationLanguageType)
	 */
	@Test
	public void testGetOverridedLabels_normal() throws Throwable {
		// Given
		Map<String, String> loadAllPropertiesResult = new HashMap<>();
		String key = "LABEL_NAME";
		String value = "label value";
		loadAllPropertiesResult.put(key, value);
		doReturn(loadAllPropertiesResult).when(localizationService)
				.loadPropertiesFromDB(nullable(LocalizationLanguageType.class));

		// When
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		ResponseResult<LabelsResponseDTO> result = underTest.getOverridedLabels(languageType);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertTrue(result.getData().getLabelPairs().containsKey(key));
		assertTrue(result.getData().getLabelPairs().containsValue(value));
		assertEquals(languageType, result.getData().getLanguageType());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for getOverridedLabels(LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#getOverridedLabels(LocalizationLanguageType)
	 */
	@Test(expected = ParameterException.class)
	public void testGetOverridedLabels_parameterException() throws Throwable {
		// Given
		Map<String, String> loadAllPropertiesResult = new HashMap<>();
		String key = "LABEL_NAME";
		String value = "label value";
		loadAllPropertiesResult.put(key, value);
		doThrow(ParameterException.class).when(localizationService)
				.loadPropertiesFromDB(nullable(LocalizationLanguageType.class));

		// When
		LocalizationLanguageType languageType = null;
		underTest.getOverridedLabels(languageType);
	}

	/**
	 * Test for getDefaultLabels(LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#getDefaultLabels(LocalizationLanguageType)
	 */
	@Test
	public void testGetDefaultLabels_normal() throws Throwable {
		// When
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		ResponseResult<LabelsResponseDTO> result = underTest.getDefaultLabels(languageType);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertFalse(result.getData().isLabelsOverrided());
		assertEquals(languageType, result.getData().getLanguageType());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for getDefaultLabels(LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#getDefaultLabels(LocalizationLanguageType)
	 */
	@Test(expected = LocalizationException.class)
	public void testGetDefaultLabels_localizationException() throws Throwable {
		// Given
		Map<String, String> loadAllPropertiesResult = new HashMap<>();
		String key = "LABEL_NAME";
		String value = "label value";
		loadAllPropertiesResult.put(key, value);
		doThrow(LocalizationException.class).when(localizationService)
				.loadPropertiesFromFile(nullable(LocalizationLanguageType.class));

		// When
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		underTest.getDefaultLabels(languageType);
	}

	/**
	 * Test for getDefaultLabels(LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.controller.LabelController#getDefaultLabels(LocalizationLanguageType)
	 */
	@Test(expected = ParameterException.class)
	public void testGetDefaultLabels_parameterException() throws Throwable {
		// Given
		Map<String, String> loadAllPropertiesResult = new HashMap<>();
		String key = "LABEL_NAME";
		String value = "label value";
		loadAllPropertiesResult.put(key, value);
		doThrow(ParameterException.class).when(localizationService)
				.loadPropertiesFromFile(nullable(LocalizationLanguageType.class));

		// When
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		underTest.getDefaultLabels(languageType);
	}
}