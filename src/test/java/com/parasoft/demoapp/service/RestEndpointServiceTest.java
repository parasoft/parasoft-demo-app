/**
 *
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.repository.global.RestEndpointRepository;

/**
 * Test class for RestEndpointService
 *
 * @see com.parasoft.demoapp.service.RestEndpointService
 */
public class RestEndpointServiceTest {

    // Object under test
    @InjectMocks
    RestEndpointService underTest;

    @Mock
    RestEndpointRepository restEndPointRepository;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for updateEndpoint(RestEndpointEntity)
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#updateEndpoint(RestEndpointEntity)
     */
    @Test
    public void testUpdateEndpoint_normal() throws Throwable {
        // Given
        boolean existsByIdResult = true;
        when(restEndPointRepository.existsById(nullable(Long.class))).thenReturn(existsByIdResult);

        RestEndpointEntity saveResult = null;
        doReturn(saveResult).when(restEndPointRepository).save((RestEndpointEntity) any());

        // When
        RestEndpointEntity restEndpointEntity = new RestEndpointEntity();
        restEndpointEntity.setId(1L);
        RestEndpointEntity result = underTest.updateEndpoint(restEndpointEntity);

        // Then
        assertEquals(saveResult, result);
    }

    /**
     * Test for updateEndpoint(RestEndpointEntity)
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#updateEndpoint(RestEndpointEntity)
     */
    @Test
    public void testUpdateEndpoint_nullParameter() throws Throwable {

        // When
        RestEndpointEntity restEndpointEntity = null; // test point
        RestEndpointEntity result = underTest.updateEndpoint(restEndpointEntity);

        // Then
        assertNull(result);
    }

    /**
     * Test for updateEndpoint(RestEndpointEntity)
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#updateEndpoint(RestEndpointEntity)
     */
    @Test
    public void testUpdateEndpoint_nullId() throws Throwable {

        // When
        RestEndpointEntity restEndpointEntity = new RestEndpointEntity();
        restEndpointEntity.setId(null);  // test point
        String message = "";
        try {
            underTest.updateEndpoint(restEndpointEntity);
        }catch(Exception e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(GlobalPreferencesMessages.REST_ENDPOINT_ENTITY_ID_CAN_NOT_BE_NULL, message);
    }

    /**
     * Test for updateEndpoint(RestEndpointEntity)
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#updateEndpoint(RestEndpointEntity)
     */
    @Test
    public void testUpdateEndpoint_endpointNotFound() throws Throwable {
        // Given
        boolean existsByIdResult = false;  // test point
        when(restEndPointRepository.existsById(nullable(Long.class))).thenReturn(existsByIdResult);

        // When
        RestEndpointEntity restEndpointEntity = new RestEndpointEntity();
        Long id = 1L;
        restEndpointEntity.setId(id);
        String message = "";
        try {
            underTest.updateEndpoint(restEndpointEntity);
        }catch(Exception e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(
                MessageFormat.format(GlobalPreferencesMessages.ID_OF_REST_ENDPOINT_ENTITY_NOT_FOUND, id), message);
    }

    /**
     * Test for validateUrl(String, String)
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#validateUrl(String, String)
     */
    @Test
    public void testValidateUrl() throws Throwable {
        // Given
        String url = "http://localhost:8080";

        // When
        underTest.validateUrl(url, GlobalPreferencesMessages.INVALID_URL);
    }

    /**
     * Test for validateUrl(String, String) with nullUrl
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#validateUrl(String, String)
     */
    @Test
    public void testValidateUrl_NullUrl() throws Throwable {
        // Given
        String url = null;
        String exceptionMessage = GlobalPreferencesMessages.BLANK_URL;

        // When
        String message = "";
        try {
            underTest.validateUrl(url, exceptionMessage);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(exceptionMessage, url), message);
    }

    /**
     * Test for validateUrl(String, String) with connectionException
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#validateUrl(String, String)
     */
    @Test
    public void testValidateUrl_ConnectionException() throws Throwable {
        // Given
        String url = "http://localhost:";
        String exceptionMessage = GlobalPreferencesMessages.INVALID_URL;

        // When
        String message = "";
        try {
            underTest.validateUrl(url, exceptionMessage);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        assertTrue(message.startsWith("Rest endpoint error: " + url));
    }

    /**
     * Test for validateUrl(String, String) with errorUrlException
     *
     * @see com.parasoft.demoapp.service.RestEndpointService#validateUrl(String, String)
     */
    @Test
    public void testValidateUrl_ErrorUrlException() throws Throwable {
        // Given
        String url = "error url";
        String exceptionMessage = GlobalPreferencesMessages.INVALID_URL;

        // When
        String message = "";
        try {
            underTest.validateUrl(url, exceptionMessage);
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        assertTrue(message.startsWith("Rest endpoint error: " + url));
    }

}