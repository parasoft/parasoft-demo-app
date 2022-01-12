package com.parasoft.demoapp.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.parasoft.demoapp.exception.LocalizationException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parasoft.demoapp.model.industry.LabelEntity;
import com.parasoft.demoapp.repository.industry.LabelRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabelService {
	
	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private LocalizationService localizationService;
	
	public List<LabelEntity> getAllLabelsFromDBByLanguageType(LocalizationLanguageType languageType){
		return labelRepository.findAllByLanguageType(languageType);
	}

	@Transactional(value = "industryTransactionManager")
	public List<LabelEntity> updateLabelsInDB(List<LabelEntity> labelEntities, LocalizationLanguageType languageType)
			throws ParameterException, LocalizationException {

		ParameterValidator.requireNonNull(labelEntities, GlobalPreferencesMessages.LABELS_CANNOT_BE_NULL);
		ParameterValidator.requireNonNull(languageType, GlobalPreferencesMessages.LOCALIZATION_LANGUAGE_TYPE_CANNOT_BE_NULL);

		// Only save labels when all name of labels are existing in properties file.
		Map<String, String> propertiesFromFile = localizationService.loadPropertiesFromFile(languageType);
		for(LabelEntity labelEntity : labelEntities){
			if(!propertiesFromFile.containsKey(labelEntity.getName())){
				throw new ParameterException(MessageFormat.format(GlobalPreferencesMessages.ILLEGAL_LABEL_NAME, labelEntity.getName()));
			}
		}

		clearAllLabelsInDBByLanguageType(languageType);

		return addLabelsIntoDB(labelEntities);
	}

	private List<LabelEntity> addLabelsIntoDB(List<LabelEntity> labelEntities) {

		return labelRepository.saveAll(labelEntities);
	}

	private void clearAllLabelsInDBByLanguageType(LocalizationLanguageType languageType) {

		labelRepository.deleteAllByLanguageType(languageType);
	}
}
