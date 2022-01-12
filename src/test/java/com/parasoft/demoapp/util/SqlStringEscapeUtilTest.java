package com.parasoft.demoapp.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for SqlStringEscapeUtil
 *
 * @see com.parasoft.demoapp.util.SqlStringEscapeUtil
 */
public class SqlStringEscapeUtilTest {

	/**
	 * Test for escapeLikeString(String)
	 *
	 * @see com.parasoft.demoapp.util.SqlStringEscapeUtil#escapeLikeString(String)
	 */
	@Test
	public void testEscapeLikeString1() throws Throwable {
		// When
		String str = "^[]/ab%c__";
		String result = SqlStringEscapeUtil.escapeLikeString(str);

		// Then
		assertEquals("^[]//ab/%c/_/_", result);
	}
}