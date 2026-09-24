package com.parasoft.demoapp.config.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class RabbitMQConfigSpringTest {

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    @Autowired
    Environment environment;

    @Autowired
    MessageConverter messageConverter;

    @Test
    public void testInitRabbitMQConfig() {
        assertEquals(rabbitMQConfig.getRabbitMqHost(), environment.getProperty("spring.rabbitmq.host"));
        assertEquals(rabbitMQConfig.getRabbitMqPort(), Integer.parseInt(environment.getProperty("spring.rabbitmq.port")));
        assertEquals(rabbitMQConfig.getUsername(), environment.getProperty("spring.rabbitmq.username"));
        assertEquals(rabbitMQConfig.getPassword(), environment.getProperty("spring.rabbitmq.password"));
        assertNotNull(rabbitMQConfig.getOrderServiceSendToQueueBinding());
    }

    @Test
    public void messageConverter_deserializesTrustedInventoryMessages() {
        InventoryOperationRequestMessageDTO request = new InventoryOperationRequestMessageDTO(
                InventoryOperation.DECREASE, "23-456-010", "test request");
        Message message = messageConverter.toMessage(request, new MessageProperties());

        Object converted = messageConverter.fromMessage(message);

        assertTrue(converted instanceof InventoryOperationRequestMessageDTO);
        assertEquals(request, converted);
    }
}
