/**
 * 
 */
package com.parasoft.demoapp.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.parasoft.demoapp.model.global.preferences.IndustryType;

/**
 * Test class for ImplementedIndustries
 *
 * @see com.parasoft.demoapp.config.ImplementedIndustries
 */
public class ImplementedIndustriesTest {

	/**
	 * Test for get()
	 *
	 * @see com.parasoft.demoapp.config.ImplementedIndustries#get()
	 */
	@Test
	public void testGet() throws Throwable {
		// When
		IndustryType[] result = ImplementedIndustries.get();

		// Then
		assertNotNull(result);
		assertEquals(3, result.length);
		assertEquals(IndustryType.DEFENSE, result[0]);
		assertEquals(IndustryType.AEROSPACE, result[1]);
		assertEquals(IndustryType.OUTDOOR, result[2]);
	}
}