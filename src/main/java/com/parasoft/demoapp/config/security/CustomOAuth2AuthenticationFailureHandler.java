package com.parasoft.demoapp.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class CustomOAuth2AuthenticationFailureHandler  implements AuthenticationFailureHandler {

    @Value("${spring.security.oauth2.client.provider.keycloak.end-session-endpoint}")
    private String keycloakEndSessionEndpoint;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String currentDomainName = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        if (exception instanceof OAuth2AuthenticationException) {
            String idTokenHint = ((OAuth2AuthenticationException) exception).getError().getDescription();
            try {
                restTemplate.getForObject(keycloakEndSessionEndpoint + "?id_token_hint={id_token_hint}",
                        String.class,
                        Collections.singletonMap("id_token_hint", idTokenHint));
            } catch (RestClientException e) {
                log.error("Tried to sign out the session from Keycloak but failed, Keycloak server is not available.", e);
            }

            response.sendRedirect(currentDomainName + "/conflict?type=keycloak_role");
        }

        if (exception instanceof UsernameNotFoundException) {
            String idTokenHint = exception.getMessage();
            try {
                restTemplate.getForObject(keycloakEndSessionEndpoint + "?id_token_hint={id_token_hint}",
                        String.class,
                        Collections.singletonMap("id_token_hint", idTokenHint));
            } catch (RestClientException e) {
                log.error("Tried to sign out the session from Keycloak but failed, Keycloak server is not available.", e);
            }

            response.sendRedirect(currentDomainName + "/conflict?type=keycloak_user");
        }
    }
}