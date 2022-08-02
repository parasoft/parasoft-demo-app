package com.parasoft.demoapp.defaultdata.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.parasoft.demoapp.repository.global.UserRepository;
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

import java.util.List;

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

  @Autowired
  UserRepository userRepository;

  /**
   * test for if default users are successfully created
   */
  @Test
  public void testDefaultUsersCreateSuccessfully() throws Throwable {
    String roleNameApprover = RoleType.ROLE_APPROVER.toString();
    String roleNamePurchaser = RoleType.ROLE_PURCHASER.toString();
    String usernameApprover = GlobalUsersCreator.USERNAME_APPROVER;
    String usernamePurchaser = GlobalUsersCreator.USERNAME_PURCHASER;

    // Then
    // check role ROLE_APPROVER
    RoleEntity role = roleService.getRoleByRoleName(roleNameApprover);
    assertNotNull(role);
    assertNotNull(role.getId());
    assertEquals(role.getName(), roleNameApprover);

    // check role ROLE_PURCHASER
    role = roleService.getRoleByRoleName(roleNamePurchaser);
    assertNotNull(role);
    assertNotNull(role.getId());
    assertEquals(role.getName(), roleNamePurchaser);

    // check user 'approver' and it's role/authority info
    UserEntity user = userService.getUserByUsername(usernameApprover);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertNotNull(user.getPassword());
    assertNotNull(user.getAuthorities());
    assertNotNull(user.getRole());
    assertTrue(passwordEncoder.matches(GlobalUsersCreator.PASSWORD, user.getPassword()));
    assertEquals(user.getUsername(), usernameApprover);
    assertEquals(1, user.getAuthorities().size());
    assertEquals(roleNameApprover,
            ((GrantedAuthority)(user.getAuthorities().toArray()[0])).getAuthority());
    assertEquals(roleNameApprover, user.getRole().getName());

    // check user 'purchaser' and it's role/authority info
    user = userService.getUserByUsername(usernamePurchaser);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertNotNull(user.getPassword());
    assertNotNull(user.getAuthorities());
    assertNotNull(user.getRole());
    assertEquals(user.getUsername(), usernamePurchaser);
    assertTrue(passwordEncoder.matches(GlobalUsersCreator.PASSWORD, user.getPassword()));
    assertEquals(1, user.getAuthorities().size());
    assertEquals(roleNamePurchaser,
            ((GrantedAuthority)(user.getAuthorities().toArray()[0])).getAuthority());
    assertEquals(roleNamePurchaser, user.getRole().getName());

    for (int i = 2; i < 51; i++) {
      // check forty nine 'approver' users and their roles/authorities info
      user = userService.getUserByUsername(usernameApprover + i);
      assertNotNull(user);
      assertNotNull(user.getId());
      assertNotNull(user.getPassword());
      assertNotNull(user.getAuthorities());
      assertNotNull(user.getRole());
      assertTrue(passwordEncoder.matches(GlobalUsersCreator.PASSWORD, user.getPassword()));
      assertEquals(1, user.getAuthorities().size());
      assertEquals(user.getUsername(), user.getUsername());
      assertEquals(roleNameApprover,
              ((GrantedAuthority)(user.getAuthorities().toArray()[0])).getAuthority());
      assertEquals(roleNameApprover, user.getRole().getName());

      // check forty nine 'purchaser' users and their roles/authorities info
      user = userService.getUserByUsername(usernamePurchaser + i);
      assertNotNull(user);
      assertNotNull(user.getId());
      assertNotNull(user.getPassword());
      assertNotNull(user.getAuthorities());
      assertNotNull(user.getRole());
      assertTrue(passwordEncoder.matches(GlobalUsersCreator.PASSWORD, user.getPassword()));
      assertEquals(1, user.getAuthorities().size());
      assertEquals(user.getUsername(), user.getUsername());
      assertEquals(roleNamePurchaser,
              ((GrantedAuthority)(user.getAuthorities().toArray()[0])).getAuthority());
      assertEquals(roleNamePurchaser, user.getRole().getName());
    }
  }
}