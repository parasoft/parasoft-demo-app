package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.defaultdata.AbstractTablesCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

public abstract class AbstractIndustryTablesCreator extends AbstractTablesCreator {

    @Autowired
    @Qualifier("industryDataSource")
    protected DataSource industryDataSource;

    @Value("classpath:sql/tables/industryInitialTablesSql.sql")
    protected Resource industryInitalTablesSql;

    @Override
    public void populateTables() {
        tablesInitialize(industryDataSource, industryInitalTablesSql);
    }
}
