/**
 * 
 */
package com.parasoft.demoapp.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import com.parasoft.demoapp.model.global.UserEntity;

/**
 * Parasoft Jtest UTA: tested class is SessionUtil
 *
 * @see com.parasoft.demoapp.util.SessionUtil
 */
public class SessionUtilTest {
	/**
	 * Parasoft Jtest UTA: test for getUserEntityInSession(HttpSession)
	 *
	 * @see com.parasoft.demoapp.util.SessionUtil#getUserEntityInSession(HttpSession)
	 */
	@Test
	public void testGetUserEntityInSession_withoutSession() throws Throwable {
		// When
		HttpSession session = null;
		UserEntity result = SessionUtil.getUserEntityInSession(session);

		// Then
		assertNull(result);
	}
	
	/**
	 * Parasoft Jtest UTA: test for getUserEntityInSession(HttpSession)
	 *
	 * @see com.parasoft.demoapp.util.SessionUtil#getUserEntityInSession(HttpSession)
	 */
	@Test
	public void testGetUserEntityInSession() throws Throwable {
		// When
		HttpSession session = mockHttpSession();
		UserEntity result = SessionUtil.getUserEntityInSession(session);
		
		// Then
		assertNotNull(result);
	}

	/**
	 * Parasoft Jtest UTA: Mock HttpSession 
	 */
	private static HttpSession mockHttpSession() throws Throwable {
		 HttpSession session = mock(HttpSession.class);
		 SecurityContext securityContext = mock(SecurityContext.class);
		 
		 Authentication authentication = mock(Authentication.class);
		 securityContext.setAuthentication(authentication);

		 when(session.getAttribute(nullable(String.class))).thenReturn(securityContext);
		 when(securityContext.getAuthentication()).thenReturn(authentication);
		 when(authentication.getPrincipal()).thenReturn(mock(UserEntity.class));
		 return session;
	}
	
	/**
	 * Parasoft Jtest UTA: test for getUserEntityInSession(HttpSession)
	 *
	 * @see com.parasoft.demoapp.util.SessionUtil#getUserEntityInSession(HttpSession)
	 */
	@Test
	public void testGetUserEntityInSession_withoutSecurityContext() throws Throwable {
		// When
		HttpSession session = mockHttpSession_withoutSecurityContext();
		UserEntity result = SessionUtil.getUserEntityInSession(session);
		
		// Then
		assertNull(result);
	}

	/**
	 * Parasoft Jtest UTA: Mock HttpSession 
	 */
	private static HttpSession mockHttpSession_withoutSecurityContext() throws Throwable {
	    HttpSession session = mock(HttpSession.class);
	    
	    SecurityContext securityContext = null; // test point
	    
	    when(session.getAttribute(nullable(String.class))).thenReturn(securityContext);
	    return session;
	}
	
	/**
	 * Parasoft Jtest UTA: test for getUserEntityInSession(HttpSession)
	 *
	 * @see com.parasoft.demoapp.util.SessionUtil#getUserEntityInSession(HttpSession)
	 */
	@Test
	public void testGetUserEntityInSession_withoutAuthentication() throws Throwable {
		// When
		HttpSession session = mockHttpSession_withoutAuthentication();
		UserEntity result = SessionUtil.getUserEntityInSession(session);
		
		// Then
		assertNull(result);
	}
	
	/**
	 * Parasoft Jtest UTA: Mock HttpSession 
	 */
	private static HttpSession mockHttpSession_withoutAuthentication() throws Throwable {
		 HttpSession session = mock(HttpSession.class);
		 
		 SecurityContext securityContext = mock(SecurityContext.class);
		 
		 when(session.getAttribute(nullable(String.class))).thenReturn(securityContext);
		 when(securityContext.getAuthentication()).thenReturn(null); // test point
		 return session;
	}
	
	/**
	 * Parasoft Jtest UTA: test for getUserEntityInSession(HttpSession)
	 *
	 * @see com.parasoft.demoapp.util.SessionUtil#getUserEntityInSession(HttpSession)
	 */
	@Test
	public void testGetUserEntityInSession_withoutPrincipal() throws Throwable {
		// When
		HttpSession session = mockHttpSession_withoutPrincipal();
		UserEntity result = SessionUtil.getUserEntityInSession(session);
		
		// Then
		assertNull(result);
	}
	
	/**
	 * Parasoft Jtest UTA: Mock HttpSession 
	 */
	private static HttpSession mockHttpSession_withoutPrincipal() throws Throwable {
		 HttpSession session = mock(HttpSession.class);
		 SecurityContext securityContext = mock(SecurityContext.class);
		
		 Authentication authentication = mock(Authentication.class);
		 securityContext.setAuthentication(authentication);
		 
		 when(session.getAttribute(nullable(String.class))).thenReturn(securityContext);
		 when(securityContext.getAuthentication()).thenReturn(authentication);
		 when(authentication.getPrincipal()).thenReturn(null); // test point
		 return session;
	}
}