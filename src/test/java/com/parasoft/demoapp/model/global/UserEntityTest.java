package com.parasoft.demoapp.model.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

public class UserEntityTest {
	
	/**
	 * to test get authorities
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testGetAuthorities() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();
		RoleEntity role = new RoleEntity();
		String roleName = "ROLE_ADMIN";
		role.setName(roleName);
		underTest.setRole(role);
		
		// When
		Collection<? extends GrantedAuthority> result = underTest.getAuthorities();
		String authority = new ArrayList<GrantedAuthority>(result).get(0).getAuthority();
		
		// Then
		 assertNotNull(result);
		 assertEquals(1, result.size());
		 assertFalse(result.contains(null));
		 assertEquals(roleName, authority);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testEquals() throws Throwable {
		// Given
		String username = ""; 
		String password = ""; 
		UserEntity underTest = new UserEntity(username, password);

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
		UserEntity underTest = new UserEntity();

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
	public void testGetPassword() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		underTest.setPassword("demo");
		String result = underTest.getPassword();

		// Then
		 assertEquals("demo", result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testGetRole() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		RoleEntity result = underTest.getRole();

		// Then
		 assertNull(result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testGetUsername() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		underTest.setUsername("demo");
		// When
		String result = underTest.getUsername();

		// Then
		 assertEquals("demo", result);
	}
	
	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testIsAccountNonExpired() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		boolean result = underTest.isAccountNonExpired();

		// Then
		 assertTrue(result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testIsAccountNonLocked() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		boolean result = underTest.isAccountNonLocked();

		// Then
		 assertTrue(result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testIsCredentialsNonExpired() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		boolean result = underTest.isCredentialsNonExpired();

		// Then
		 assertTrue(result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testIsEnabled() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		boolean result = underTest.isEnabled();

		// Then
		 assertTrue(result);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testSetPassword() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		String password = ""; 
		underTest.setPassword(password);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testSetUsername() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		String username = ""; 
		underTest.setUsername(username);
	}

	/**
	 * just for the coverage of unit tests
	 * @throws Throwable
	 */
	@Test(timeout = 1000)
	public void testToString() throws Throwable {
		// Given
		UserEntity underTest = new UserEntity();

		// When
		String result = underTest.toString();

		// Then
		 assertNotNull(result);
	}
}