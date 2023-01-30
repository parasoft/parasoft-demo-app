package com.parasoft.demoapp.util;

import com.parasoft.demoapp.config.security.SecurityConfig;
import com.parasoft.demoapp.model.global.UserEntity;
import org.springframework.security.core.Authentication;

public class AuthenticationUtil {

    /**
     * to get current user id
     * @param auth
     * @return user id
     */
    public static Long getUserIdInAuthentication(Authentication auth) {
        if(auth == null){
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserEntity) {
            return ((UserEntity) principal).getId();
        } else if (principal instanceof SecurityConfig.CustomJwt) {
            return ((SecurityConfig.CustomJwt) principal).getUserInfo().getId();
        }

        return null;
    }

    /**
     * to get current user role type
     * @param auth
     * @return user role type
     */
    public static String getUserRoleNameInAuthentication(Authentication auth) {
        if(auth == null){
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserEntity) {
            return ((UserEntity) principal).getRole().getName();
        } else if (principal instanceof SecurityConfig.CustomJwt) {
            return ((SecurityConfig.CustomJwt) principal).getUserInfo().getRole().getName();
        }

        return null;
    }

    /**
     * To get current username
     * @param auth
     * @return username
     */
    public static String getUsernameInAuthentication(Authentication auth) {
        if(auth == null){
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserEntity) {
            return ((UserEntity) principal).getUsername();
        } else if (principal instanceof SecurityConfig.CustomJwt) {
            return ((SecurityConfig.CustomJwt) principal).getUserInfo().getUsername();
        }

        return null;
    }
}
