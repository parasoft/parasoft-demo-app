package com.parasoft.demoapp.defaultdata.global;

import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.model.global.preferences.MqType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.service.GlobalPreferencesService;

import static org.junit.Assert.*;

/**
 * test class GlobalPreferencesCreator
 *
 * @see GlobalPreferencesCreator
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class GlobalPreferencesCreatorSpringTest {

    @Autowired
    GlobalPreferencesService globalPreferencesService;

    /**
     * test if global preferences are successfully created
     */
    @Test
    public void testGlobalPreferencesCreateSuccessfully() throws Throwable {
        // When

        //Check current global preferences
        GlobalPreferencesEntity currentGlobalPreferences = globalPreferencesService.getCurrentGlobalPreferences();
        assertNotNull(currentGlobalPreferences);
        assertNotNull(currentGlobalPreferences.getId());
        assertNull(currentGlobalPreferences.getDataAccessMode());
        assertNull(currentGlobalPreferences.getSoapEndPoint());
        assertNotNull(currentGlobalPreferences.getRestEndPoints());
        assertEquals(5, currentGlobalPreferences.getRestEndPoints().size());
        assertEquals(IndustryType.OUTDOOR, currentGlobalPreferences.getIndustryType());
        assertEquals(2, currentGlobalPreferences.getDemoBugs().size());
        assertTrue(currentGlobalPreferences.getAdvertisingEnabled());
        assertEquals(MqType.ACTIVE_MQ, currentGlobalPreferences.getMqType());
        assertEquals(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST, currentGlobalPreferences.getOrderServiceDestinationQueue());
        assertEquals(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE, currentGlobalPreferences.getOrderServiceReplyToQueue());
        assertEquals(KafkaConfig.DEFAULT_ORDER_SERVICE_REQUEST_TOPIC, currentGlobalPreferences.getOrderServiceRequestTopic());
        assertEquals(KafkaConfig.DEFAULT_ORDER_SERVICE_RESPONSE_TOPIC, currentGlobalPreferences.getOrderServiceReplyToQueue());
    }

}
