/**
 * 
 */
package com.parasoft.demoapp.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.parasoft.demoapp.model.global.preferences.IndustryType;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Test class for ImplementedIndustries
 *
 * @see com.parasoft.demoapp.config.ImplementedIndustries
 */
@RunWith(JUnitParamsRunner.class)
public class ImplementedIndustriesParameterizedTest {

	/**
	 * Test for isIndustryImplemented(IndustryType)
	 *
	 * @see com.parasoft.demoapp.config.ImplementedIndustries#isIndustryImplemented(IndustryType)
	 */
	@Test
	@Parameters(method = "testIsIndustryImplemented_Parameters")
	public void testIsIndustryImplemented(IndustryType industry, boolean expected) throws Throwable {
		boolean result = ImplementedIndustries.isIndustryImplemented(industry);

		assertEquals(expected, result);
	}

	// Initialize test parameters
	@SuppressWarnings("unused")
	private static Object[][] testIsIndustryImplemented_Parameters() throws Throwable {
		// Parameters: industry={0}, expected={1}
		return new Object[][] { { IndustryType.DEFENSE, true }, { IndustryType.AEROSPACE, true }, { IndustryType.OUTDOOR, true },
			{ IndustryType.HEALTHCARE, false }, { IndustryType.GOVERNMENT, false }, { IndustryType.RETAIL, false }};
	}
}