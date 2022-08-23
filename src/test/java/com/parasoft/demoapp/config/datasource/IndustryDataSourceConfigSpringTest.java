package com.parasoft.demoapp.config.datasource;

import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.CannotCreateTransactionException;

import javax.persistence.EntityManagerFactory;
import java.text.MessageFormat;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * test for IndustryDataSourceConfig
 *
 * @see IndustryDataSourceConfig
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class IndustryDataSourceConfigSpringTest {
    @Autowired
    @Qualifier("industryDataSource")
    IndustryRoutingDataSource industryDataSource;

    @Autowired
    ItemService itemService;

    /**
     * Test for industryTransactionManager(EntityManagerFactory)
     *
     * @see IndustryDataSourceConfig#industryTransactionManager(EntityManagerFactory)
     */
    @Test
    public void testIndustryTransactionManager_exceptionWithCustomizedMessage() throws Throwable {
        industryDataSource.setTargetDataSources(new HashMap<>()); // Override existing datasource to empty datasource
        IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;
        industryDataSource.afterPropertiesSet();

        // When
        boolean hasException = false;
        try {
            // Any access to database will get an exception since there is no datasource configuration in application
            itemService.getItemById(1L);
        } catch (CannotCreateTransactionException e) {
            hasException = true;
            // Then
            // The error message is overwrote by customized exception
            assertTrue(e.getMessage().startsWith(MessageFormat.format(ConfigMessages.CANNOT_DETERMINE_DATASOURCE, IndustryRoutingDataSource.currentIndustry.getValue())));
        } finally {
            // Then
            assertTrue(hasException);
        }
    }
}