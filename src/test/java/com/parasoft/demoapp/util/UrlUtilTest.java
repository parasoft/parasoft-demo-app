/**
 * 
 */
package com.parasoft.demoapp.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for UrlUtil
 *
 * @see com.parasoft.demoapp.util.UrlUtil
 */
public class UrlUtilTest {

	/**
	 * Test for isGoodHttpForm(String)
	 *
	 * @see com.parasoft.demoapp.util.UrlUtil#isGoodHttpForm(String)
	 */
	@Test
	public void testIsGoodHttpForm_positive() throws Throwable {
		// When
		String urlStr = "http://localhost:8080";
		boolean result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);

		// When
		urlStr = "http://localhost/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://localhost:8080/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://LOCALHOST:8080/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://192.168.0.1:8080/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "https://192.168.0.1:8080/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://192.168.0.1:8080/1";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://www.parasoft.com:8080";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://www.parasoft.com:8080/123";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://w:8080/123";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://www.parasoft.com/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);
		
		// When
		urlStr = "http://WWW.PARASOFT.COM/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);

		// When
		urlStr = "http://12";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);

		// When
		urlStr = "http://localhost/?";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertTrue(result);

	}

	@Test
	public void testIsGoodHttpForm_negative() throws Throwable {
		// When
		String urlStr = "http://192.168.0.1:8080/ 1";
		boolean result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertFalse(result);

		// When
		urlStr = "http:";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertFalse(result);

		// When
		urlStr = "http:/";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertFalse(result);

		// When
		urlStr = "htt";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertFalse(result);

		// When
		urlStr = "http://192.169.0.";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertFalse(result);

		// When
		urlStr = null;
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertFalse(result);

		// When
		urlStr = "http://localhost?";
		result = UrlUtil.isGoodHttpForm(urlStr);

		// Then
		assertFalse(result);

	}
}