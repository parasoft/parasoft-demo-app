package com.parasoft.demoapp.controller;


import com.parasoft.demoapp.dto.ForgotPasswordResponseDTO;
import com.parasoft.demoapp.exception.UserNotFoundException;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Test for ForgotPasswordController
 *
 * @see ForgotPasswordController
 */
public class ForgotPasswordControllerTest {

    @InjectMocks
    ForgotPasswordController underTest;

    @Mock
    UserService userService;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for forgotPassword()
     *
     * @see ForgotPasswordController#forgotPassword()
     */
    @Test
    public void testForgotPassword_normal() throws Throwable {
        // Given
        RoleEntity role = mock(RoleEntity.class);
        UserEntity user = new UserEntity("test_username", "test_password", role);

        doReturn(user).when(userService).getFirstUserByRoleName(anyString());

        // When
        ResponseResult<List<ForgotPasswordResponseDTO>> result = underTest.forgotPassword();

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(result.getData().size(), RoleType.values().length);
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    /**
     * Test for forgotPassword()
     *
     * @see ForgotPasswordController#forgotPassword()
     */
    @Test
    public void testForgotPassword_UserNotFoundException() throws Throwable {
        // Given
        RoleEntity role = mock(RoleEntity.class);

        given(userService.getFirstUserByRoleName(anyString())).willThrow(new UserNotFoundException(""));

        // When
        ResponseResult<List<ForgotPasswordResponseDTO>> result = underTest.forgotPassword();

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(result.getData().size(), RoleType.values().length);
        for(ForgotPasswordResponseDTO item : result.getData()) {
            assertNull(item.getPrimaryUserInfo());
            assertFalse(item.isHasPrimaryUser());
        }
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }
}