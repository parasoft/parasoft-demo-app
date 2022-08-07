package com.parasoft.demoapp.config;

import com.parasoft.demoapp.config.datasource.DataSourceConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.hsqldb.server.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@Configuration
public class HyperSqlDbServerConfig implements SmartLifecycle {
    private final HsqlProperties properties;
    private Server server;
    private boolean running = false;

    @Autowired
    public HyperSqlDbServerConfig(DataSourceConfigurationProperties.Global global,
                                  DataSourceConfigurationProperties.Industry industry) {
        Properties properties = new Properties();
        properties.setProperty("server.remote_open", "true");

        // Parse the "global" database configuration and configure properties
        properties.setProperty("server.dbname.0", global.getConfiguration().getDatabaseName());
        properties.setProperty("server.database.0", global.getConfiguration().getDatabasePath());

        // Parse the "industry" database configuration and configure properties
        int index = 1;
        for(DataSourceConfigurationProperties.DataSourceProperties configuration : industry.getConfigurations().values()) {
            properties.setProperty("server.dbname." + index, configuration.getDatabaseName());
            properties.setProperty("server.database." + index, configuration.getDatabasePath());
            index++;
        }

        this.properties = new HsqlProperties(properties);
    }

    @Override
    public boolean isRunning() {
        if(server != null)
            server.checkRunning(running);
        return running;
    }

    @Override
    public void start() {
        if(server == null) {
            log.info("Starting HSQL server...");
            ServerConfiguration.translateDefaultDatabaseProperty(properties);
            server = new Server();
            try {
                server.setRestartOnShutdown(false);
                server.setNoSystemExit(true);
                server.setProperties(properties);
                server.start();
                running = true;
                log.info("HSQL Server listening on " + server.getPort());
            } catch(AclFormatException | IOException e) {
                log.error("Error starting HSQL server.", e);
            }
        }
    }

    @Override
    public void stop() {
        log.info("Stopping HSQL server...");
        if(server != null) {
            server.stop();
            running = false;
        }
    }

    @Override
    public int getPhase() {
        return 0;
    }
}