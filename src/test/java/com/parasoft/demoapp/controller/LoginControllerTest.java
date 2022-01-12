package com.parasoft.demoapp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;

import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.util.SessionUtil;

/**
 * Parasoft Jtest UTA: tested class is LoginController
 *
 * @see LoginController
 */
@PrepareForTest({ SessionUtil.class })
@RunWith(PowerMockRunner.class)
public class LoginControllerTest {

	@InjectMocks
	LoginController underTest;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	/**
	 * Parasoft Jtest UTA: tested method is login(HttpSession)
	 *
	 * @see LoginController#showLoginPage(HttpSession, ModelMap)
	 */
	@Test
	public void testLogin_firstVisitLoginPage() throws Throwable {
		//Given
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("industry", "defense");

		when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

		// When
		HttpSession session = mock(HttpSession.class);
		String result = underTest.showLoginPage(session, modelMap);

		// Then
		assertEquals("login", result);
	}

	/**
	* Parasoft Jtest UTA: tested method is showLoginPage(HttpSession)
	*
	* @see LoginController#showLoginPage(HttpSession, ModelMap)
	*/
	@Test
	public void testLogin_returnLoginPageWithoutLogout() throws Throwable {

		//Given
		spy(SessionUtil.class);

		UserEntity getUserIdInSessionResult = new UserEntity();
		doReturn(getUserIdInSessionResult).when(SessionUtil.class);
		SessionUtil.getUserEntityInSession(nullable(HttpSession.class));

		when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute(nullable(String.class))).thenReturn("ROLE_APPROVER");
		
		// When
		ModelMap modelMap = new ModelMap();
		String result = underTest.showLoginPage(session, modelMap);
		
		// Then
		assertEquals("redirect:/", result);
	}

	/**
	 * Test for showLoginPage(HttpSession, ModelMap)
	 *
	 * @see LoginController#showLoginPage(HttpSession, ModelMap)
	 */
	@Test
	public void testLogin_error_globalPreferencesMoreThanOneException() throws Throwable {
		//Given
		when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

		// When
		HttpSession session = mock(HttpSession.class);
		ModelMap modelMap = new ModelMap();
		String result = underTest.showLoginPage(session, modelMap);

		// Then
		assertEquals("error", result);
	}

	/**
	 * Test for showLoginPage(HttpSession, ModelMap)
	 *
	 * @see LoginController#showLoginPage(HttpSession, ModelMap)
	 */
	@Test
	public void testLogin_error_globalPreferencesNotFoundException() throws Throwable {
		//Given
		when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

		// When
		HttpSession session = mock(HttpSession.class);
		ModelMap modelMap = new ModelMap();
		String result = underTest.showLoginPage(session, modelMap);

		// Then
		assertEquals("error", result);
	}
}