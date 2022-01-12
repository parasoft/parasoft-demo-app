/**
 * 
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.industry.LabelEntity;
import com.parasoft.demoapp.repository.industry.LabelRepository;

/**
 * Test class for LabelService
 *
 * @see com.parasoft.demoapp.service.LabelService
 */
public class LabelServiceTest {
	
	@InjectMocks
	LabelService underTest;
	
	@Mock
	LabelRepository labelRepository;

	@Mock
	LocalizationService localizationService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for updateLabelsInDB(List, LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.service.LabelService#updateLabelsInDB(List, LocalizationLanguageType)
	 */
	@Test
	public void testUpdateLabelsInDB_nullLabelEntities() throws Throwable {

		// Given
		List<LabelEntity> labelEntities = null;
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		
		// When
		String message = "";
		try {
			underTest.updateLabelsInDB(labelEntities, languageType);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		
		assertEquals(GlobalPreferencesMessages.LABELS_CANNOT_BE_NULL, message);
		
	}
	
	/**
	 * Test for updateLabelsInDB(List, LocalizationLanguageType)
	 *
	 * @see com.parasoft.demoapp.service.LabelService#updateLabelsInDB(List, LocalizationLanguageType)
	 */
	@Test
	public void testUpdateLabelsInDB_nullLanguageType() throws Throwable {

		// Given
		List<LabelEntity> labelEntities = new ArrayList<>();
		LocalizationLanguageType languageType = null;
		
		// When
		String message = "";
		try {
			underTest.updateLabelsInDB(labelEntities, languageType);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		
		assertEquals(GlobalPreferencesMessages.LOCALIZATION_LANGUAGE_TYPE_CANNOT_BE_NULL, message);
		
	}
}