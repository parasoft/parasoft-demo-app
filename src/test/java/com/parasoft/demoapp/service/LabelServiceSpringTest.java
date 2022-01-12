/**
 * 
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.industry.LabelEntity;

/**
 * Test class for LabelService
 *
 * @see com.parasoft.demoapp.service.LabelService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class LabelServiceSpringTest {

	// Component under test
	@Autowired
	LabelService service;

	/**
	 * Test for updateLabelsInDB(List, LocalizationLanguageType) and getAllLabelsFromDBByLanguageType(LocalizationLanguageType)
	 * <br/>
	 * When update labels twice with same language type, the previous updated labels will be removed.
	 * @see com.parasoft.demoapp.service.LabelService#updateLabelsInDB(List, LocalizationLanguageType)
	 * @see com.parasoft.demoapp.service.LabelService#getAllLabelsFromDBByLanguageType(LocalizationLanguageType)
	 */
	@Test
	@Transactional(value = "industryTransactionManager")
	public void testUpdateLabelsInDB_normal_sameLanguageOfLabelCannotBothExist() throws Throwable {
		// Given
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;
		
		List<LabelEntity> labelEntities = new ArrayList<>();
		String labelName = "PROJECT_NAME";
		String labelValue = "value";
		LabelEntity item = new LabelEntity(labelName, labelValue, languageType);
		labelEntities.add(item);
		
		// When
		// Add new label with EN language type.
		List<LabelEntity> result = service.updateLabelsInDB(labelEntities, languageType);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(labelName, result.get(0).getName());
		assertEquals(labelValue, result.get(0).getValue());
		assertEquals(languageType, result.get(0).getLanguageType());
		
		// When
		// Get all EN labels
		result = service.getAllLabelsFromDBByLanguageType(languageType);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(labelName, result.get(0).getName());
		assertEquals(labelValue, result.get(0).getValue());
		assertEquals(languageType, result.get(0).getLanguageType());
		
		// Given
		labelEntities.clear();
		String newLabelName = "SIGN_OUT";
		String newLabelValue = "new value";
		item = new LabelEntity(newLabelName, newLabelValue, languageType);
		labelEntities.add(item);
		
		// When
		// Add another new label with EN language type, the previous EN label will be removed.
		result = service.updateLabelsInDB(labelEntities, languageType);

		assertEquals(newLabelName, result.get(0).getName());
		assertEquals(newLabelValue, result.get(0).getValue());
		assertEquals(languageType, result.get(0).getLanguageType());

		// When
		// Get all EN labels, , the previous EN label is removed.
		result = service.getAllLabelsFromDBByLanguageType(languageType);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertNotEquals(labelName, result.get(0).getName());
		assertNotEquals(labelValue, result.get(0).getValue());
	}

	/**
	 * Test for updateLabelsInDB(List, LocalizationLanguageType) and getAllLabelsFromDBByLanguageType(LocalizationLanguageType)
	 * <br/>
	 * When update labels twice with different language type, the previous updated labels will not be removed.
	 * @see com.parasoft.demoapp.service.LabelService#updateLabelsInDB(List, LocalizationLanguageType)
	 * @see com.parasoft.demoapp.service.LabelService#getAllLabelsFromDBByLanguageType(LocalizationLanguageType)
	 */
	@Test
	@Transactional(value = "industryTransactionManager")
	public void testUpdateLabelsInDB_normal_differentLanguageOfLabelCanBothExist() throws Throwable {
		// Given
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;

		List<LabelEntity> labelEntities = new ArrayList<>();
		String labelName = "PROJECT_NAME";
		String labelValue = "value";
		LabelEntity item = new LabelEntity(labelName, labelValue, languageType);
		labelEntities.add(item);

		// When
		// Add new label with EN language type.
		List<LabelEntity> result = service.updateLabelsInDB(labelEntities, languageType);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(labelName, result.get(0).getName());
		assertEquals(labelValue, result.get(0).getValue());
		assertEquals(languageType, result.get(0).getLanguageType());

		// When
		// Get all EN labels
		result = service.getAllLabelsFromDBByLanguageType(languageType);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(labelName, result.get(0).getName());
		assertEquals(labelValue, result.get(0).getValue());
		assertEquals(languageType, result.get(0).getLanguageType());

		// When
		// Add another new label with ZH language type, the previous label will not be removed
		LocalizationLanguageType newLanguageType  = LocalizationLanguageType.ZH;
		labelEntities.clear();
		labelEntities.add(new LabelEntity(labelName, labelValue, newLanguageType));
		result = service.updateLabelsInDB(labelEntities, newLanguageType);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(labelName, result.get(0).getName());
		assertEquals(labelValue, result.get(0).getValue());

		// When
		// Get all EN labels, EN labels and ZH labels are both existing in DB
		result = service.getAllLabelsFromDBByLanguageType(languageType);

		// Then
		assertEquals(1, result.size());
		assertEquals(labelName, result.get(0).getName());
		assertEquals(labelValue, result.get(0).getValue());
		assertEquals(languageType, result.get(0).getLanguageType());

		// When
		// Get all ZH labels, EN labels and ZH labels are both existing in DB
		result = service.getAllLabelsFromDBByLanguageType(newLanguageType);

		// Then
		assertEquals(1, result.size());
		assertEquals(labelName, result.get(0).getName());
		assertEquals(labelValue, result.get(0).getValue());
		assertEquals(newLanguageType, result.get(0).getLanguageType());
	}

	/**
	 * Test for updateLabelsInDB(List, LocalizationLanguageType)
	 * <br/>
	 * Name of To be updated label do not exist in properties file, only update labels which name is existing in properties file.
	 * @see com.parasoft.demoapp.service.LabelService#updateLabelsInDB(List, LocalizationLanguageType)
	 */
	@Test
	public void testUpdateLabelsInDB_exception_labelNameNotExistsInPropertiesFile() {
		// Given
		LocalizationLanguageType languageType = LocalizationLanguageType.EN;

		List<LabelEntity> labelEntities = new ArrayList<>();
		String labelName = "NOT_EXISTED_NAME_XXX";  // This name dose not exist in localization properties file.
		String labelValue = "value";
		LabelEntity item = new LabelEntity(labelName, labelValue, languageType);
		labelEntities.add(item);

		// When
		String message = "";

		try {
			service.updateLabelsInDB(labelEntities, languageType);
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		assertEquals(MessageFormat.format(GlobalPreferencesMessages.ILLEGAL_LABEL_NAME, labelName) , message);
	}

}