package com.parasoft.demoapp.defaultdata;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

public class DatabaseOperationUtil {

	public static void executeSqlScript(final DataSource dataSource, final Resource script) {
		final DataSourceInitializer initializer = new DataSourceInitializer();
		
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator(script));
		initializer.afterPropertiesSet();
	}
	
    private static DatabasePopulator databasePopulator(final Resource script) {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        populator.addScripts(script);
        populator.setSeparator("$$");
        populator.setSqlScriptEncoding("UTF-8");
        return populator;
    }
    
}
