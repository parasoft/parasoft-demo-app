package com.parasoft.demoapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.parasoft.demoapp.messages.ConfigMessages;

@Configuration
public class OpenApiConfig {
	private ConfigMessages configMessages = new ConfigMessages();

    private static final String MODULE_NAME = "Parasoft Demo App";
    private static final String API_VERSION = "1.0.0";
    private static final String API_TITLE = String.format("%s REST API", MODULE_NAME);

    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}") String authorizationUri,
                                 @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}") String tokenUri) {
        final String basicSchemeName = "basicAuth";
        final String oAuthSchemeName = "oAuth2AuthCode";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(basicSchemeName).addList(oAuthSchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(basicSchemeName,
                                        new SecurityScheme()
                                                .name(basicSchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("basic")
                                )
                                .addSecuritySchemes(oAuthSchemeName,
                                        new SecurityScheme()
                                                .name(oAuthSchemeName)
                                                .type(SecurityScheme.Type.OAUTH2)
                                                .flows(new OAuthFlows().authorizationCode(
                                                        new OAuthFlow().tokenUrl(tokenUri)
                                                                .authorizationUrl(authorizationUri)
                                                ))
                                )
                )
                .info(new Info().title(API_TITLE).version(API_VERSION)
                		.description(configMessages.getString(ConfigMessages.GENERAL_API_DESCRIPTION)));
    }

    @Bean
    public GroupedOpenApi regularRestApiOfV1() {
        return GroupedOpenApi.builder()
                .pathsToMatch("/v1/**")
                .group("v1")
                .addOpenApiCustomiser(new OpenApiCustomiser() {

					@Override
					public void customise(OpenAPI openApi) {
						openApi.info(new Info().title(API_TITLE).version(API_VERSION)
								.description(configMessages.getString(ConfigMessages.REGULAR_API_DESCRIPTION)));
					}
                	
                })
                .build();
    }

    @Bean
    public GroupedOpenApi proxyRestApiOfV1() {
        return GroupedOpenApi.builder()
                .pathsToMatch("/proxy/v1/**")
                .group("v1-proxy")
                .addOpenApiCustomiser(new OpenApiCustomiser() {

					@Override
					public void customise(OpenAPI openApi) {
						openApi.info(new Info().title(API_TITLE).version(API_VERSION)
								.description(configMessages.getString(ConfigMessages.GATEWAY_API_DESCRIPTION)));
					}
                	
                })
                .build();
    }

}
