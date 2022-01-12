package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.BuildInfoDTO;
import com.parasoft.demoapp.service.BuildInfoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class BuildInfoControllerTest {

    @Mock
    private BuildInfoService buildInfoService;

    @InjectMocks
    private BuildInfoController underTest;

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
        BuildInfoDTO buildInfo = new BuildInfoDTO();
        when(buildInfoService.getBuildInfo()).thenReturn(buildInfo);

        // When
        ResponseResult<BuildInfoDTO> result = underTest.getBuildInfo();

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }
}
