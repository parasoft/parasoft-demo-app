package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.messages.ConfigMessages;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.util.HttpServletResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity)authentication.getPrincipal();

        RoleEntity authority = user.getRole();
        String roleName = authority.getName();	// value is like ROLE_APPROVER

        String agent = request.getHeader("User-Agent");
        // Android user who has purchaser role can not use this application
        if(RoleType.ROLE_PURCHASER.name().equals(roleName) && agent.contains("Android")) {
            HttpServletResponseUtil.returnJsonResponse(response, HttpStatus.UNAUTHORIZED.value(),
                    ResponseResult.STATUS_ERR, ConfigMessages.USER_IS_NOT_AUTHORIZED, null);
            session.invalidate();
            return;
        }

        HttpServletResponseUtil.returnJsonResponse(response, HttpStatus.OK.value(),
                ResponseResult.STATUS_OK, ConfigMessages.LOGIN_SUCCESSFULLY, null);
    }

}
