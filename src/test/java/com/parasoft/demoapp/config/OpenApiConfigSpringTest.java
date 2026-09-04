package com.parasoft.demoapp.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "grpc.server.port=0")
@AutoConfigureMockMvc
@DirtiesContext
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
public class OpenApiConfigSpringTest {

    private static final List<String> OUTDOOR_REGIONS = Arrays.asList("LOCATION_1", "LOCATION_2", "LOCATION_3",
            "LOCATION_4", "LOCATION_5", "LOCATION_6", "LOCATION_7", "LOCATION_8");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void apiDocs_whenOutdoorSkinIsActive_filtersLocationRegionParameter() throws Exception {
        JsonNode schema = requestParameterSchema("/v1/locations/location", "region");

        assertEquals(OUTDOOR_REGIONS, enumValues(schema.get("enum")));
    }

    @Test
    public void apiDocs_whenOutdoorSkinIsActive_filtersItemRegionsParameter() throws Exception {
        JsonNode schema = requestParameterSchema("/v1/assets/items", "regions");

        assertEquals(OUTDOOR_REGIONS, enumValues(schema.get("items").get("enum")));
    }

    private JsonNode requestParameterSchema(String path, String parameterName) throws Exception {
        String apiDocs = mockMvc.perform(get("/api-docs/v1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode parameters = objectMapper.readTree(apiDocs)
                .path("paths")
                .path(path)
                .path("get")
                .path("parameters");
        for (JsonNode parameter : parameters) {
            if (parameterName.equals(parameter.path("name").asText())) {
                return parameter.path("schema");
            }
        }

        fail("OpenAPI parameter was not found: " + path + " " + parameterName);
        return null;
    }

    private List<String> enumValues(JsonNode enumValues) {
        List<String> values = new ArrayList<>();
        enumValues.forEach(value -> values.add(value.asText()));
        return values;
    }
}
