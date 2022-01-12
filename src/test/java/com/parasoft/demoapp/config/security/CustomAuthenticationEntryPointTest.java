/**
 * 
 */
package com.parasoft.demoapp.config.security;

import static com.parasoft.demoapp.util.HttpServletResponseUtil.CHARSET_UTF8;
import static com.parasoft.demoapp.util.HttpServletResponseUtil.CONTENT_TYPE_JSON;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

/**
 * Test class for CustomAuthenticationEntryPoint
 *
 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint
 */
public class CustomAuthenticationEntryPointTest {

	@InjectMocks
	CustomAuthenticationEntryPoint underTest;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for isRestfulApiRequest(HttpServletRequest)
	 *
	 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint#isRestfulApiRequest(HttpServletRequest)
	 */
	@Test
	public void testIsRestfulApiRequest_true1() throws Throwable {
		// When
		HttpServletRequest req = new MockHttpServletRequest(HttpMethod.GET.toString(), "/v1/");
		boolean result = underTest.isRestfulApiRequest(req);

		// Then
		assertTrue(result);
	}

	/**
	 * Test for isRestfulApiRequest(HttpServletRequest)
	 *
	 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint#isRestfulApiRequest(HttpServletRequest)
	 */
	@Test
	public void testIsRestfulApiRequest_true2() throws Throwable {
		// When
		HttpServletRequest req = new MockHttpServletRequest(HttpMethod.GET.toString(), "/v1/A");
		boolean result = underTest.isRestfulApiRequest(req);

		// Then
		assertTrue(result);
	}

	/**
	 * Test for isRestfulApiRequest(HttpServletRequest)
	 *
	 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint#isRestfulApiRequest(HttpServletRequest)
	 */
	@Test
	public void testIsRestfulApiRequest_true3() throws Throwable {
		// When
		HttpServletRequest req = new MockHttpServletRequest(HttpMethod.GET.toString(), "/v1.0/A");
		boolean result = underTest.isRestfulApiRequest(req);

		// Then
		assertTrue(result);
	}

	/**
	 * Test for isRestfulApiRequest(HttpServletRequest)
	 *
	 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint#isRestfulApiRequest(HttpServletRequest)
	 */
	@Test
	public void testIsRestfulApiRequest_true4() throws Throwable {
		// When
		HttpServletRequest req = new MockHttpServletRequest(HttpMethod.GET.toString(), "/v1.0.0/A");
		boolean result = underTest.isRestfulApiRequest(req);

		// Then
		assertTrue(result);
	}

	/**
	 * Test for isRestfulApiRequest(HttpServletRequest)
	 *
	 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint#isRestfulApiRequest(HttpServletRequest)
	 */
	@Test
	public void testIsRestfulApiRequest_false() throws Throwable {
		// When
		HttpServletRequest req = new MockHttpServletRequest(HttpMethod.GET.toString(), "/v/A");
		boolean result = underTest.isRestfulApiRequest(req);

		// Then
		assertFalse(result);
	}

	/**
	 * Test for commence(HttpServletRequest, HttpServletResponse, AuthenticationException)
	 *
	 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint#commence(HttpServletRequest, HttpServletResponse, AuthenticationException)
	 */
	@Test
	public void testCommence_redirectToLoginPage() throws Throwable {
		// When
		HttpServletRequest req = new MockHttpServletRequest(HttpMethod.GET.toString(), "/notRestfulApiRequest");
		HttpServletResponse resp = new MockHttpServletResponse();
		AuthenticationException authException = new InsufficientAuthenticationException("exception message.");
		underTest.commence(req, resp, authException);
		
		assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, resp.getStatus());
		assertEquals("/loginPage", resp.getHeader(HttpHeaders.LOCATION));

	}
	
	/**
	 * Test for commence(HttpServletRequest, HttpServletResponse, AuthenticationException)
	 *
	 * @see com.parasoft.demoapp.config.security.CustomAuthenticationEntryPoint#commence(HttpServletRequest, HttpServletResponse, AuthenticationException)
	 */
	@Test
	public void testCommence_returnJsonString() throws Throwable {
		// When
		HttpServletRequest req = new MockHttpServletRequest(HttpMethod.GET.toString(), "/v1/restfulApiRequest");
		HttpServletResponse resp = new MockHttpServletResponse();
		AuthenticationException authException = new InsufficientAuthenticationException("exception message.");
		underTest.commence(req, resp, authException);
		
		assertEquals(HttpStatus.FORBIDDEN.value(), resp.getStatus());
		assertEquals(CHARSET_UTF8, resp.getCharacterEncoding());
		assertEquals(CONTENT_TYPE_JSON, resp.getContentType());
	}
}