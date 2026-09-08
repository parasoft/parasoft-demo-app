package com.parasoft.demoapp.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.parasoft.demoapp.controller.ItemController;
import com.parasoft.demoapp.controller.LocationController;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.GlobalPreferencesService;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

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

    @Test
    public void customizeParameter_whenLocationRegionParameterIsUsed_returnsOnlySupportedLocations() throws Exception {
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(activeIndustry);
        Parameter parameter = new Parameter().schema(new StringSchema());
        MethodParameter methodParameter = new MethodParameter(
                LocationController.class.getMethod("getLocation", RegionType.class), 0);

        parameterCustomizer().customize(parameter, methodParameter);

        assertEquals(expectedRegions, parameter.getSchema().getEnum());
        assertFalse(parameter.getSchema().getEnum().contains(unsupportedRegion));
    }

    @Test
    public void customizeParameter_whenItemRegionsParameterIsUsed_returnsOnlySupportedLocations() throws Exception {
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(activeIndustry);
        Parameter parameter = new Parameter().schema(new ArraySchema().items(new StringSchema()));
        MethodParameter methodParameter = new MethodParameter(ItemController.class.getMethod("getItems", Long.class,
                RegionType[].class, String.class, Pageable.class), 1);

        parameterCustomizer().customize(parameter, methodParameter);

        assertEquals(expectedRegions, parameter.getSchema().getItems().getEnum());
        assertFalse(parameter.getSchema().getItems().getEnum().contains(unsupportedRegion));
    }

    private ParameterCustomizer parameterCustomizer() {
        assertTrue("The schema customizer must also customize endpoint parameters",
                ParameterCustomizer.class.isAssignableFrom(underTest.getClass()));
        return (ParameterCustomizer) underTest;
    }
}
