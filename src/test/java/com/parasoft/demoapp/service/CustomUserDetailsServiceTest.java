package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.parasoft.demoapp.model.global.UserEntity;

/**
 * test class CustomUserDetailsService
 *
 * @see CustomUserDetailsService
 */
public class CustomUserDetailsServiceTest {

	@InjectMocks
	CustomUserDetailsService underTest;

	@Mock
	UserService userService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for loadUserByUsername(String)
	 *
	 * @see CustomUserDetailsService#loadUserByUsername(String)
	 */
	@Test
	public void testLoadUserByUsername() throws Throwable {
		// Given
		String username = "username";
		String password = "password";
		UserEntity getUserByUsernameResult = new UserEntity(username, password);
		when(userService.getUserByUsername(anyString())).thenReturn(getUserByUsernameResult);

		// When
		UserDetails result = underTest.loadUserByUsername(username);

		// Then
		assertNotNull(result);
		assertEquals(username, result.getUsername());
		assertEquals(password, result.getPassword());
		assertEquals(0, result.getAuthorities().size());
	}
	
	/**
	 * test for loadUserByUsername(String)
	 *
	 * @see CustomUserDetailsService#loadUserByUsername(String)
	 */
	@Test(expected = UsernameNotFoundException.class)
	public void testLoadUserByUsername_usernameNotFoundException() throws Throwable {
		// Given
		String username = "username";
		when(userService.getUserByUsername(anyString())).thenThrow(UsernameNotFoundException.class);

		// When
		underTest.loadUserByUsername(username);
	}
}