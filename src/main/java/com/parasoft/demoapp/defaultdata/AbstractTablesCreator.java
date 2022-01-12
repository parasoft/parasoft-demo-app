package com.parasoft.demoapp.defaultdata;

import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * Use for table creation, there are two methods to be implemented in sub class.
 * switchIndustry() --> the behavior of switch industry
 * populateTable() --> the behavior of table initialization
 */
public abstract class AbstractTablesCreator {

    protected DatabaseOperationMessages messages = new DatabaseOperationMessages();

    // entrance, use Template Pattern
    public void create() {
        switchIndustry();
        populateTables();
    }

    protected void tablesInitialize(final DataSource dataSource, final Resource script) {
        DatabaseOperationUtil.executeSqlScript(dataSource, script);
    }

    protected abstract void switchIndustry();

    protected abstract void populateTables();

}
