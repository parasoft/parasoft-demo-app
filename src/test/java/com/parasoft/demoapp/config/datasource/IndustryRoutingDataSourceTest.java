package com.parasoft.demoapp.config.datasource;

import com.parasoft.demoapp.exception.CannotDetermineTargetDataSourceException;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndustryRoutingDataSourceTest {

    /**
     * Test for determineTargetDataSource()
     *
     * @see IndustryRoutingDataSource#determineTargetDataSource()
     */
    @Test
    public void testDetermineTargetDataSource_exception_IllegalStateException() throws Exception {
        // Given
        IndustryRoutingDataSource underTest = new IndustryRoutingDataSource();
        underTest.setTargetDataSources(new HashMap<>()); // No datasource
        IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;
        underTest.afterPropertiesSet();

        // When
        boolean hasException = false;
        try {
            underTest.determineTargetDataSource();
        } catch (CannotDetermineTargetDataSourceException e) {
            hasException = true;
            // Then
            // The error message is overwrote by customized exception
            assertEquals(MessageFormat.format(ConfigMessages.CANNOT_DETERMINE_DATASOURCE, IndustryRoutingDataSource.currentIndustry.getValue()), e.getMessage());
        } finally {
            // Then
            assertTrue(hasException);
        }
    }
}