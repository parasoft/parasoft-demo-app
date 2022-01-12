package com.parasoft.demoapp.defaultdata;

import com.parasoft.demoapp.repository.global.DatabaseInitResultRepository;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ParasoftJDBCProxyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultDataConfig {

    @Bean(initMethod = "init")
    public InitializationEntrance defaultDataInitialization(DatabaseInitResultRepository databaseInitResultRepository,
                                                        List<AbstractTablesCreator> tablesCreators,
                                                        List<AbstractDataCreator> dataCreators,
                                                        GlobalPreferencesService globalPreferencesService,
                                                        ParasoftJDBCProxyService parasoftJDBCProxyService){

        return new InitializationEntrance(databaseInitResultRepository, tablesCreators, dataCreators,
                                            globalPreferencesService, parasoftJDBCProxyService);
    }
}
