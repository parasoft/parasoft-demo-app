/**
 * 
 */
package com.parasoft.demoapp.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.industry.LabelEntity;

/**
 * Test class for LabelsRequestDTO
 *
 * @see com.parasoft.demoapp.dto.LabelsRequestDTO
 */
public class LabelsRequestDTOTest {

	/**
	 * Test for toEntities()
	 *
	 * @see com.parasoft.demoapp.dto.LabelsRequestDTO#toEntities()
	 */
	@Test
	public void testToEntities_nullLabelPairs() throws Throwable {
		// Given
		LabelsRequestDTO underTest = new LabelsRequestDTO();
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		underTest.setLanguageType(languageType);
		underTest.setLabelPairs(null);

		// When
		List<LabelEntity> result = underTest.toEntities();

		// Then
		 assertNotNull(result);
		 assertEquals(0, result.size());
	}
}