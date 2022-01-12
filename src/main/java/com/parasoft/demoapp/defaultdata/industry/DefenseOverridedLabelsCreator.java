package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.LabelService;
import com.parasoft.demoapp.service.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Set;

@Component
@Slf4j
@Order(10) // The order of default data creation
public class DefenseOverridedLabelsCreator extends AbstractOverridedLabelsCreator {

	@Autowired
	public DefenseOverridedLabelsCreator(LabelService labelService, LocalizationService localizationService,
										 GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService){

		super(labelService, localizationService, globalPreferencesDefaultSettingsService);
	}

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.DEFENSE));

		IndustryRoutingDataSource.currentIndustry = IndustryType.DEFENSE;
	}

	@Override
	public void populateData() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_OVERRIDED_LABELS), IndustryType.DEFENSE));

		Set<String> names = globalPreferencesDefaultSettingsService.defaultOverridedLabelNamesForaDefenseIndustry();
		handleAndStoreLabels(names, IndustryType.DEFENSE);

		log.info(messages.getString(DatabaseOperationMessages.WRITE_DONE));
	}
}
