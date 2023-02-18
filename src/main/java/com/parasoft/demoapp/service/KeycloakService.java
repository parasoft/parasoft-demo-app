package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.CannotLogoutFromKeycloakException;
import com.parasoft.demoapp.messages.ConfigMessages;
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

    public void oauth2Logout(String idToken) throws CannotLogoutFromKeycloakException {
        try {
            restTemplate.getForObject(keycloakEndSessionEndpoint + "?id_token_hint={id_token_hint}",
                    String.class,
                    Collections.singletonMap("id_token_hint", idToken));
        } catch (RestClientException e) {
            throw new CannotLogoutFromKeycloakException(ConfigMessages.KEYCLOAK_SING_OUT_FAILED, e);
        }
    }
}
