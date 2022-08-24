/**
 *
 */
package com.parasoft.demoapp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.security.core.Authentication;

import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;

/**
 * Test class for AuthenticationUtil
 *
 * @see com.parasoft.demoapp.util.AuthenticationUtil
 */
public class AuthenticationUtilTest {

	/**
	 * Test for getUserIdInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUserIdInAuthentication(Authentication)
	 */
	@Test
	public void testGetUserIdInAuthentication_normal() throws Throwable {
		// When
		Authentication auth = mock(Authentication.class);
		UserEntity user = new UserEntity("user", "password");
		Long userId = 1L;
		user.setId(userId);
		Object principal = user;

		when(auth.getPrincipal()).thenReturn(principal);
		Long result = AuthenticationUtil.getUserIdInAuthentication(auth);

		// Then
		assertEquals(userId, result);
	}

	/**
	 * Test for getUserIdInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUserIdInAuthentication(Authentication)
	 */
	@Test
	public void testGetUserIdInAuthentication_nullAuthentication() throws Throwable {
		// When
		Authentication auth = null; // test point
		Long result = AuthenticationUtil.getUserIdInAuthentication(auth);

		// Then
		assertNull(result);
	}

	/**
	 * Test for getUserIdInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUserIdInAuthentication(Authentication)
	 */
	@Test
	public void testGetUserIdInAuthentication_nullPrincipal() throws Throwable {
		// When
		Authentication auth = mock(Authentication.class);
		Object principal = null; // test point
		when(auth.getPrincipal()).thenReturn(principal);
		Long result = AuthenticationUtil.getUserIdInAuthentication(auth);

		// Then
		assertNull(result);
	}

	/**
	 * Test for getUserRoleNameInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUserRoleNameInAuthentication(Authentication)
	 */
	@Test
	public void testGetUserRoleNameInAuthentication_normal() throws Throwable {
		// When
		Authentication auth = mock(Authentication.class);
		UserEntity user = new UserEntity("user", "password");
		String userRoleType = RoleType.ROLE_APPROVER.toString();
		RoleEntity roleEntity = new RoleEntity();
		roleEntity.setName(userRoleType);
		user.setRole(roleEntity);
		Object principal = user;

		when(auth.getPrincipal()).thenReturn(principal);
		String result = AuthenticationUtil.getUserRoleNameInAuthentication(auth);

		// Then
		assertEquals(userRoleType, result);
	}

	/**
	 * Test for getUserRoleNameInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUserRoleNameInAuthentication(Authentication)
	 */
	@Test
	public void testgetUserRoleNameInAuthentication_nullAuthentication() throws Throwable {
		// When
		Authentication auth = null; // test point
		String result = AuthenticationUtil.getUserRoleNameInAuthentication(auth);

		// Then
		assertNull(result);
	}

	/**
	 * Test for getUserRoleNameInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUserRoleNameInAuthentication(Authentication)
	 */
	@Test
	public void testGetUserRoleNameInAuthentication_nullPrincipal() throws Throwable {
		// When
		Authentication auth = mock(Authentication.class);
		Object principal = null; // test point
		when(auth.getPrincipal()).thenReturn(principal);
		String result = AuthenticationUtil.getUserRoleNameInAuthentication(auth);

		// Then
		assertNull(result);
	}

	/**
	 * Test for getUserRoleNameInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUserRoleNameInAuthentication(Authentication)
	 */
	@Test
	public void testGetUserRoleNameInAuthentication_nullRole() throws Throwable {
		// When
		Authentication auth = mock(Authentication.class);
		UserEntity user = mock(UserEntity.class);
		Object principal = user;
		RoleEntity roleEntity = null; // test point
		when(auth.getPrincipal()).thenReturn(principal);
		when(user.getRole()).thenReturn(roleEntity);
		String result = AuthenticationUtil.getUserRoleNameInAuthentication(auth);

		// Then
		assertNull(result);
	}

	/**
	 * Test for getUsernameInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUsernameInAuthentication(Authentication)
	 */
	@Test
	public void testGetUsernameInAuthentication_normal() {
		// Given
		Authentication auth = mock(Authentication.class);
		String username = "username";
		UserEntity user = new UserEntity(username, "password");
		Object principal = user;
		when(auth.getPrincipal()).thenReturn(principal);

		// When
		String result = AuthenticationUtil.getUsernameInAuthentication(auth);

		// Then
		assertEquals(username, result);
	}

	/**
	 * Test for getUsernameInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUsernameInAuthentication(Authentication)
	 */
	@Test
	public void testGetUsernameInAuthentication_nullAuthentication() {
		// Given
		Authentication auth = null; // test point

		// When
		String result = AuthenticationUtil.getUsernameInAuthentication(auth);

		// Then
		assertNull(result);
	}

	/**
	 * Test for getUsernameInAuthentication(Authentication)
	 *
	 * @see com.parasoft.demoapp.util.AuthenticationUtil#getUsernameInAuthentication(Authentication)
	 */
	@Test
	public void testGetUsernameInAuthentication_nullPrincipal() {
		// Given
		Authentication auth = mock(Authentication.class);
		Object principal = null; // test point
		when(auth.getPrincipal()).thenReturn(principal);

		// When
		String result = AuthenticationUtil.getUsernameInAuthentication(auth);

		// Then
		assertNull(result);
	}
}