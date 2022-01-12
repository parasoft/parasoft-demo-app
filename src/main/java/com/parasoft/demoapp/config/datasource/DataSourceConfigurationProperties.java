package com.parasoft.demoapp.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DataSourceConfigurationProperties {

    @Data
    @ConfigurationProperties(prefix = "global.datasource")
    public static class Global {
        private DataSourceProperties configuration;

        public DataSource createTargetDataSources() {
            return configuration.createDataSource();
        }
    }

    @Data
    @ConfigurationProperties(prefix = "industry.datasource")
    public static class Industry {
        private HashMap<String, DataSourceProperties> configurations = new HashMap<>(); // parasoft-suppress UC.AURCO "expected"

        public Map<Object, Object> createTargetDataSources() {
            Map<Object, Object> result = new HashMap<>();
            configurations.forEach((key, value) ->  result.put(key, value.createDataSource()));
            return result;
        }
    }

    @Data
    public static class DataSourceProperties {
        protected String url;
        protected String username;
        protected String password;
        protected String driverClassName;

        public DataSource createDataSource() {
            @SuppressWarnings("unchecked")
			DataSourceBuilder<HikariDataSource> builder = (DataSourceBuilder<HikariDataSource>) DataSourceBuilder.create();
            builder.url(url);
            builder.driverClassName(driverClassName);
            builder.username(username);
            builder.password(password);
            return builder.build();
        }
    }
}
