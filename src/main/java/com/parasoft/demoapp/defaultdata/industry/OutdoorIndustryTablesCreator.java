package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4) // The order of tables creation
public class OutdoorIndustryTablesCreator extends AbstractIndustryTablesCreator {

    @Override
    public void switchIndustry() {
        IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;
    }

}
