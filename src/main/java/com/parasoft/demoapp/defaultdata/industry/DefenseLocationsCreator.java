package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.text.MessageFormat;

@Component
@Slf4j
@Order(4) // The order of default data creation
public class DefenseLocationsCreator extends AbstractDataCreator {

	@Autowired
	@Qualifier("industryDataSource")
	protected DataSource industryDataSource;

	@Value("classpath:sql/defaultdata/defense/tbl_location-defaultData.sql")
	protected Resource defenseDefaultLocationsSql;

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.DEFENSE));

		IndustryRoutingDataSource.currentIndustry = IndustryType.DEFENSE;
	}

	@Override
	public void populateData() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_LOCATIONS), IndustryType.DEFENSE));

		// create locations
		dataInitialize(industryDataSource, defenseDefaultLocationsSql);

		log.info(messages.getString(DatabaseOperationMessages.WRITE_DONE));
	}

}
