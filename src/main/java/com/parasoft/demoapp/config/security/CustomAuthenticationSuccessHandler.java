package com.parasoft.demoapp.config.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.parasoft.demoapp.util.SessionUtil;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.UserEntity;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)authentication.getPrincipal();

		//When logging in successfully, the user's role would be stored in session for checking the role in other page
		RoleEntity authority = user.getRole();
		String roleName = authority.getName();	// value is like ROLE_APPROVER
		session.setAttribute(SessionUtil.FULL_ROLE_NAME_KEY, roleName);
		roleName = roleName.substring(roleName.lastIndexOf("_") + 1).toLowerCase();	//value is like APPROVER
		session.setAttribute(SessionUtil.ROLE_NAME_KEY, roleName);
		response.sendRedirect("/");
	}

}
