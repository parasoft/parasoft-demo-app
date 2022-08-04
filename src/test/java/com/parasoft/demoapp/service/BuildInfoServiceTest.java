package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.BuildInfoDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.info.BuildProperties;

import java.time.Instant;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class BuildInfoServiceTest {

    @Mock
    private BuildProperties buildProperties;

    @InjectMocks
    private BuildInfoService underTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetBuildInfo() {
        // Given
        // From META-INF/build-info.properties file.
        String buildVersion = "1.0.0";
        String buildId = "20200731163332";
        Instant buildTime = Instant.parse("2020-07-31T08:33:33.146Z"); // 1596184413146
        // buildTime Epoch timestamp in milliseconds get by access REST API endpoint: http://localhost:8080/v1/build-info
        Long buildTimeEpochMilli = 1596184413146L;

        when(buildProperties.getVersion()).thenReturn(buildVersion);
        when(buildProperties.get("id")).thenReturn(buildId);
        when(buildProperties.getTime()).thenReturn(buildTime);

        // When
        final BuildInfoDTO result = underTest.getBuildInfo();

        // Then
        assertEquals(buildVersion, result.getBuildVersion());
        assertEquals(buildId, result.getBuildId());
        assertEquals(buildTimeEpochMilli, result.getBuildTime());
    }
}
