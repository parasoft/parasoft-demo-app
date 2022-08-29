package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.util.HttpServletResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HttpServletResponseUtil.returnJsonResponse(response, HttpStatus.UNAUTHORIZED.value(),
                ResponseResult.STATUS_ERR, ConfigMessages.USER_IS_NOT_AUTHORIZED, null);
    }
}
