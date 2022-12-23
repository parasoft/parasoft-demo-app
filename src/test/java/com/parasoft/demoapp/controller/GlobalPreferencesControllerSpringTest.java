package com.parasoft.demoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQConfig;
import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.dto.MQPropertiesResponseDTO;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * test class GlobalPreferencesController
 *
 * @see GlobalPreferencesController
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
public class GlobalPreferencesControllerSpringTest {
    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private KafkaConfig kafkaConfig;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    @Test
    public void test_getMQProperties_normal() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/mqProperties";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl))
                        .andExpect(status().isOk())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());

        MQPropertiesResponseDTO mqPropertiesResponse =
                objectMapper.convertValue(result.getData(), MQPropertiesResponseDTO.class);
        MQPropertiesResponseDTO mqProperties = globalPreferencesService.getMQProperties();

        assertNotNull(mqPropertiesResponse);
        assertEquals(mqProperties.getKafkaConfig(), mqPropertiesResponse.getKafkaConfig());
        assertEquals(mqProperties.getActiveMqConfig(), mqPropertiesResponse.getActiveMqConfig());
    }

    @Test
    public void test_getMQProperties_incorrectAuthentication() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/mqProperties";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl).with(httpBasic(GlobalUsersCreator.USERNAME_PURCHASER,"invalidPass")))
                        .andExpect(status().isUnauthorized())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
        assertEquals(ConfigMessages.USER_IS_NOT_AUTHORIZED, result.getMessage());
        assertEquals(result.getData(), "Bad credentials");
    }


    /**
     * Test for validateKafkaBrokerUrl()
     * <br/>
     * This test needs Kafka server enabled.
     *
     * @see com.parasoft.demoapp.controller.GlobalPreferencesController#validateKafkaBrokerUrl()
     */
    // @Test
    public void test_validateKafkaBrokerUrl_normal() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/kafkaBrokerUrlValidation";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl))
                        .andExpect(status().isOk())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    @Test
    public void test_validateKafkaBrokerUrl_kafkaIsNotAvailable() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/kafkaBrokerUrlValidation";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl))
                        .andExpect(status().isInternalServerError())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
        assertEquals(MessageFormat.format(GlobalPreferencesMessages.KAFKA_SERVER_IS_NOT_AVAILABLE, kafkaConfig.getBootstrapServers()), result.getMessage());
    }

    @Test
    public void test_validateKafkaBrokerUrl_incorrectAuthentication() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/kafkaBrokerUrlValidation";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl).with(httpBasic(GlobalUsersCreator.USERNAME_PURCHASER,"invalidPass")))
                        .andExpect(status().isUnauthorized())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
        assertEquals(ConfigMessages.USER_IS_NOT_AUTHORIZED, result.getMessage());
        assertEquals(result.getData(), "Bad credentials");
    }

    /**
     * Test for validateRabbitMQServerUrl()
     * <br/>
     * This test needs RabbitMQ server enabled.
     *
     * @see com.parasoft.demoapp.controller.GlobalPreferencesController#validateRabbitMQServerUrl()
     */
    //@Test
    public void test_validateRabbitMQServerUrl_normal() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/rabbitMQUrlValidation";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl))
                        .andExpect(status().isOk())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    @Test
    public void test_validateRabbitMQServerUrl_rabbitMQIsNotAvailable() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/rabbitMQUrlValidation";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl))
                        .andExpect(status().isInternalServerError())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
        assertEquals(MessageFormat.format(GlobalPreferencesMessages.RABBITMQ_SERVER_IS_NOT_AVAILABLE,
                rabbitMQConfig.getRabbitMqHost() + ":" + rabbitMQConfig.getRabbitMqPort()), result.getMessage());
    }

    @Test
    public void test_validateRabbitMQServerUrl_incorrectAuthentication() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/rabbitMQUrlValidation";
        MvcResult mvcResult =
                mockMvc.perform(get(baseUrl).with(httpBasic(GlobalUsersCreator.USERNAME_PURCHASER,"invalidPass")))
                        .andExpect(status().isUnauthorized())
                        .andReturn();
        MockHttpServletResponse response  = mvcResult.getResponse();
        ResponseResult result =
                objectMapper.readValue(response.getContentAsString(), ResponseResult.class);

        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
        assertEquals(ConfigMessages.USER_IS_NOT_AUTHORIZED, result.getMessage());
        assertEquals(result.getData(), "Bad credentials");
    }
}
