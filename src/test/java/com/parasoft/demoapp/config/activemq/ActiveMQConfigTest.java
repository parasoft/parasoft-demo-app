package com.parasoft.demoapp.config.activemq;

import junitparams.JUnitParamsRunner;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class ActiveMQConfigTest {
    @Test
    public void testResetInventoryActiveMqQueues() {
        // Given
        ActiveMQQueue requestMqForTest = new ActiveMQQueue("for.test.request");
        ActiveMQQueue responseMqForTest =  new ActiveMQQueue("for.test.response");
        ActiveMQQueue defaultRequestMq = new ActiveMQQueue("queue.inventory.request");
        ActiveMQQueue defaultResponseMq = new ActiveMQQueue("queue.inventory.response");

        ActiveMQConfig.setInventoryRequestActiveMqQueue(requestMqForTest);
        ActiveMQConfig.setInventoryResponseActiveMqQueue(responseMqForTest);

        assertEquals(ActiveMQConfig.getInventoryRequestActiveMqQueue(), requestMqForTest);
        assertEquals(ActiveMQConfig.getInventoryResponseActiveMqQueue(), responseMqForTest);

        // When
        ActiveMQConfig.resetInventoryActiveMqQueues();

        // Then
        assertEquals(ActiveMQConfig.getInventoryRequestActiveMqQueue(), defaultRequestMq);
        assertEquals(ActiveMQConfig.getInventoryResponseActiveMqQueue(), defaultResponseMq);
    }
}
