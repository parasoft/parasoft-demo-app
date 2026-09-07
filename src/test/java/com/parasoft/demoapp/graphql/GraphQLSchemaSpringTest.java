package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import graphql.language.EnumTypeDefinition;
import graphql.schema.idl.SchemaParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "grpc.server.port=0")
@AutoConfigureMockMvc
@DirtiesContext
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
public class GraphQLSchemaSpringTest {

    private static final List<String> OUTDOOR_REGIONS = Arrays.asList("LOCATION_1", "LOCATION_2", "LOCATION_3",
            "LOCATION_4", "LOCATION_5", "LOCATION_6", "LOCATION_7", "LOCATION_8");
    private static final List<String> DEFENSE_REGIONS = Arrays.asList("UNITED_STATES", "UNITED_KINGDOM", "GERMANY",
            "FRANCE", "JAPAN", "SOUTH_KOREA", "SPAIN", "AUSTRALIA");
    private static final List<String> AEROSPACE_REGIONS = Arrays.asList("MERCURY", "VENUS", "EARTH", "MARS",
            "JUPITER", "SATURN", "URANUS", "NEPTUNE");
    private static final String REGION_TYPE_INTROSPECTION = "{ __type(name: \"RegionType\") { enumValues { name } } }";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GraphQLProvider graphQLProvider;

    @Test
    public void schemaGraphqls_whenOutdoorSkinIsActive_containsOnlyOutdoorRegions() throws Exception {
        assertRegionTypeDefinition(IndustryType.OUTDOOR, OUTDOOR_REGIONS);
    }

    @Test
    public void schemaGraphqls_whenDefenseSkinIsActive_containsOnlyDefenseRegions() throws Exception {
        assertRegionTypeDefinition(IndustryType.DEFENSE, DEFENSE_REGIONS);
    }

    @Test
    public void schemaGraphqls_whenAerospaceSkinIsActive_containsOnlyAerospaceRegions() throws Exception {
        assertRegionTypeDefinition(IndustryType.AEROSPACE, AEROSPACE_REGIONS);
    }

    @Test
    public void graphql_whenOutdoorSkinIsActive_rejectsDefenseRegion() throws Exception {
        graphQLProvider.onIndustryChange(IndustryType.OUTDOOR);

        JsonNode response = executeGraphQL("{ getLocation(region: UNITED_STATES) { id } }");

        assertEquals(1, response.path("errors").size());
        assertTrue(response.path("errors").get(0).path("message").asText().contains("Validation error"));
    }

    private void assertRegionTypeDefinition(IndustryType industryType, List<String> expectedRegions) throws Exception {
        graphQLProvider.onIndustryChange(industryType);

        assertEquals(expectedRegions, regionTypeValuesFromSchema());
        assertEquals(expectedRegions, regionTypeValuesFromIntrospection());
    }

    private List<String> regionTypeValuesFromSchema() throws Exception {
        String schema = mockMvc.perform(get("/schema.graphqls"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        EnumTypeDefinition regionTypeDefinition = new SchemaParser().parse(schema)
                .getType("RegionType", EnumTypeDefinition.class)
                .orElseThrow(() -> new AssertionError("/schema.graphqls does not define RegionType."));
        return regionTypeDefinition.getEnumValueDefinitions().stream()
                .map(value -> value.getName())
                .collect(Collectors.toList());
    }

    private List<String> regionTypeValuesFromIntrospection() throws Exception {
        JsonNode enumValues = executeGraphQL(REGION_TYPE_INTROSPECTION)
                .path("data")
                .path("__type")
                .path("enumValues");

        List<String> values = new ArrayList<>();
        enumValues.forEach(value -> values.add(value.path("name").asText()));
        return values;
    }

    private JsonNode executeGraphQL(String query) throws Exception {
        MvcResult asyncResult = mockMvc.perform(get("/graphql")
                        .queryParam("query", query))
                .andExpect(request().asyncStarted())
                .andReturn();
        String response = mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }
}
