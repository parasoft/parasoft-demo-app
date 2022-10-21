package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.defaultdata.DataRecreatable;
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
@Order(7) // The order of default data creation
public class OutdoorIndustryAssetsCreator extends AbstractDataCreator implements DataRecreatable {

	@Autowired
	@Qualifier("industryDataSource")
	protected DataSource industryDataSource;

	@Value("classpath:sql/defaultdata/outdoor/tbl_category-defaultData.sql")
	protected Resource outdoorDefaultCategoriesSql;

	@Value("classpath:sql/defaultdata/outdoor/tbl_item-defaultData.sql")
	protected Resource outdoorDefaultItemsSql;

	@Value("classpath:sql/defaultdata/outdoor/tbl_item_inventory-defaultData.sql")
	protected Resource outdoorDefaultItemInventorySql;

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.OUTDOOR));

		IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;
	}

	@Override
	public void populateData() {

		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_CATEGORIES_ITEMS), IndustryType.OUTDOOR));

		// create categories
		dataInitialize(industryDataSource, outdoorDefaultCategoriesSql);

		// create items
		dataInitialize(industryDataSource, outdoorDefaultItemsSql);

		// create item inventory
		dataInitialize(industryDataSource, outdoorDefaultItemInventorySql);

		log.info(messages.getString(DatabaseOperationMessages.WRITE_DONE));
	}

	@Override
	public void recreateData() {
		create();
	}
}
