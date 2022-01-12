/**
 * 
 */
package com.parasoft.demoapp.model.industry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.model.global.preferences.IndustryType;

/**
 * test for RegionType
 *
 * @see RegionType
 */
public class RegionTypeTest {

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for getRegionsByIndustryType(IndustryType)
	 *
	 * @see RegionType#getRegionsByIndustryType(IndustryType)
	 */
	@Test
	public void testGetRegionByIndustryType_Defense() {
		// When
		IndustryType industryType = IndustryType.DEFENSE;
		List<RegionType> result = RegionType.getRegionsByIndustryType(industryType);

		// Then
		assertNotNull(result);
		assertEquals(8, result.size());
	}

	/**
	 * test for getRegionsByIndustryType(IndustryType)
	 *
	 * @see RegionType#getRegionsByIndustryType(IndustryType)
	 */
	@Test
	public void testGetRegionsByIndustryType_Aerospace() {
		// When
		IndustryType industryType = IndustryType.AEROSPACE;
		List<RegionType> result = RegionType.getRegionsByIndustryType(industryType);

		// Then
		assertNotNull(result);
		assertEquals(8, result.size());
	}

	/**
	 * test for isIndustry(IndustryType)
	 * 
	 * @see com.parasoft.demoapp.model.industry.RegionType#isIndustry(IndustryType)
	 */
	@Test
	public void testIsIndustry() {
		// When
		IndustryType industryType = IndustryType.DEFENSE;
		Boolean result = RegionType.UNITED_STATES.isIndustry(industryType);

		// Then
		assertNotNull(result);
		assertEquals(true, result);
	}
}