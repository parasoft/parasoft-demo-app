package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.util.HttpServletResponseUtil;
import com.parasoft.demoapp.util.UrlUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException)
            throws IOException {

        if (authException instanceof InsufficientAuthenticationException) {
            if(!isRestfulApiRequest(req)){
                resp.sendRedirect("/loginPage");
            }else{
                HttpServletResponseUtil.returnJsonErrorResponse(resp, HttpStatus.FORBIDDEN.value(),
                        ResponseResult.STATUS_ERR, ConfigMessages.USER_HAS_NO_PERMISSION, authException.getMessage());
            }
        }
    }

    public boolean isRestfulApiRequest(HttpServletRequest req) {
        return UrlUtil.isRestfulApiRequest(req);
    }
}