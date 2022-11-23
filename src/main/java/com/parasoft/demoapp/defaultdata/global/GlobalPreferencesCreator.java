package com.parasoft.demoapp.defaultdata.global;

import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.RestEndpointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
@Slf4j
@Order(2) // The order of default data creation
public class GlobalPreferencesCreator extends AbstractDataCreator {

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private RestEndpointService restEndpointService;

    @Autowired
    private GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService;

    @Autowired
    @Qualifier("globalDataSource")
    protected DataSource globalDataSource;

    @Override
    public void switchIndustry() {
        // no need to do anything
    }

    @Override
    @Transactional(transactionManager = "globalTransactionManager")
    public void populateData() {
        log.info(messages.getString(DatabaseOperationMessages.WRITE_GLOBAL_PREFERENCES));

        GlobalPreferencesEntity defaultPreferences = defaultGlobalPreferencesSettingsService.defaultPreferences();

        // create current global preferences, it is the same as default when application starts up.
        try {
            defaultPreferences = globalPreferencesService.addNewGlobalPreferences(defaultPreferences.getDataAccessMode(),
                            defaultPreferences.getSoapEndPoint(), defaultPreferences.getRestEndPoints(),
                            defaultPreferences.getIndustryType(), defaultPreferences.getWebServiceMode(),
                            defaultPreferences.getGraphQLEndpoint(), defaultPreferences.getDemoBugs(),
                            defaultPreferences.getAdvertisingEnabled(), defaultPreferences.getUseParasoftJDBCProxy(),
                            defaultPreferences.getParasoftVirtualizeServerUrl(),
                            defaultPreferences.getParasoftVirtualizeServerPath(),
                            defaultPreferences.getParasoftVirtualizeGroupId(),
                            defaultPreferences.getMqProxyEnabled(),
                            defaultPreferences.getMqType(),
                            defaultPreferences.getOrderServiceDestinationQueue(),
                            defaultPreferences.getOrderServiceReplyToQueue());

            for(RestEndpointEntity restEndpointEntity : restEndpointService.getAllEndpoints()){
                restEndpointEntity.setGlobalPreferences(defaultPreferences);
                restEndpointService.updateEndpoint(restEndpointEntity);
            }

        } catch (Exception e) {
            // normally, it can not reach here on production environment
            throw new RuntimeException(e.getMessage(), e);
        }

        log.info(messages.getString(DatabaseOperationMessages.WRITE_DONE));
    }
}
