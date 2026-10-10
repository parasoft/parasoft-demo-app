package com.parasoft.demoapp.defaultdata.global;

import com.parasoft.demoapp.defaultdata.AbstractTablesCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;

@Component
@Slf4j
@Order(1) // The order of tables creation
public class GlobalTablesCreator extends AbstractTablesCreator {

    @Autowired
    @Qualifier("globalDataSource")
    protected DataSource globalDataSource;

    @Value("classpath:sql/tables/globalInitialTablesSql.sql")
    protected Resource globalInitialTablesSql;

    @Override
    public void switchIndustry() {
        // no need to switch industry
    }

    @Override
    public void populateTables() {
        tablesInitialize(globalDataSource, globalInitialTablesSql);
        upgradeInitialOrderStatusPreference();
    }

    private void upgradeInitialOrderStatusPreference() {
        JdbcTemplate jdbc = new JdbcTemplate(globalDataSource);
        boolean columnExists = Boolean.TRUE.equals(jdbc.execute((ConnectionCallback<Boolean>) connection -> {
            try (ResultSet columns = connection.getMetaData().getColumns(null, null,
                    "TBL_GLOBAL_PREFERENCES", "NEW_ORDERS_INITIALLY_PROCESSED")) {
                return columns.next();
            }
        }));
        if (!columnExists) {
            jdbc.execute("ALTER TABLE TBL_GLOBAL_PREFERENCES ADD COLUMN NEW_ORDERS_INITIALLY_PROCESSED TINYINT DEFAULT 0");
            log.info("Added standalone initial order status preference to existing global settings");
        }

        // Migrate the earlier demo-bug representation before Hibernate loads the now-removed enum value.
        // Keep the copy and removal atomic so restarting cannot restore an option the user later disabled.
        Integer migrated = new TransactionTemplate(new DataSourceTransactionManager(globalDataSource)).execute(status -> {
            jdbc.update("UPDATE TBL_GLOBAL_PREFERENCES SET NEW_ORDERS_INITIALLY_PROCESSED = 1 "
                    + "WHERE ID IN (SELECT GLOBAL_PREFERENCES_ID FROM TBL_DEMO_BUG "
                    + "WHERE DEMO_BUGS_TYPE = 'PROCESS_ORDERS_IMMEDIATELY')");
            return jdbc.update("DELETE FROM TBL_DEMO_BUG WHERE DEMO_BUGS_TYPE = 'PROCESS_ORDERS_IMMEDIATELY'");
        });
        if (migrated != null && migrated > 0) {
            log.info("Migrated {} legacy initial order status preference records", migrated);
        }
    }
}
