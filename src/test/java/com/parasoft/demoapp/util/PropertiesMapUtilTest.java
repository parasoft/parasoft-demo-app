/**
 * 
 */
package com.parasoft.demoapp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test class for PropertiesMapUtil
 *
 * @see com.parasoft.demoapp.util.PropertiesMapUtil
 */
public class PropertiesMapUtilTest {

	/**
	 * Test for sortByKey(Map)
	 *
	 * @see com.parasoft.demoapp.util.PropertiesMapUtil#sortByKey(Map)
	 */
	@Test
	public void testSortByKey_null() throws Throwable {
		// When
		Map<String, String> map = null;
		Map<String, String> result = PropertiesMapUtil.sortByKey(map);

		// Then
		assertNull(result);
	}
	
	/**
	 * Test for sortByKey(Map)
	 *
	 * @see com.parasoft.demoapp.util.PropertiesMapUtil#sortByKey(Map)
	 */
	@Test
	public void testSortByKey_normal() throws Throwable {
		// When
		Map<String, String> map = new HashMap<>();
		map.put("c", null);
		map.put("b", "b");
		map.put("a", "a");
		Map<String, String> result = PropertiesMapUtil.sortByKey(map);

		// Then
		assertNotNull(result);
		assertEquals("{a=a, b=b, c=null}", result.toString());
	}
}