package com.parasoft.demoapp.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomOAuth2AuthenticationFailureHandler  implements AuthenticationFailureHandler {

    @Value("${spring.security.oauth2.client.provider.keycloak.end-session-endpoint}")
    private String keycloakEndSessionEndpoint;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String currentDomainName = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        if (exception instanceof OAuth2AuthenticationException) {
            String idTokenHint = ((OAuth2AuthenticationException) exception).getError().getDescription();
            response.sendRedirect(keycloakEndSessionEndpoint +
                    "?post_logout_redirect_uri=" + currentDomainName + "/conflict?type=keycloak_role&id_token_hint=" + idTokenHint);
        }

        if (exception instanceof UsernameNotFoundException) {
            String idTokenHint = exception.getMessage();
            response.sendRedirect(keycloakEndSessionEndpoint +
                    "?post_logout_redirect_uri=" + currentDomainName + "/conflict?type=keycloak_user&id_token_hint=" + idTokenHint);
        }
    }
}