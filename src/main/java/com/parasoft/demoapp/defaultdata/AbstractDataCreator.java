package com.parasoft.demoapp.defaultdata;

import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * Use for default data creation, there are two methods to be implemented in sub class.
 * switchIndustry() --> the behavior of switch industry
 * populateData()   --> the behavior of data insertions
 */
public abstract class AbstractDataCreator {

    protected DatabaseOperationMessages messages = new DatabaseOperationMessages();

    // entrance, use Template Pattern
    public void create() {
        switchIndustry();
        populateData();
    }

    protected abstract void switchIndustry();

    protected abstract void populateData();

    protected void dataInitialize(final DataSource dataSource, final Resource script) {
        DatabaseOperationUtil.executeSqlScript(dataSource, script);
    }
}
