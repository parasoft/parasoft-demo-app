package com.parasoft.demoapp.dto;


import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.UserEntity;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Test for ForgotPasswordResponseDTO
 *
 * @see ForgotPasswordResponseDTO
 * @author tren
 */
public class ForgotPasswordResponseDTOTest {
    public UserEntity getTestUser() {
        RoleEntity role = new RoleEntity("role");
        return new UserEntity("test_username", "test_password", role);
    }

    /**
     * Test for getInstanceFrom(UserEntity)
     *
     * @author tren
     * @see ForgotPasswordResponseDTO#getInstanceFrom(UserEntity)
     */
    @Test
    public void testGetInstanceFrom() throws Throwable {
        // When
        UserEntity user = getTestUser();
        ForgotPasswordResponseDTO result = ForgotPasswordResponseDTO.getInstanceFrom(user);

        // Then
        ForgotPasswordResponseDTO.PrimaryUserInfo userInfo = result.getPrimaryUserInfo();
        assertEquals(result.isHasPrimaryUser(), true);
        assertEquals(result.getRoleName(), user.getRole().getName());
        assertNotNull(userInfo);
        assertEquals(userInfo.getUserName(), user.getUsername());
        assertEquals(userInfo.getPassword(), user.getPassword());

    }

    /**
     * Test for getInstance(String)
     *
     * @author tren
     * @see ForgotPasswordResponseDTO#getInstance(String)
     */
    @Test(timeout = 1000)
    public void testGetInstance() throws Throwable {
        // When
        String roleName = "role";
        ForgotPasswordResponseDTO result = ForgotPasswordResponseDTO.getInstance(roleName);

        // Then
        assertNull(result.getPrimaryUserInfo());
        assertEquals(result.isHasPrimaryUser(), false);
        assertEquals(result.getRoleName(), roleName);
    }
}