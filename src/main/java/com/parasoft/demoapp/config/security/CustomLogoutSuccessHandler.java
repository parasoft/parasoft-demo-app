package com.parasoft.demoapp.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler{

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.end_session_endpoint}")
    private String keycloakEndSessionEndpoint;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            CustomOidcUser user = (CustomOidcUser) authentication.getPrincipal();
            String idTokenValue = user.getIdToken().getTokenValue();
            try {
                restTemplate.getForObject(keycloakEndSessionEndpoint + "?id_token_hint={id_token_hint}",
                        String.class,
                        Collections.singletonMap("id_token_hint", idTokenValue));
            } catch (RestClientException e) {
                log.error("Tried to sign out the session from Keycloak but failed, Keycloak server is not available.", e);
            }
        }
        response.sendRedirect("/loginPage");
    }
}
