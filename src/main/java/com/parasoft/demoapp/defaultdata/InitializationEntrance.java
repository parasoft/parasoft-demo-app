package com.parasoft.demoapp.defaultdata;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.exception.VirtualizeServerUrlException;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.DatabaseInitResultEntity;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.repository.global.DatabaseInitResultRepository;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ParasoftJDBCProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;

/**
 * the entrance class of databases initialization, include create tables and insert default data.
 */
@Slf4j
public class InitializationEntrance {

    private DatabaseInitResultRepository databaseInitResultRepository;

    private List<AbstractTablesCreator> tablesCreators;

    private List<AbstractDataCreator> dataCreators;

    private GlobalPreferencesService globalPreferencesService;

    private ParasoftJDBCProxyService parasoftJDBCProxyService;

    private DatabaseOperationMessages databaseOperationMessages = new DatabaseOperationMessages();

    public InitializationEntrance(DatabaseInitResultRepository databaseInitResultRepository,
                                  List<AbstractTablesCreator> tablesCreators,
                                  List<AbstractDataCreator> dataCreators,
                                  GlobalPreferencesService globalPreferencesService,
                                  ParasoftJDBCProxyService parasoftJDBCProxyService) {

        this.databaseInitResultRepository = databaseInitResultRepository;
        this.tablesCreators = tablesCreators;
        this.dataCreators = dataCreators;
        this.globalPreferencesService = globalPreferencesService;
        this.parasoftJDBCProxyService = parasoftJDBCProxyService;
    }

    /**
     * the entrance method of databases initialization, include create tables and insert default data.
     * @throws Exception
     */
    public void init() throws Exception {

        log.info(databaseOperationMessages.getString(DatabaseOperationMessages.INITIALIZE_DATABASES));

        // create tables first
        for(AbstractTablesCreator tablesCreator : tablesCreators){
            tablesCreator.create();
        }

        DatabaseInitResultEntity result = databaseInitResultRepository.findFirstByOrderByCreatedTimeDesc();
        if(result == null){

            result = databaseInitResultRepository.save(
                    new DatabaseInitResultEntity(false, new Date(), null));

            // create default data
            for(AbstractDataCreator dataCreator : dataCreators){
                dataCreator.create();
            }

            result.setCreated(true);
            databaseInitResultRepository.save(result);

            log.info(databaseOperationMessages.getString(DatabaseOperationMessages.DATABASES_INITIALIZED_COMPLETELY));

        } else if(!result.isCreated()) {

            throw new RuntimeException(databaseOperationMessages.getString(DatabaseOperationMessages.DATABASES_NOT_CLEAR));

        } else{

            log.info(databaseOperationMessages.getString(DatabaseOperationMessages.DATABASES_INITIALIZED_ALREADY));
        }


        GlobalPreferencesEntity globalPreferences = globalPreferencesService.getCurrentGlobalPreferences();
        IndustryRoutingDataSource.currentIndustry = globalPreferences.getIndustryType();


        Boolean useParasoftJDBCProxy = globalPreferences.getUseParasoftJDBCProxy();
        String parasoftVirtualizeServerUrl = globalPreferences.getParasoftVirtualizeServerUrl();
        boolean validateResult = false;
        // handle parasoft JDBC proxy if it is needed
        if(useParasoftJDBCProxy){
            if(StringUtils.isBlank(parasoftVirtualizeServerUrl)){
                parasoftVirtualizeServerUrl = PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;
            }

            try{
                parasoftJDBCProxyService.validateVirtualizeServerUrl(parasoftVirtualizeServerUrl);
                validateResult = true;
            }catch (VirtualizeServerUrlException e){
                e.printStackTrace();
                validateResult = false;
            }
        }

        IndustryRoutingDataSource.useParasoftJDBCProxy = useParasoftJDBCProxy;
        IndustryRoutingDataSource.parasoftVirtualizeServerUrl = parasoftVirtualizeServerUrl;
        IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected = validateResult;


        parasoftJDBCProxyService.refreshParasoftJDBCProxyDataSource();


        log.info(MessageFormat.format(databaseOperationMessages.getString(DatabaseOperationMessages.CURRENT_INDUSTRY),
                globalPreferences.getIndustryType()));
    }

}