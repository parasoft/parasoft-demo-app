package com.parasoft.demoapp.util;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import com.parasoft.demoapp.model.global.UserEntity;


public class SessionUtil {
	
	private static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
	public static final String FULL_ROLE_NAME_KEY = "FULL_ROLE_NAME";
	public static final String ROLE_NAME_KEY = "ROLE_NAME";
	
	 /**
	   * Get user entity from HttpSession.
	   * If there is no user entity, this function will return null.
	   * 
	   * @param session
	   * @return UserEntity or null
	   */
	public static UserEntity getUserEntityInSession(HttpSession session) {
	    if (session == null) {
	      return null;
	    }

	    Object obj = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
	    if (obj == null) {
	      return null;
	    }

	    SecurityContext securityContext = (SecurityContext) obj;
	    Authentication authentication = securityContext.getAuthentication();
	    Object principal = null;
	    if (authentication == null || (principal = authentication.getPrincipal()) == null) {
	      return null;
	    }

	    UserEntity userInSession = (UserEntity) principal;
	    return userInSession;
	}
}
