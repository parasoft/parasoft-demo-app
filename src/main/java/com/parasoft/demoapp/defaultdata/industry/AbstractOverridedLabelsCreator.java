package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.LabelEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.LabelService;
import com.parasoft.demoapp.service.LocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractOverridedLabelsCreator extends AbstractDataCreator {

	protected LabelService labelService;

	protected LocalizationService localizationService;

	protected GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService;

	@Autowired
	public AbstractOverridedLabelsCreator(LabelService labelService, LocalizationService localizationService,
										  GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService){
		this.labelService = labelService;
		this.localizationService = localizationService;
		this.globalPreferencesDefaultSettingsService = globalPreferencesDefaultSettingsService;
	}

	protected void handleAndStoreLabels(Set<String> labelsNames, IndustryType industryType) {
		for(LocalizationLanguageType languageType : LocalizationLanguageType.values()){
			List<LabelEntity> defaultLabels = new ArrayList<>();

			Map<String, String> labelsPairs = null;
			try {
				labelsPairs = localizationService.loadPropertiesFromFile(languageType);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			for(String labelName : labelsNames){
				if(labelsPairs.containsKey(labelName)){
					defaultLabels.add(new LabelEntity(labelName, labelsPairs.get(labelName), languageType));
				}else{
					throw new RuntimeException(MessageFormat.format(
							messages.getString(DatabaseOperationMessages.FAILED_TO_WRITE_DEFAULT_OVERRIDED_LABELS), labelName, industryType));
				}
			}

			// create labels
			try {
				labelService.updateLabelsInDB(defaultLabels, languageType);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
}
