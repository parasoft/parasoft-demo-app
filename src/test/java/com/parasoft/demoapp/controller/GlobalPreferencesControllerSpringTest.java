package com.parasoft.demoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.dto.MQPropertiesResponseDTO;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
public class GlobalPreferencesControllerSpringTest {
    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
}
