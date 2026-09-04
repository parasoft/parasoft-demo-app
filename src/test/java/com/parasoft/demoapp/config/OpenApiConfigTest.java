package com.parasoft.demoapp.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springdoc.core.SpringDocConfigProperties;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.GlobalPreferencesService;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public class OpenApiConfigTest {

    private GlobalPreferencesService globalPreferencesService;
    private OpenApiConfig.SchemaPropertyCustomizer underTest;

    @Before
    public void setUp() {
        globalPreferencesService = mock(GlobalPreferencesService.class);
        underTest = new OpenApiConfig.SchemaPropertyCustomizer(globalPreferencesService,
                new SpringDocConfigProperties());
    }

    @Test
    public void customize_whenOutdoorSkinIsActive_excludesDefenseLocations() throws Exception {
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.OUTDOOR);

        Schema<String> schema = new StringSchema();
        AnnotatedType regionType = new AnnotatedType()
                .type(TypeFactory.defaultInstance().constructType(RegionType.class));

        underTest.customize(schema, regionType);

        assertEquals(Arrays.asList("LOCATION_1", "LOCATION_2", "LOCATION_3", "LOCATION_4",
                "LOCATION_5", "LOCATION_6", "LOCATION_7", "LOCATION_8"), schema.getEnum());
        assertFalse(schema.getEnum().contains("UNITED_STATES"));
    }
}
