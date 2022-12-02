package com.parasoft.demoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parasoft.demoapp.dto.MQPropertiesResponseDTO;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * test class GlobalPreferencesController
 *
 * @see GlobalPreferencesController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class GlobalPreferencesControllerSpringTest {
    @Autowired
    GlobalPreferencesService globalPreferencesService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetMQProperties() {
        String baseURL = "/v1/demoAdmin/mqProperties";

        ResponseResult<MQPropertiesResponseDTO> result =
                restTemplate.getForObject(baseURL, ResponseResult.class);
        MQPropertiesResponseDTO mqPropertiesResponse =
                objectMapper.convertValue(result.getData(), MQPropertiesResponseDTO.class);
        MQPropertiesResponseDTO mqProperties = globalPreferencesService.getMQProperties();

        assertNotNull(mqPropertiesResponse);
        assertEquals(mqPropertiesResponse.getActiveMqConfig(), mqProperties.getActiveMqConfig());
        assertEquals(mqPropertiesResponse.getKafkaConfig(), mqProperties.getKafkaConfig());
    }
}
