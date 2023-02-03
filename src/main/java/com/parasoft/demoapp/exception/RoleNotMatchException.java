package com.parasoft.demoapp.exception;

import org.springframework.security.core.AuthenticationException;

public class RoleNotMatchException extends AuthenticationException {

    private static final long serialVersionUID = 3136490972980184930L;


    public RoleNotMatchException(String msg, Throwable t) {
        super(msg, t);
    }

    public RoleNotMatchException(String msg) {
        super(msg);
    }
}
