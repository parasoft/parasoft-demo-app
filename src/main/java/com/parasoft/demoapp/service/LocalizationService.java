package com.parasoft.demoapp.service;

import com.google.gson.Gson;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.exception.LocalizationException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.messages.LocalizationMessages;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.LabelEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;


@Service
public class LocalizationService {

	private static final String FILE_RESOURCE_PATH = "i18n/%s/common";
	private static final String FILE_EXTENSION = ".properties";

	@Autowired
	private LabelService labelService;

	@Autowired
	private GlobalPreferencesService globalPreferencesService;

	public String getLocalization(LocalizationLanguageType languageType) throws LocalizationException, ParameterException {

		return getJSON(languageType);
	}

	public String getLocalization(String key, LocalizationLanguageType languageType) throws LocalizationException, ParameterException {

		return loadAllProperties(languageType, false).get(key);
	}

	/**
	 * to get the content of corresponding properties files according to incoming language, like: 'en','zh', etc.
	 * @param languageType {@link LocalizationLanguageType}
	 * @return json string
	 */
	private String getJSON(LocalizationLanguageType languageType) throws LocalizationException, ParameterException {
		Gson gSon = new Gson();

		Map<String, String> map = loadAllProperties(languageType, false);

		return gSon.toJson(map);
	}

	/**
	 * Get properties from properties file first, and then
	 * get properties from DB, and override the value with value from DB if properties file and DB have the same property name.
	 * If value from DB is blank, it will not override value from file.
	 * @param languageType {@link LocalizationLanguageType}
	 * @return map
	 * @throws LocalizationException When failed to load properties form file.
	 */
	public Map<String, String> loadAllProperties(LocalizationLanguageType languageType, boolean mandatoryOverride)
			throws LocalizationException, ParameterException {

		ParameterValidator.requireNonNull(languageType, GlobalPreferencesMessages.LOCALIZATION_LANGUAGE_TYPE_CANNOT_BE_NULL);

		HashMap<String, String> map = new HashMap<>();

		map.putAll(loadPropertiesFromFile(languageType));

		boolean labelsOverrided = globalPreferencesService.getLabelOverridedStatus();

		if(mandatoryOverride || labelsOverrided){
			// Get properties from DB, and override value from file.
			loadPropertiesFromDB(languageType).forEach((key, value) -> {
				if(!StringUtils.isBlank(value)){
					map.put(key, value);
				}
			});
		}

		return map;
	}

	public Map<String, String> loadPropertiesFromDB(LocalizationLanguageType languageType) throws ParameterException {
		ParameterValidator.requireNonNull(languageType, GlobalPreferencesMessages.LOCALIZATION_LANGUAGE_TYPE_CANNOT_BE_NULL);

		HashMap<String, String> map = new HashMap<>();
		List<LabelEntity> labels = labelService.getAllLabelsFromDBByLanguageType(languageType);
		for(LabelEntity label : labels) {
			map.put(label.getName(), label.getValue());
		}

		return map;
	}

	public Map<String, String> loadPropertiesFromFile(LocalizationLanguageType languageType)
			throws LocalizationException, ParameterException {

		ParameterValidator.requireNonNull(languageType, GlobalPreferencesMessages.LOCALIZATION_LANGUAGE_TYPE_CANNOT_BE_NULL);

		HashMap<String, String> map = new HashMap<>();

		Properties localizationProperties = new Properties();
		String propertiesFilePath = getFileResourcePath(languageType);

		InputStream input = LocalizationService.class.getClassLoader().getResourceAsStream(propertiesFilePath);

		try {
			localizationProperties.load(input);
			Iterator<?> iterator = localizationProperties.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<?, ?> localizationEntry = (Entry<?, ?>) iterator.next();
				String localizationKey = (String) localizationEntry.getKey();
				String localizationValue = (String) localizationEntry.getValue();
				map.put(localizationKey, localizationValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new LocalizationException(LocalizationMessages.FAILED_TO_LOAD_PROPERTIES_FILE, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		return map;
	}

	private String getFileResourcePath(LocalizationLanguageType languageType){
		IndustryType currentIndustry = IndustryRoutingDataSource.currentIndustry;

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format(FILE_RESOURCE_PATH, currentIndustry.getValue()));

		stringBuilder.append(languageType.getPropertiesFileSuffix()).append(FILE_EXTENSION);

		return stringBuilder.toString();
	}
}
