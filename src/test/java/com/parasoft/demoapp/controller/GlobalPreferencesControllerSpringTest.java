package com.parasoft.demoapp.controller;

import tools.jackson.databind.ObjectMapper;
import com.parasoft.demoapp.grpc.GRPCConfig;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQConfig;
import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.dto.GRPCPropertiesResponseDTO;
import com.parasoft.demoapp.dto.MQPropertiesResponseDTO;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    @Autowired
    GRPCConfig gRPConfig;

    @Test
    @Transactional(value = "globalTransactionManager")
    public void testNewOrdersInitiallyProcessed_responseCompatibility() throws Exception {
        mockMvc.perform(get("/v1/demoAdmin/currentPreferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.newOrdersInitiallyProcessed").doesNotExist());
        mockMvc.perform(get("/v1/demoAdmin/defaultPreferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.newOrdersInitiallyProcessed").doesNotExist());

        String preferences = "{\"industryType\":\"OUTDOOR\",\"webServiceMode\":\"REST_API\","
                + "\"advertisingEnabled\":true,\"mqType\":\"ACTIVE_MQ\","
                + "\"orderServiceSendTo\":\"inventory.request\",\"orderServiceListenOn\":\"inventory.response\"";
        // MockMvc shares this transaction; rollback preserves the original settings for later test classes.
        mockMvc.perform(put("/v1/demoAdmin/preferences")
                        .with(httpBasic(GlobalUsersCreator.USERNAME_PURCHASER, GlobalUsersCreator.PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(preferences + ",\"newOrdersInitiallyProcessed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.newOrdersInitiallyProcessed").value(true));
        mockMvc.perform(get("/v1/demoAdmin/currentPreferences"))
                .andExpect(jsonPath("$.data.newOrdersInitiallyProcessed").value(true));

        // An older client can omit the new field and still receive the previous response shape.
        MvcResult disabled = mockMvc.perform(put("/v1/demoAdmin/preferences")
                        .with(httpBasic(GlobalUsersCreator.USERNAME_PURCHASER, GlobalUsersCreator.PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON).content(preferences + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.newOrdersInitiallyProcessed").doesNotExist())
                .andReturn();
        assertFalse(globalPreferencesService.getCurrentGlobalPreferences().getNewOrdersInitiallyProcessed());
        var response = objectMapper.readTree(disabled.getResponse().getContentAsString()).get("data");
        assertTrue(response.has("advertisingEnabled"));
        assertTrue(response.has("demoBugs"));
        assertTrue(response.has("useParasoftJDBCProxy"));
    }

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

    @Test
    public void test_getGRPCProperties_normal() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/gRPCProperties";
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

        GRPCPropertiesResponseDTO gRPCPropertiesResponse =
                objectMapper.convertValue(result.getData(), GRPCPropertiesResponseDTO.class);
        GRPCPropertiesResponseDTO gRPCProperties = new GRPCPropertiesResponseDTO(gRPConfig.getPort());

        assertNotNull(gRPCPropertiesResponse);
        assertEquals(gRPCProperties.getPort(),gRPCPropertiesResponse.getPort());
    }

    @Test
    public void test_getGRPCProperties_incorrectAuthentication() throws Exception {
        assertNotNull(mockMvc);

        String baseUrl = "/v1/demoAdmin/gRPCProperties";
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
