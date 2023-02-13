package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.IdTokenDTO;
import com.parasoft.demoapp.exception.CannotLogoutFromKeycloakException;
import com.parasoft.demoapp.service.KeycloakService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@RunWith(PowerMockRunner.class)
public class SwaggerUIControllerTest {

    @InjectMocks
    SwaggerUIController underTest;

    @Mock
    KeycloakService keycloakService;

    /**
     * Test for showSwaggerUIPage()
     *
     * @see SwaggerUIController#showSwaggerUIPage()
     */
    @Test
    public void testShowSwaggerUIPage() throws Throwable {
        // When
        String result = underTest.showSwaggerUIPage();

        // Then
        assertEquals("swaggerUIIndex", result);
    }

    @Test
    public void swaggerOAuth2Logout_success() throws Throwable {
        // Given
        IdTokenDTO idToken = new IdTokenDTO();
        idToken.setIdToken("idToken");
        doNothing().when(keycloakService).oauth2Logout(anyString());

        // When
        ResponseResult<Void> result = underTest.swaggerOAuth2Logout(idToken);

        // Then
        assertEquals(ResponseResult.STATUS_OK, result.getStatus().intValue());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    @Test(expected = CannotLogoutFromKeycloakException.class)
    public void swaggerOAuth2Logout_error_cannotLogoutFromKeycloakException() throws Throwable {
        // Given
        IdTokenDTO idToken = new IdTokenDTO();
        idToken.setIdToken("idToken");
        doThrow(CannotLogoutFromKeycloakException.class).when(keycloakService).oauth2Logout(anyString());

        // When
        underTest.swaggerOAuth2Logout(idToken);
    }
}
