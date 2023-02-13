package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.IdToken;
import com.parasoft.demoapp.service.KeycloakService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        //Given
        IdToken idToken = new IdToken();
        idToken.setIdToken("idToken");
        when(keycloakService.oauth2Logout(anyString())).thenReturn("Logout");

        //When
        underTest.swaggerOAuth2Logout(idToken);

    }

    @Test
    public void swaggerOAuth2Logout_failed() throws Throwable {
        //Given
        IdToken idToken = new IdToken();
        idToken.setIdToken("idToken");

        //When
        underTest.swaggerOAuth2Logout(idToken);
    }
}
