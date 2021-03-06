package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.messages.AssetMessages;
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
@Order(8) // The order of default data creation
public class OutdoorIndustryLocationsCreator extends AbstractDataCreator {

	protected AssetMessages assetMessages = new AssetMessages();

	@Autowired
	@Qualifier("industryDataSource")
	protected DataSource industryDataSource;

	@Value("classpath:sql/defaultdata/outdoor/tbl_location-defaultData.sql")
	protected Resource aerospaceDefaultLocationsSql;

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.OUTDOOR));

		IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;
	}

	@Override
	public void populateData() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_LOCATIONS), IndustryType.OUTDOOR));

		// create locations
		dataInitialize(industryDataSource, aerospaceDefaultLocationsSql);

		log.info(assetMessages.getString(DatabaseOperationMessages.WRITE_DONE));
	}

}
