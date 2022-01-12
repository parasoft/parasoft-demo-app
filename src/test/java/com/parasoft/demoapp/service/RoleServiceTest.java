package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.messages.Messages;
import com.parasoft.demoapp.messages.UserMessages;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.repository.global.RoleRepository;

/**
 * test for class RoleService
 *
 * @see RoleService
 */
public class RoleServiceTest {

	@InjectMocks
	RoleService underTest;

	@Mock
	RoleRepository roleRepository;

	Messages messages = new UserMessages();

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for addNewRole(String)
	 *
	 * @see RoleService#addNewRole(String)
	 */
	@Test
	public void testAddNewRole_roleNameExistsAlready() throws Throwable {
		// Given
		boolean existsByNameResult = true;
		when(roleRepository.existsByName(nullable(String.class))).thenReturn(existsByNameResult);

		RoleEntity saveResult = mock(RoleEntity.class);
		doReturn(saveResult).when(roleRepository).save((RoleEntity) any());

		// When
		String roleName = "ROLE_EXISTS";
		String message = "";
		try {
			underTest.addNewRole(roleName);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(UserMessages.ROLE_NAME_EXISTS_ALREADY, roleName), message);
	}

	/**
	 * test for addNewRole(String)
	 *
	 * @see RoleService#addNewRole(String)
	 */
	@Test
	public void testAddNewRole_normal() throws Throwable {
		// Given
		boolean existsByNameResult = false;
		when(roleRepository.existsByName(nullable(String.class))).thenReturn(existsByNameResult);

		RoleEntity saveResult = mock(RoleEntity.class);
		doReturn(saveResult).when(roleRepository).save((RoleEntity) any());

		// When
		String roleName = "ROLE_NORMAL";
		RoleEntity result = underTest.addNewRole(roleName);

		// Then
		assertEquals(saveResult, result);
	}

	/**
	 * test for roleExists(String)
	 *
	 * @see RoleService#roleExists(String)
	 */
	@Test
	public void testRoleExists_roleNameExists() throws Throwable {
		// Given
		boolean existsByNameResult = true;
		when(roleRepository.existsByName(nullable(String.class))).thenReturn(existsByNameResult);

		// When
		String roleName = "ROLE_EXISTS";
		boolean result = underTest.roleExists(roleName);

		// Then
		assertTrue(result);
	}

	/**
	 * test for roleExists(String)
	 *
	 * @see RoleService#roleExists(String)
	 */
	@Test
	public void testRoleExists_roleNameNotExists() throws Throwable {
		// Given
		boolean existsByNameResult = false;
		when(roleRepository.existsByName(nullable(String.class))).thenReturn(existsByNameResult);

		// When
		String roleName = "ROLE_NOTEXISTS";
		boolean result = underTest.roleExists(roleName);

		// Then
		assertFalse(result);
	}

	/**
	 * test for getRoleByRoleName(String)
	 *
	 * @see RoleService#getRoleByRoleName(String)
	 */
	@Test
	public void testGetRoleByRoleName_normal() throws Throwable {
		// Given
		RoleEntity findByNameResult = mock(RoleEntity.class);
		when(roleRepository.findByName(nullable(String.class))).thenReturn(findByNameResult);

		// When
		String roleName = "ROLE_NORMAL";
		RoleEntity result = underTest.getRoleByRoleName(roleName);

		// Then
		assertEquals(findByNameResult, result);
	}
	
	/**
	 * test for getRoleByRoleName(String)
	 *
	 * @see RoleService#getRoleByRoleName(String)
	 */
	@Test
	public void testGetRoleByRoleName_nullReturn() throws Throwable {
		// Given
		RoleEntity findByNameResult = null;
		when(roleRepository.findByName(nullable(String.class))).thenReturn(findByNameResult);

		// When
		String roleName = "ROLE_NOTEXISTS";
		String message = "";
		try {
			underTest.getRoleByRoleName(roleName);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(UserMessages.ROLE_NAME_NOT_FOUND, roleName), message);
	}

}