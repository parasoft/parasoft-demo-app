package com.parasoft.demoapp.util;

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

        UserEntity user = (UserEntity) auth.getPrincipal();

        if(user == null){
            return null;
        }

        return user.getId();
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

        UserEntity user = (UserEntity) auth.getPrincipal();

        if(user == null || user.getRole() == null){
            return null;
        }

        return user.getRole().getName();
    }
}
