package com.parasoft.demoapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;

import org.junit.Test;

import com.parasoft.demoapp.messages.GlobalPreferencesMessages;

/**
 * Test class for ParasoftJDBCProxyService
 *
 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService
 */
public class ParasoftJDBCProxyServiceTest {

	/**
	 * Test for validateVirtualizeServerUrl(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeServerUrl(String)
	 */
	@Test
	public void testValidateVirtualizeServerUrl_virtualizeServerUrlException_timeOut() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String virtualizeServerUrl = "http://localhost:01";
		String message = "";
		
		try {
			underTest.validateVirtualizeServerUrl(virtualizeServerUrl);
		}catch(Exception e) {
			message= e.getMessage();
		}
		
		assertNotNull(message);
		assertTrue(message.startsWith("Parasoft Virtualize server URL error: "));
	}
	
	/**
	 * Test for validateVirtualizeServerUrl(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeServerUrl(String)
	 */
	@Test
	public void testValidateVirtualizeServerUrl_virtualizeServerUrlException_not200() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String virtualizeServerUrl = "http://www.parasoft.com";
		String message = "";
		
		try {
			underTest.validateVirtualizeServerUrl(virtualizeServerUrl);
		}catch(Exception e) {
			message= e.getMessage();
		}
		
		assertNotNull(message);
		assertTrue(message.startsWith("Parasoft Virtualize server URL error: "));
		assertTrue(message.endsWith("301"));
	}
	
	/**
	 * Test for validateVirtualizeServerUrl(String)
	 * <br/>
	 * This test needs Virtualize server enabled.
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeServerUrl(String)
	 */
	//@Test
	public void testValidateVirtualizeServerUrl_normal() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String virtualizeServerUrl = "http://localhost:9080";
		
		underTest.validateVirtualizeServerUrl(virtualizeServerUrl);
		
	}

	/**
	 * Test for validateVirtualizeServerPath(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeServerPath(String)
	 */
	@Test
	public void testValidateVirtualizeServerPath_nullServerPath() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String virtualizeServerPath = null;

		String message = "";

		try {
			underTest.validateVirtualizeServerPath(virtualizeServerPath);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
                virtualizeServerPath));

	}

	/**
	 * Test for validateVirtualizeServerPath(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeServerPath(String)
	 */
	@Test
	public void testValidateVirtualizeServerPath_illegalServerPath() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String virtualizeServerPath = ""; // test point

		String message = "";

		try {
			underTest.validateVirtualizeServerPath(virtualizeServerPath);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
				virtualizeServerPath));


		// When
		virtualizeServerPath = " /path"; // test point

		try {
			underTest.validateVirtualizeServerPath(virtualizeServerPath);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
				virtualizeServerPath));


		// When
		virtualizeServerPath = "/p ath"; // test point

		try {
			underTest.validateVirtualizeServerPath(virtualizeServerPath);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
				virtualizeServerPath));


		// When
		virtualizeServerPath = "path"; // test point

		try {
			underTest.validateVirtualizeServerPath(virtualizeServerPath);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
				virtualizeServerPath));


		// When
		virtualizeServerPath = "path?!*()";

		try {
			underTest.validateVirtualizeServerPath(virtualizeServerPath);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
				virtualizeServerPath));

		// When
		virtualizeServerPath = "/path/"; // test point

		try {
			underTest.validateVirtualizeServerPath(virtualizeServerPath);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
				virtualizeServerPath));

	}

	/**
	 * Test for validateVirtualizeServerPath(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeServerPath(String)
	 */
	@Test
	public void testValidateVirtualizeServerPath_normal() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String virtualizeServerPath = "/path"; // test point
		underTest.validateVirtualizeServerPath(virtualizeServerPath);

		// When
		virtualizeServerPath = "/path123"; // test point
		underTest.validateVirtualizeServerPath(virtualizeServerPath);

		// When
		virtualizeServerPath = "/path123_-"; // test point
		underTest.validateVirtualizeServerPath(virtualizeServerPath);

		// When
		virtualizeServerPath = "/path123_-"; // test point
		underTest.validateVirtualizeServerPath(virtualizeServerPath);

	}

	/**
	 * Test for validateVirtualizeGroupId(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeGroupId(String)
	 */
	@Test
	public void testValidateVirtualizeGroupId_nullGroupId() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String message = "";
		String virtualizeGroupId = null; // test point
		try {
			underTest.validateVirtualizeGroupId(virtualizeGroupId);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_GROUP_ID,
				virtualizeGroupId));

	}

	/**
	 * Test for validateVirtualizeGroupId(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeGroupId(String)
	 */
	@Test
	public void testValidateVirtualizeGroupId_illegalGroupId() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String message = "";
		String virtualizeGroupId = ""; // test point

		try {
			underTest.validateVirtualizeGroupId(virtualizeGroupId);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_GROUP_ID,
				virtualizeGroupId));


		// When
		virtualizeGroupId = " pda"; // test point

		try {
			underTest.validateVirtualizeGroupId(virtualizeGroupId);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_GROUP_ID,
				virtualizeGroupId));


		// When
		virtualizeGroupId = "p da"; // test point

		try {
			underTest.validateVirtualizeGroupId(virtualizeGroupId);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_GROUP_ID,
				virtualizeGroupId));


		// When
		virtualizeGroupId = "pda!?*"; // test point

		try {
			underTest.validateVirtualizeGroupId(virtualizeGroupId);
		}catch(Exception e) {
			message= e.getMessage();
		}

		// Then
		assertNotNull(message);
		assertEquals(message, MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_GROUP_ID,
				virtualizeGroupId));

	}

	/**
	 * Test for validateVirtualizeGroupId(String)
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#validateVirtualizeGroupId(String)
	 */
	@Test
	public void testValidateVirtualizeGroupId_normal() throws Throwable {
		// Given
		ParasoftJDBCProxyService underTest = new ParasoftJDBCProxyService();

		// When
		String virtualizeGroupId = "pda"; // test point
		underTest.validateVirtualizeGroupId(virtualizeGroupId);

		// When
		virtualizeGroupId = "pda-_"; // test point
		underTest.validateVirtualizeGroupId(virtualizeGroupId);
	}
}