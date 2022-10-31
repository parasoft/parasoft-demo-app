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
        ActiveMQQueue requestMqForTest = new ActiveMQQueue("queue.inventory.request");
        ActiveMQQueue responseMqForTest = new ActiveMQQueue("queue.inventory.response");

        ActiveMQConfig.resetInventoryActiveMqQueues();

        assertEquals(ActiveMQConfig.getInventoryRequestActiveMqQueue(), requestMqForTest);
        assertEquals(ActiveMQConfig.getInventoryResponseActiveMqQueue(), responseMqForTest);
    }
}
