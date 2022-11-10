package com.parasoft.demoapp.service;

import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test class for EndpointService
 *
 * @see com.parasoft.demoapp.service.EndpointService
 */
public class EndpointServiceTest {
    @InjectMocks
    EndpointService underTest;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for validateUrl(String, String)
     *
     * @see com.parasoft.demoapp.service.EndpointService#validateUrl(String, String)
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
     * @see com.parasoft.demoapp.service.EndpointService#validateUrl(String, String)
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
     * @see com.parasoft.demoapp.service.EndpointService#validateUrl(String, String)
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
     * @see com.parasoft.demoapp.service.EndpointService#validateUrl(String, String)
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
