package com.parasoft.demoapp.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springdoc.core.SpringDocConfigProperties;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.GlobalPreferencesService;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

@RunWith(Parameterized.class)
public class OpenApiConfigTest {

    private final IndustryType activeIndustry;
    private final Collection<String> expectedRegions;
    private final String unsupportedRegion;
    private GlobalPreferencesService globalPreferencesService;
    private OpenApiConfig.SchemaPropertyCustomizer underTest;

    public OpenApiConfigTest(IndustryType activeIndustry, Collection<String> expectedRegions, String unsupportedRegion) {
        this.activeIndustry = activeIndustry;
        this.expectedRegions = expectedRegions;
        this.unsupportedRegion = unsupportedRegion;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> skinLocations() {
        return Arrays.asList(new Object[][] {
                { IndustryType.OUTDOOR, Arrays.asList("LOCATION_1", "LOCATION_2", "LOCATION_3", "LOCATION_4",
                        "LOCATION_5", "LOCATION_6", "LOCATION_7", "LOCATION_8"), "UNITED_STATES" },
                { IndustryType.DEFENSE, Arrays.asList("UNITED_STATES", "UNITED_KINGDOM", "GERMANY", "FRANCE",
                        "JAPAN", "SOUTH_KOREA", "SPAIN", "AUSTRALIA"), "LOCATION_1" },
                { IndustryType.AEROSPACE, Arrays.asList("MERCURY", "VENUS", "EARTH", "MARS", "JUPITER", "SATURN",
                        "URANUS", "NEPTUNE"), "UNITED_STATES" }
        });
    }

    @Before
    public void setUp() {
        globalPreferencesService = mock(GlobalPreferencesService.class);
        underTest = new OpenApiConfig.SchemaPropertyCustomizer(globalPreferencesService,
                new SpringDocConfigProperties());
    }

    @Test
    public void customize_whenSkinIsActive_returnsOnlySupportedLocations() throws Exception {
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(activeIndustry);

        Schema<String> schema = new StringSchema();
        AnnotatedType regionType = new AnnotatedType()
                .type(TypeFactory.defaultInstance().constructType(RegionType.class));

        underTest.customize(schema, regionType);

        assertEquals(expectedRegions, schema.getEnum());
        assertFalse(schema.getEnum().contains(unsupportedRegion));
    }
}
