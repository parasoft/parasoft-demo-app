package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.exception.CannotLogoutFromKeycloakException;
import com.parasoft.demoapp.service.KeycloakService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.parasoft.demoapp.exception.RoleNotMatchException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomOAuth2AuthenticationFailureHandler  implements AuthenticationFailureHandler {

    @Autowired
    KeycloakService keycloakService;

    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {


        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException error = (OAuth2AuthenticationException) exception;
            String errorCode = "invalid_token_response";
            if (errorCode.equals(error.getError().getErrorCode())) {
                response.sendRedirect("/unauthorized?type=client_error");
                return;
            }
        }

        String idTokenHint = exception.getMessage();
        signOutFromKeycloak(idTokenHint);

        if (exception instanceof RoleNotMatchException) {
            response.sendRedirect("/accessDenied?type=keycloak_role");
            return;
        }

        if (exception instanceof UsernameNotFoundException) {
            response.sendRedirect("/unauthorized?type=keycloak_user");
            return;
        }
    }

    private void signOutFromKeycloak(String idTokenHint) throws IOException, CannotLogoutFromKeycloakException {
        if (idTokenHint != null) {
            keycloakService.oauth2Logout(idTokenHint);
        }
    }
}