package com.parasoft.demoapp.defaultdata.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.service.RoleService;
import com.parasoft.demoapp.service.UserService;

/**
 * test class GlobalUsersCreator
 *
 * @see GlobalUsersCreator
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class GlobalUsersCreatorSpringTest {

	@Autowired
	UserService userService;
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * test for if default users are successfully created
	 */
	@Test
	public void testDefaultUsersCreateSuccessfully() throws Throwable {
		
		// Then
		// check role ROLE_APPROVER
		String rolename = RoleType.ROLE_APPROVER.toString();
		RoleEntity role = roleService.getRoleByRoleName(rolename);
		assertNotNull(role);
		assertNotNull(role.getId());
		assertEquals(role.getName(), rolename);
		
		// check role ROLE_PURCHASER
		rolename = RoleType.ROLE_PURCHASER.toString();
		role = roleService.getRoleByRoleName(rolename);
		assertNotNull(role);
		assertNotNull(role.getId());
		assertEquals(role.getName(), rolename);
		
		// check user 'approver' and it's role/authority info
		String username = GlobalUsersCreator.USERNAME_APPROVER;
		UserEntity user = userService.getUserByUsername(username);
		assertNotNull(user);
		assertNotNull(user.getId());
		assertNotNull(user.getPassword());
		assertNotNull(user.getAuthorities());
		assertNotNull(user.getRole());
		assertTrue(passwordEncoder.matches(GlobalUsersCreator.PASSWORD, user.getPassword()));
		assertEquals(user.getUsername(), username);
		assertEquals(1, user.getAuthorities().size());
		assertEquals(RoleType.ROLE_APPROVER.toString(),
				((GrantedAuthority)(user.getAuthorities().toArray()[0])).getAuthority());
		assertEquals(RoleType.ROLE_APPROVER.toString(), user.getRole().getName());

		// check user 'purchaser' and it's role/authority info
		username = GlobalUsersCreator.USERNAME_PURCHASER;
		user = userService.getUserByUsername(username);
		assertNotNull(user);
		assertNotNull(user.getId());
		assertNotNull(user.getPassword());
		assertNotNull(user.getAuthorities());
		assertNotNull(user.getRole());
		assertEquals(user.getUsername(), username);
		assertTrue(passwordEncoder.matches(GlobalUsersCreator.PASSWORD, user.getPassword()));
		assertEquals(1, user.getAuthorities().size());
		assertEquals(RoleType.ROLE_PURCHASER.toString(),
				((GrantedAuthority)(user.getAuthorities().toArray()[0])).getAuthority());
		assertEquals(RoleType.ROLE_PURCHASER.toString(), user.getRole().getName());

	}
}