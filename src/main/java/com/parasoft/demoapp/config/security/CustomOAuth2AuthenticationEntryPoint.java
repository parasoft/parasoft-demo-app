package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.util.HttpServletResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomOAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException)
            throws IOException {
        HttpServletResponseUtil.returnJsonResponse(resp, HttpStatus.UNAUTHORIZED.value(),
                ResponseResult.STATUS_ERR, ConfigMessages.USER_IS_NOT_AUTHORIZED, authException.getMessage());
    }
}