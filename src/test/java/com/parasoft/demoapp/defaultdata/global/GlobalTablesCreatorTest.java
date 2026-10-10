package com.parasoft.demoapp.defaultdata.global;

import org.junit.After;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.util.UUID;

import static org.junit.Assert.*;

public class GlobalTablesCreatorTest {
    private final DriverManagerDataSource dataSource = new DriverManagerDataSource(
            "jdbc:hsqldb:mem:" + UUID.randomUUID(), "SA", "");
    private final JdbcTemplate jdbc = new JdbcTemplate(dataSource);

    @After
    public void shutdown() {
        jdbc.execute("SHUTDOWN");
    }

    private void initialize() {
        GlobalTablesCreator creator = new GlobalTablesCreator();
        creator.globalDataSource = dataSource;
        creator.globalInitialTablesSql = new ClassPathResource("sql/tables/globalInitialTablesSql.sql");
        creator.populateTables();
    }

    private void assertColumnExists() throws Exception {
        try (var connection = dataSource.getConnection();
             ResultSet columns = connection.getMetaData().getColumns(null, null,
                     "TBL_GLOBAL_PREFERENCES", "NEW_ORDERS_INITIALLY_PROCESSED")) {
            assertTrue("Standalone initial-status preference column must exist", columns.next());
        }
    }

    @Test
    public void initializesNewDatabaseWithDisabledPreference() throws Exception {
        initialize();
        assertColumnExists();
        jdbc.update("INSERT INTO TBL_GLOBAL_PREFERENCES (ID) VALUES (1)");
        assertFalse(jdbc.queryForObject("SELECT NEW_ORDERS_INITIALLY_PROCESSED FROM TBL_GLOBAL_PREFERENCES", Boolean.class));
    }

    @Test
    public void migratesLegacyPreferenceWithoutRemovingOtherDemoBugs() throws Exception {
        // Existing tables predate the independent initial-status preference.
        jdbc.execute("CREATE TABLE TBL_GLOBAL_PREFERENCES (ID BIGINT PRIMARY KEY)");
        jdbc.update("INSERT INTO TBL_GLOBAL_PREFERENCES (ID) VALUES (1), (2)");
        jdbc.execute("CREATE TABLE TBL_DEMO_BUG (ID BIGINT PRIMARY KEY, DEMO_BUGS_TYPE VARCHAR(255), GLOBAL_PREFERENCES_ID BIGINT)");
        jdbc.update("INSERT INTO TBL_DEMO_BUG VALUES (1, 'PROCESS_ORDERS_IMMEDIATELY', 1), (2, 'REVERSE_ORDER_OF_ORDERS', 1)");

        initialize();
        assertColumnExists();
        assertTrue(jdbc.queryForObject("SELECT NEW_ORDERS_INITIALLY_PROCESSED FROM TBL_GLOBAL_PREFERENCES WHERE ID = 1", Boolean.class));
        assertFalse(jdbc.queryForObject("SELECT NEW_ORDERS_INITIALLY_PROCESSED FROM TBL_GLOBAL_PREFERENCES WHERE ID = 2", Boolean.class));
        assertEquals("REVERSE_ORDER_OF_ORDERS", jdbc.queryForObject("SELECT DEMO_BUGS_TYPE FROM TBL_DEMO_BUG", String.class));

        jdbc.update("UPDATE TBL_GLOBAL_PREFERENCES SET NEW_ORDERS_INITIALLY_PROCESSED = 0 WHERE ID = 1");
        initialize();
        assertFalse(jdbc.queryForObject("SELECT NEW_ORDERS_INITIALLY_PROCESSED FROM TBL_GLOBAL_PREFERENCES WHERE ID = 1", Boolean.class));
        assertEquals(Integer.valueOf(1), jdbc.queryForObject("SELECT COUNT(*) FROM TBL_DEMO_BUG", Integer.class));
    }
}
