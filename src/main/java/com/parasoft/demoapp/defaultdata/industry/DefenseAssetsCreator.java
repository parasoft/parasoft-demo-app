package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.defaultdata.DataRecreatable;
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
@Order(3) // The order of default data creation
public class DefenseAssetsCreator extends AbstractDataCreator implements DataRecreatable {

	protected AssetMessages assetMessages = new AssetMessages();

	@Autowired
	@Qualifier("industryDataSource")
	protected DataSource industryDataSource;

	@Value("classpath:sql/defaultdata/defense/tbl_category-defaultData.sql")
	protected Resource defenseDefaultCategoriesSql;

	@Value("classpath:sql/defaultdata/defense/tbl_item-defaultData.sql")
	protected Resource defenseDefaultItemsSql;

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.DEFENSE));

		IndustryRoutingDataSource.currentIndustry = IndustryType.DEFENSE;
	}

	@Override
	public void populateData() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_CATEGORIES_ITEMS), IndustryType.DEFENSE));

		// create categories
		dataInitialize(industryDataSource, defenseDefaultCategoriesSql);

		// create items
		dataInitialize(industryDataSource, defenseDefaultItemsSql);

		log.info(assetMessages.getString(DatabaseOperationMessages.WRITE_DONE));
	}

	@Override
	public void recreateData() {
		create();
	}
}
