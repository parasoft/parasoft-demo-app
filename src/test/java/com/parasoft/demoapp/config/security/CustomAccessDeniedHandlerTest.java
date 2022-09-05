package com.parasoft.demoapp.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomAccessDeniedHandlerTest {

    CustomAccessDeniedHandler underTest = new CustomAccessDeniedHandler();

    /**
     * Test for handle(HttpServletRequest, HttpServletResponse, AccessDeniedException)
     *
     * @see com.parasoft.demoapp.config.security.CustomAccessDeniedHandler#handle(HttpServletRequest, HttpServletResponse, AccessDeniedException)
     */
    @Test
    void handle_restApiRequest() throws ServletException, IOException {
        // Given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        AccessDeniedException accessDeniedException = new AccessDeniedException("error message");
        httpServletRequest.setRequestURI("/v1/request"); // Test point: Rest api request

        // When
        underTest.handle(httpServletRequest, httpServletResponse, accessDeniedException);

        // Then
        assertEquals("{\"status\":0,\"message\":\"Current user does not have permission.\",\"data\":\"error message\"}", httpServletResponse.getContentAsString());
        assertEquals(403, httpServletResponse.getStatus());
    }

    /**
     * Test for handle(HttpServletRequest, HttpServletResponse, AccessDeniedException)
     *
     * @see com.parasoft.demoapp.config.security.CustomAccessDeniedHandler#handle(HttpServletRequest, HttpServletResponse, AccessDeniedException)
     */
    @Test
    void handle_noRestApiRequest() throws ServletException, IOException {
        // Given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        AccessDeniedException accessDeniedException = new AccessDeniedException("error message");
        httpServletRequest.setRequestURI("/request"); // Test point: Not rest api request

        // When
        underTest.handle(httpServletRequest, httpServletResponse, accessDeniedException);

        // Then
        assertEquals("", httpServletResponse.getContentAsString());
        assertEquals(302, httpServletResponse.getStatus());
        assertEquals("/accessDenied", httpServletResponse.getRedirectedUrl());
    }
}