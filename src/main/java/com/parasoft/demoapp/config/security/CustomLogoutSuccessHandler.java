package com.parasoft.demoapp.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler{

    private final OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler;

    public CustomLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        this.oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        this.oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/loginPage");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            oidcLogoutSuccessHandler.onLogoutSuccess(request, response, authentication);
            return;
        }
        response.sendRedirect("/loginPage");
    }
}
