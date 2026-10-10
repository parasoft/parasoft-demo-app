package com.parasoft.demoapp.config;

import org.junit.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebConfigTest {

    @Test
    public void jsonMapperBuilderCustomizer_serializesUtcDateWithNumericOffset() {
        JsonMapper.Builder builder = JsonMapper.builder();
        new WebConfig().jsonMapperBuilderCustomizer().customize(builder);

        String serializedDate = builder.build()
                .writeValueAsString(Date.from(Instant.parse("2026-10-10T06:18:13.430Z")));

        assertEquals("\"2026-10-10T06:18:13.430+00:00\"", serializedDate);
    }
}
