package com.parasoft.demoapp.defaultdata.global;

import com.parasoft.demoapp.defaultdata.AbstractTablesCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Order(1) // The order of tables creation
public class GlobalTablesCreator extends AbstractTablesCreator {

    @Autowired
    @Qualifier("globalDataSource")
    protected DataSource globalDataSource;

    @Value("classpath:sql/tables/globalInitialTablesSql.sql")
    protected Resource globalInitialTablesSql;

    @Override
    public void switchIndustry() {
        // no need to switch industry
    }

    @Override
    public void populateTables() {
        tablesInitialize(globalDataSource, globalInitialTablesSql);
    }
}
