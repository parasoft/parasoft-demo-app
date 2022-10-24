package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.DataRecreatable;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
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
@Order(5) // The order of default data creation
public class AerospaceAssetsCreator extends AbstractDataCreator implements DataRecreatable {

	@Autowired
	@Qualifier("industryDataSource")
	protected DataSource industryDataSource;

	@Value("classpath:sql/defaultdata/aerospace/tbl_category-defaultData.sql")
	protected Resource aerospaceDefaultCategoriesSql;

	@Value("classpath:sql/defaultdata/aerospace/tbl_item-defaultData.sql")
	protected Resource aerospaceDefaultItemsSql;

	@Value("classpath:sql/defaultdata/aerospace/tbl_item_inventory-defaultData.sql")
	protected Resource aerospaceDefaultItemInventorySql;

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.AEROSPACE));

		IndustryRoutingDataSource.currentIndustry = IndustryType.AEROSPACE;
	}

	@Override
	public void populateData() {

		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_CATEGORIES_ITEMS), IndustryType.AEROSPACE));

		// create categories
		dataInitialize(industryDataSource, aerospaceDefaultCategoriesSql);

		// create items
		dataInitialize(industryDataSource, aerospaceDefaultItemsSql);

		// create item inventory
		dataInitialize(industryDataSource, aerospaceDefaultItemInventorySql);

		log.info(messages.getString(DatabaseOperationMessages.WRITE_DONE));
	}

	@Override
	public void recreateData() {
		create();
	}
}
