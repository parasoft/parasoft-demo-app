package com.parasoft.demoapp.defaultdata;

import com.parasoft.demoapp.config.ImplementedIndustries;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.DatabaseInitResultEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.repository.global.DatabaseInitResultRepository;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * The entrance class of databases reset, not every table, but some necessary table(like industry data).
 */
@Component
@Slf4j
public class ResetEntrance {

    @Autowired
    private DatabaseInitResultRepository databaseInitResultRepository;

    @Autowired
    private List<DataRecreatable> dataRecreatableservice;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    @Qualifier("industryDataSource")
    private DataSource industryDataSource;

    @Value("classpath:sql/tables/industryTablesTruncateSql.sql")
    private Resource industryTablesTruncateSql;

    private DatabaseOperationMessages messages = new DatabaseOperationMessages();

    public void run(){
    	truncateAllIndustriesTables();
        regenerateDefaultData();

        try {
            afterReset();
        } catch (Exception e) {
            // Generally, it can't reach here.
            e.printStackTrace();
        }
    }

    private void regenerateDefaultData(){
        log.info(messages.getString(DatabaseOperationMessages.RECREATE_DEFAULT_DATA));

        for(DataRecreatable service : dataRecreatableservice){
            service.recreateData();
        }

        log.info(messages.getString(DatabaseOperationMessages.DEFAULT_DATA_RECREATED));
    }

    private void afterReset() throws GlobalPreferencesMoreThanOneException, GlobalPreferencesNotFoundException {
        IndustryType currentIndustry = globalPreferencesService.getCurrentIndustry();
        IndustryRoutingDataSource.currentIndustry = currentIndustry;
        DatabaseInitResultEntity result = databaseInitResultRepository.findFirstByOrderByCreatedTimeDesc();
        result.setLatestRecreatedTime(new Date());
        databaseInitResultRepository.save(result);
    }

    private void truncateAllIndustriesTables(){
        log.info(messages.getString(DatabaseOperationMessages.PREPARE_TO_REMOVE_DATA));

        for(IndustryType industry : ImplementedIndustries.get()){
            log.info(MessageFormat.format(
                    messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), industry));

            IndustryRoutingDataSource.currentIndustry = industry;
            log.info(MessageFormat.format(
                    messages.getString(DatabaseOperationMessages.REMOVING_DATA_OF), industry));
            tablesTruncate(industryDataSource, industryTablesTruncateSql);
        }

        log.info(messages.getString(DatabaseOperationMessages.DATA_REMOVED));
    }
    
    private void tablesTruncate(final DataSource dataSource, final Resource script) {
    	DatabaseOperationUtil.executeSqlScript(dataSource, script);
    }

}