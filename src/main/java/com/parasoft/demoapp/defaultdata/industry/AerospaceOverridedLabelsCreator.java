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
@Order(9) // The order of default data creation
public class AerospaceOverridedLabelsCreator extends AbstractOverridedLabelsCreator {

	@Autowired
	public AerospaceOverridedLabelsCreator(LabelService labelService, LocalizationService localizationService,
                                           GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService){

		super(labelService, localizationService, globalPreferencesDefaultSettingsService);
	}

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.AEROSPACE));

		IndustryRoutingDataSource.currentIndustry = IndustryType.AEROSPACE;
	}

	@Override
	public void populateData() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_OVERRIDED_LABELS), IndustryType.AEROSPACE));

		Set<String> names = globalPreferencesDefaultSettingsService.defaultOverridedLabelNamesForaAerospaceIndustry();
		handleAndStoreLabels(names, IndustryType.AEROSPACE);

		log.info(messages.getString(DatabaseOperationMessages.WRITE_DONE));
	}
}
