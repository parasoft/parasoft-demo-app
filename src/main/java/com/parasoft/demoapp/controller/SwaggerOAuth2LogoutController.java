package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.CategoryNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Controller
public class SwaggerOAuth2LogoutController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.end-session-endpoint}")
    private String keycloakEndSessionEndpoint;

    @ResponseBody
    @RequestMapping(value = {"/swaggerOAuth2Logout"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout(@RequestBody String idToken)
            throws CategoryNotFoundException, ParameterException {
            try {
                restTemplate.getForObject(keycloakEndSessionEndpoint + "?id_token_hint={id_token_hint}",
                        String.class,
                        Collections.singletonMap("id_token_hint", idToken));
            } catch (RestClientException e) {
                log.error("Tried to sign out the session from Keycloak but failed, Keycloak server is not available.", e);
            }
    }
}
