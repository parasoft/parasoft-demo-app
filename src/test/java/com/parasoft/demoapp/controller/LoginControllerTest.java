package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.global.preferences.WebServiceMode;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Parasoft Jtest UTA: tested class is LoginController
 *
 * @see LoginController
 */
public class LoginControllerTest {

    @InjectMocks
    LoginController underTest;

    @Mock
    GlobalPreferencesService globalPreferencesService;

    @Mock
    GlobalPreferencesEntity globalPreferencesEntity;

    @Before
    public void setupMocks()  throws Throwable {
        MockitoAnnotations.initMocks(this);
        when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferencesEntity);
        when(globalPreferencesEntity.getWebServiceMode()).thenReturn(WebServiceMode.GRAPHQL);
    }

    /**
     * Test showLoginPage(Authentication, ModelMap)
     *
     * @see LoginController#showLoginPage(Authentication, ModelMap)
     */
    @Test
    public void testLogin_returnLoginPage() throws Throwable {
        //Given
        ModelMap modelMap = new ModelMap();
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        HttpSession session = mock(HttpSession.class);
        String result = underTest.showLoginPage(null, modelMap);

        // Then
        assertEquals("login", result);
    }

    /**
     * Test showLoginPage(Authentication, ModelMap)
     *
     * @see LoginController#showLoginPage(Authentication, ModelMap)
     */
    @Test
    public void testLogin_returnHomePage() throws Throwable {

        //Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showLoginPage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);

        // Then
        assertEquals("redirect:/", result);
    }

    /**
     * Test showLoginPage(Authentication, ModelMap)
     *
     * @see LoginController#showLoginPage(Authentication, ModelMap)
     */
    @Test
    public void testLogin_error_globalPreferencesMoreThanOneException() throws Throwable {
        //Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showLoginPage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test showLoginPage(Authentication, ModelMap)
     *
     * @see LoginController#showLoginPage(Authentication, ModelMap)
     */
    @Test
    public void testLogin_error_globalPreferencesNotFoundException() throws Throwable {
        //Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showLoginPage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);

        // Then
        assertEquals("error/500", result);
    }
}