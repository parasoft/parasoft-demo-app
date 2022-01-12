package com.parasoft.demoapp.defaultdata.global;

import com.parasoft.demoapp.defaultdata.AbstractTablesCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("creationResultCreator")
public class DatabaseInitResultTableCreator extends AbstractTablesCreator {

    @Autowired
    @Qualifier("globalDataSource")
    protected DataSource globalDataSource;

    @Value("classpath:sql/tables/dbInitResultInitialTableSql.sql")
    protected Resource creationResultInitialTablesSql;

    @Override
    public void switchIndustry() {
        // no need to switch industry
    }

    public void populateTables() {
        tablesInitialize(globalDataSource, creationResultInitialTablesSql);
    }

}
