package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.exception.RoleNotMatchException;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.util.HttpServletResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomBearerTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final BearerTokenAuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException)
            throws IOException {

        delegate.commence(req, resp, authException);

        if (authException instanceof RoleNotMatchException) {
            HttpServletResponseUtil.returnJsonResponse(resp, HttpStatus.FORBIDDEN.value(),
                    ResponseResult.STATUS_ERR, ConfigMessages.ROLE_NOT_MATCH, authException.getMessage());
            return;
        }

        if (authException instanceof UsernameNotFoundException) {
            HttpServletResponseUtil.returnJsonResponse(resp, HttpStatus.UNAUTHORIZED.value(),
                    ResponseResult.STATUS_ERR, ConfigMessages.USERNAME_NOT_AVAILABLE, authException.getMessage());
            return;
        }

        HttpServletResponseUtil.returnJsonResponse(resp, HttpStatus.UNAUTHORIZED.value(),
                ResponseResult.STATUS_ERR, ConfigMessages.USER_IS_NOT_AUTHORIZED, authException.getMessage());
    }
}