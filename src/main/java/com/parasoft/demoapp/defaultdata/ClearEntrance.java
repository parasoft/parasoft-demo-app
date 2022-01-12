package com.parasoft.demoapp.defaultdata;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.text.MessageFormat;

/**
 * The entrance class of databases clear, not every table, but some necessary table(like industry data).
 */
@Component
@Slf4j
public class ClearEntrance {

    @Autowired
    @Qualifier("industryDataSource")
    private DataSource industryDataSource;

    @Value("classpath:sql/tables/industryTablesTruncateSql.sql")
    private Resource industryTablesTruncateSql;

    private DatabaseOperationMessages messages = new DatabaseOperationMessages();

    public void run(){
        try {
        	truncateCurrentIndustryTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void truncateCurrentIndustryTables() {
		log.info(messages.getString(DatabaseOperationMessages.PREPARE_TO_REMOVE_DATA));
		
        log.info(MessageFormat.format(
                messages.getString(DatabaseOperationMessages.REMOVING_DATA_OF), IndustryRoutingDataSource.currentIndustry));
        
        tablesTruncate(industryDataSource, industryTablesTruncateSql);
        
        log.info(messages.getString(DatabaseOperationMessages.DATA_REMOVED));
	}
	
    private void tablesTruncate(final DataSource dataSource, final Resource script) {
    	DatabaseOperationUtil.executeSqlScript(dataSource, script);
    }
}
