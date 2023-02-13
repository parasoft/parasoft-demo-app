package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.CannotLogoutFromKeycloakException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class KeycloakService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.end-session-endpoint}")
    private String keycloakEndSessionEndpoint;

    public String oauth2Logout(String idToken) throws CannotLogoutFromKeycloakException {

        try {
            restTemplate.getForObject(keycloakEndSessionEndpoint + "?id_token_hint={id_token_hint}",
                    String.class,
                    Collections.singletonMap("id_token_hint", idToken));
        } catch (RestClientException e) {
            String massage = "Tried to sign out the session from Keycloak but failed, Keycloak server is not available.";
            throw new CannotLogoutFromKeycloakException(massage);
        }
        return "Logout";
    }
}
