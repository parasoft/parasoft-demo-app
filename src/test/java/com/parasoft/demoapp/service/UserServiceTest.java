package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.UserNotFoundException;
import com.parasoft.demoapp.exception.UsernameExistsAlreadyException;
import com.parasoft.demoapp.messages.UserMessages;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.repository.global.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * test class UserService
 *
 * @see UserService
 */
public class UserServiceTest {

    @InjectMocks
    UserService underTest;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * test for getUserByUsername(String)
     *
     * @see UserService#getUserByUsername(String)
     */
    @Test
    public void testGetUserByUsername_normal_noRole() throws Throwable {
        // Given
        String username = "username";
        String password = "password";

        UserEntity findByUsernameResult = new UserEntity(username, password);
        when(userRepository.findByUsername(anyString())).thenReturn(findByUsernameResult);

        // When
        UserEntity result = underTest.getUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(0, result.getAuthorities().size());
    }

    /**
     * test for getUserByUsername(String)
     *
     * @see UserService#getUserByUsername(String)
     */
    @Test
    public void testGetUserByUsername_normal_withRole() throws Throwable {
        // Given
        String username = "username";
        String password = "password";
        RoleEntity role = new RoleEntity(RoleType.ROLE_APPROVER.toString());

        UserEntity findByUsernameResult = new UserEntity(username, password, role);
        when(userRepository.findByUsername(anyString())).thenReturn(findByUsernameResult);

        // When
        UserEntity result = underTest.getUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(1, result.getAuthorities().size());
    }

    /**
     * test for getUserByUsername(String)
     *
     * @see UserService#getUserByUsername(String)
     */
    @Test
    public void testGetUserByUsername_nullUsername() throws Throwable {

        // When
        String username = null;
        String message = "";

        try {
            underTest.getUserByUsername(username);
        } catch (NullPointerException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(UserMessages.USERNAME_CANNOT_NULL, message);
    }

    /**
     * test for getUserByUsername(String)
     *
     * @see UserService#getUserByUsername(String)
     */
    @Test
    public void testGetUserByUsername_usernameNotFound() throws Throwable {
        // Given
        UserEntity findByUsernameResult = null;
        when(userRepository.findByUsername(anyString())).thenReturn(findByUsernameResult);

        // When
        String username = "test";
        String message = "";

        try {
            underTest.getUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(UserMessages.USERNAME_NOT_FOUND, username), message);
    }

    /**
     * test for addNewUser(String, String)
     *
     * @see UserService#addNewUser(String, String)
     */
    @Test
    public void testAddNewUser_nullUsername() throws Throwable {
        // Given
        String username = null;
        String password = "password";

        // When
        String message = "";
        try {
            @SuppressWarnings("unused")
            UserEntity result = underTest.addNewUser(username, password);
        } catch (NullPointerException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(UserMessages.USERNAME_CANNOT_NULL, message);
    }

    /**
     * test for addNewUser(String, String)
     *
     * @see UserService#addNewUser(String, String)
     */
    @Test
    public void testAddNewUser_nullPassword() throws Throwable {
        // Given
        String username = "username";
        String password = null;

        // When
        String message = "";
        try {
            @SuppressWarnings("unused")
            UserEntity result = underTest.addNewUser(username, password);
        } catch (NullPointerException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(UserMessages.PASSWORD_CANNOT_NULL, message);
    }

    /**
     * test for addNewUser(String, String)
     *
     * @see UserService#addNewUser(String, String)
     */
    @Test
    public void testAddNewUser_userExistsAlready() throws Throwable {
        // Given
        String username = "username";
        String password = "password";
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When
        String message = "";
        try {
            @SuppressWarnings("unused")
            UserEntity result = underTest.addNewUser(username, password);
        } catch (UsernameExistsAlreadyException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(UserMessages.USERNAME_EXISTS_ALREADY, username), message);
    }

    /**
     * test for addNewUser(String, String)
     *
     * @see UserService#addNewUser(String, String)
     */
    @Test
    public void testAddNewUser_normal() throws Throwable {
        // Given
        String username = "username";
        String password = "password";

        UserEntity saveResult = new UserEntity(username, password);
        doReturn(saveResult).when(userRepository).save((UserEntity) any());
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(password);

        // When
        UserEntity result = underTest.addNewUser(username, password);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(0, result.getAuthorities().size());
    }

    /**
     * test for updateUser(UserEntity)
     *
     * @see UserService#updateUser(UserEntity)
     */
    @Test
    public void testUpdateUser_normal() throws Throwable {
        // Given
        String username = "username";
        String password = "password";

        UserEntity saveResult = new UserEntity(username, password);
        doReturn(saveResult).when(userRepository).save((UserEntity) any());
        doReturn(true).when(userRepository).existsById(anyLong());

        // When
        UserEntity user = new UserEntity();
        user.setId(1L);
        UserEntity result = underTest.updateUser(user);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(0, result.getAuthorities().size());
    }

    /**
     * test for updateUser(UserEntity)
     *
     * @see UserService#updateUser(UserEntity)
     */
    @Test
    public void testUpdateUser_nullUser() throws Throwable {
        // Given
        UserEntity user = null;

        // When
        String message = "";
        try {
            @SuppressWarnings("unused")
            UserEntity result = underTest.updateUser(user);
        } catch (NullPointerException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(UserMessages.USER_CANNOT_NULL, message);
    }

    /**
     * test for updateUser(UserEntity)
     *
     * @see UserService#updateUser(UserEntity)
     */
    @Test
    public void testUpdateUser_nullUserId() throws Throwable {
        // Given
        String username = "username";
        String password = "password";

        UserEntity user = new UserEntity(username, password);
        user.setId(null);

        // When
        String message = "";
        try {
            @SuppressWarnings("unused")
            UserEntity result = underTest.updateUser(user);
        } catch (UserNotFoundException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(UserMessages.USER_ID_NOT_FOUND, "null"), message);
    }

    /**
     * test for updateUser(UserEntity)
     *
     * @see UserService#updateUser(UserEntity)
     */
    @Test
    public void testUpdateUser_userIdNotExists() throws Throwable {
        // Given
        doReturn(false).when(userRepository).existsById(anyLong());

        // When
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        String message = "";
        try {
            underTest.updateUser(user);
        } catch (UserNotFoundException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(UserMessages.USER_ID_NOT_FOUND, userId), message);
    }


    /**
     * test for removeUser(Long)
     *
     * @see UserService#removeUser(Long)
     */
    @Test
    public void testRemoveUser_normal() throws Throwable {
        // Given
        doReturn(true).when(userRepository).existsById(anyLong());
        doAnswer(invocation -> null).when(userRepository).deleteById(nullable(Long.class));

        // When
        Long id = 0L;
        underTest.removeUser(id);
    }

    /**
     * test for removeUser(Long)
     *
     * @see UserService#removeUser(Long)
     */
    @Test
    public void testRemoveUser_userIdNotExists() throws Throwable {
        // Given
        doReturn(false).when(userRepository).existsById(anyLong());

        // When
        Long id = -1L;
        String message = "";
        try {
            underTest.removeUser(id);
        } catch (UserNotFoundException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(UserMessages.USER_ID_NOT_FOUND, id), message);
    }

    /**
     * test for removeUser(Long)
     *
     * @see UserService#removeUser(Long)
     */
    @Test
    public void testRemoveUser_nullUserId() throws Throwable {
        // Given
        Long id = null;

        // When
        String message = "";
        try {
            underTest.removeUser(id);
        } catch (NullPointerException e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(MessageFormat.format(UserMessages.USER_ID_CANNOT_NULL, id), message);
    }

    /**
     * test for getFirstUserByRoleName(String)
     *
     * @see UserService#getFirstUserByRoleName(String)
     */
    @Test
    public void testGetFirstUserByRoleName_normal() throws Throwable {
        // Given
        UserEntity user = mock(UserEntity.class);
        doReturn(user).when(userRepository).getFirstByRole_Name(anyString());

        // When
        UserEntity result = underTest.getFirstUserByRoleName("testParam");

        // Then
        assertNotNull(result);
        assertEquals(user, result);
    }

    /**
     * test for getFirstUserByRoleName(String)
     *
     * @see UserService#getFirstUserByRoleName(String)
     */
    @Test( expected = UserNotFoundException.class)
    public void testGetFirstUserByRoleName_UserNotFoundException() throws Throwable {
        // Given
        doReturn(null).when(userRepository).getFirstByRole_Name(anyString());

        // When
        UserEntity result = underTest.getFirstUserByRoleName("testParam");
    }


}