package com.parasoft.demoapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
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
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("basic")
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
