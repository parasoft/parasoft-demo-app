package com.parasoft.demoapp.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for CustomAuthenticationFailureHandler
 *
 * @see com.parasoft.demoapp.config.security.CustomAuthenticationFailureHandler
 */
public class CustomAuthenticationFailureHandlerTest {

    CustomAuthenticationFailureHandler underTest = new CustomAuthenticationFailureHandler();

    /**
     * Test for onAuthenticationFailure(HttpServletRequest, HttpServletResponse, AuthenticationException)
     *
     * @see com.parasoft.demoapp.config.security.CustomAuthenticationFailureHandler#onAuthenticationFailure(HttpServletRequest, HttpServletResponse, AuthenticationException)
     */
    @Test
    public void onAuthenticationFailure() throws ServletException, IOException {
        // Given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        // When
        underTest.onAuthenticationFailure(httpServletRequest, httpServletResponse, null);

        // Then
        assertEquals("{\"status\":0,\"message\":\"Current user is not authorized.\"}", httpServletResponse.getContentAsString());
        assertEquals(401, httpServletResponse.getStatus());
    }
}