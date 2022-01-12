package com.parasoft.demoapp.model.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RoleEntityTest {

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testEquals() throws Throwable {
		// Given
		String name = ""; 
		RoleEntity underTest = new RoleEntity(name);

		// When
		Object o = new Object(); 
		boolean result = underTest.equals(o);

		// Then
		 assertFalse(result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testGetId() throws Throwable {
		// Given
		RoleEntity underTest = new RoleEntity();
		
		// When
		Long result = underTest.getId();

		// Then
		 assertNull(result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testGetName() throws Throwable {
		// Given
		RoleEntity underTest = new RoleEntity();
		underTest.setName("demo");
		
		// When
		String result = underTest.getName();

		// Then
		 assertEquals("demo", result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testSetName() throws Throwable {
		// Given
		RoleEntity underTest = new RoleEntity();

		// When
		String name = ""; 
		underTest.setName(name);

	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testToString() throws Throwable {
		// Given
		RoleEntity underTest = new RoleEntity();
		// When
		String result = underTest.toString();

		// Then
		assertNotNull(result);
	}
}