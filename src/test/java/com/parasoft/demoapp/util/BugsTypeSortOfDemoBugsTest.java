package com.parasoft.demoapp.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.parasoft.demoapp.model.global.preferences.DemoBugEntity;
import com.parasoft.demoapp.model.global.preferences.DemoBugsType;

/**
 * Test class for BugsTypeSortOfDemoBugs
 *
 * @see com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs
 */
public class BugsTypeSortOfDemoBugsTest {
	/**
	 * Test for compare(DemoBugEntity, DemoBugEntity) 'o1 equals to o2'
	 *
	 * @see com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs#compare(DemoBugEntity, DemoBugEntity)
	 */
	@Test
	public void testCompare_equal() throws Throwable {
		// Given
		BugsTypeSortOfDemoBugs underTest = new BugsTypeSortOfDemoBugs();
		DemoBugEntity o1 = mock(DemoBugEntity.class);
		when(o1.getDemoBugsType()).thenReturn(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS);
		DemoBugEntity o2 = mock(DemoBugEntity.class);
		when(o2.getDemoBugsType()).thenReturn(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS);

		// When
		int result = underTest.compare(o1, o2);

		// Then
		assertEquals(0, result);
		
	}

	/**
	 * Test for compare(DemoBugEntity, DemoBugEntity) 'o1 is less than o2'
	 *
	 * @see com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs#compare(DemoBugEntity, DemoBugEntity)
	 */
	@Test
	public void testCompare_less_than() throws Throwable {
		// Given
		BugsTypeSortOfDemoBugs underTest = new BugsTypeSortOfDemoBugs();
		DemoBugEntity o1 = mock(DemoBugEntity.class);
		when(o1.getDemoBugsType()).thenReturn(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS);
		DemoBugEntity o2 = mock(DemoBugEntity.class);
		when(o2.getDemoBugsType()).thenReturn(DemoBugsType.INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER);

		// When
		int result = underTest.compare(o1, o2);

		// Then
		assertEquals(-2, result);
		
	}
	
	/**
	 * Test for compare(DemoBugEntity, DemoBugEntity) 'o1 is greater than o2'
	 *
	 * @see com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs#compare(DemoBugEntity, DemoBugEntity)
	 */
	@Test
	public void testCompare_greater_than() throws Throwable {
		// Given
		BugsTypeSortOfDemoBugs underTest = new BugsTypeSortOfDemoBugs();
		DemoBugEntity o1 = mock(DemoBugEntity.class);
		when(o1.getDemoBugsType()).thenReturn(DemoBugsType.INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER);
		DemoBugEntity o2 = mock(DemoBugEntity.class);
		when(o2.getDemoBugsType()).thenReturn(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS);

		// When
		int result = underTest.compare(o1, o2);

		// Then
		assertEquals(2, result);
		
	}
}