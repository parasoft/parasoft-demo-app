package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CustomAuthenticationSuccessHandler
 *
 * @see com.parasoft.demoapp.config.security.CustomAuthenticationSuccessHandler
 */
class CustomAuthenticationSuccessHandlerTest {

    CustomAuthenticationSuccessHandler underTest = new CustomAuthenticationSuccessHandler();

    MockHttpServletRequest httpServletRequest;

    MockHttpServletResponse httpServletResponse;

    Authentication authentication;

    UserEntity principal;

    MockHttpSession httpSession = new MockHttpSession();

    /**
     * Test for onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     * <br/>
     * Purchaser is not allowed to log in on Android device.
     *
     * @see com.parasoft.demoapp.config.security.CustomAuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     */
    @Test
    void onAuthenticationSuccess_loginWithPurchaserUserOnAndroidDevice() throws IOException {
        // Given
        httpServletRequest = new MockHttpServletRequest();
        httpServletResponse = new MockHttpServletResponse();
        httpServletRequest.setSession(httpSession);

        // Purchaser is not allow to log in on Android device
        principal = new UserEntity("username", "password", new RoleEntity(RoleType.ROLE_PURCHASER.name()));
        authentication = new TestingAuthenticationToken(principal, null);
        // User-Agent can be used to confirm the request is from Android device
        httpServletRequest.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 13; sdk_gphone64_x86_64 Build/TPB4.220624.004)");

        // When
        underTest.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // Then
        assertTrue(httpSession.isInvalid());
        assertEquals("{\"status\":0,\"message\":\"Current user is not authorized.\"}", httpServletResponse.getContentAsString());
        assertEquals(401, httpServletResponse.getStatus());
    }

    /**
     * Test for onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     * <br/>
     * Approver is allowed to log in on Android device.
     *
     * @see com.parasoft.demoapp.config.security.CustomAuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     */
    @Test
    void onAuthenticationSuccess_loginWithApproverUserOnAndroidDevice() throws IOException {
        // Given
        httpServletRequest = new MockHttpServletRequest();
        httpServletResponse = new MockHttpServletResponse();
        httpServletRequest.setSession(httpSession);

        // Approver is allowed to log in on Android device
        principal = new UserEntity("username", "password", new RoleEntity(RoleType.ROLE_APPROVER.name()));
        authentication = new TestingAuthenticationToken(principal, null);
        // User-Agent can be used to confirm the request is from Android device
        httpServletRequest.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 13; sdk_gphone64_x86_64 Build/TPB4.220624.004)");

        // When
        underTest.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // Then
        assertFalse(httpSession.isInvalid());
        assertEquals("{\"status\":1,\"message\":\"Login successfully.\"}", httpServletResponse.getContentAsString());
        assertEquals(200, httpServletResponse.getStatus());
        assertEquals(principal.getRole().getName(), httpSession.getAttribute(SessionUtil.FULL_ROLE_NAME_KEY));
        assertEquals("approver", httpSession.getAttribute(SessionUtil.ROLE_NAME_KEY));
    }

    /**
     * Test for onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     * <br/>
     * Purchaser is allowed to log in on non-Android device.
     *
     * @see com.parasoft.demoapp.config.security.CustomAuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     */
    @Test
    void onAuthenticationSuccess_loginWithPurchaserUserOnNonAndroidDevice() throws IOException {
        // Given
        httpServletRequest = new MockHttpServletRequest();
        httpServletResponse = new MockHttpServletResponse();
        httpServletRequest.setSession(httpSession);
        principal = new UserEntity("username", "password", new RoleEntity(RoleType.ROLE_PURCHASER.name()));
        authentication = new TestingAuthenticationToken(principal, null);
        // Non-android device
        httpServletRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");

        // When
        underTest.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // Then
        assertFalse(httpSession.isInvalid());
        assertEquals("{\"status\":1,\"message\":\"Login successfully.\"}", httpServletResponse.getContentAsString());
        assertEquals(200, httpServletResponse.getStatus());
        assertEquals(principal.getRole().getName(), httpSession.getAttribute(SessionUtil.FULL_ROLE_NAME_KEY));
        assertEquals("purchaser", httpSession.getAttribute(SessionUtil.ROLE_NAME_KEY));
    }

    /**
     * Test for onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     * <br/>
     * Approver is allowed to log in on non-Android device.
     *
     * @see com.parasoft.demoapp.config.security.CustomAuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     */
    @Test
    void onAuthenticationSuccess_loginWithApproverUserOnNonAndroidDevice() throws IOException {
        // Given
        httpServletRequest = new MockHttpServletRequest();
        httpServletResponse = new MockHttpServletResponse();
        httpServletRequest.setSession(httpSession);

        // Approver is allowed to log in on Android device
        principal = new UserEntity("username", "password", new RoleEntity(RoleType.ROLE_APPROVER.name()));
        authentication = new TestingAuthenticationToken(principal, null);
        // Non-android device
        httpServletRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");

        // When
        underTest.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

        // Then
        assertFalse(httpSession.isInvalid());
        assertEquals("{\"status\":1,\"message\":\"Login successfully.\"}", httpServletResponse.getContentAsString());
        assertEquals(200, httpServletResponse.getStatus());
        assertEquals(principal.getRole().getName(), httpSession.getAttribute(SessionUtil.FULL_ROLE_NAME_KEY));
        assertEquals("approver", httpSession.getAttribute(SessionUtil.ROLE_NAME_KEY));
    }
}