package com.parasoft.demoapp.config;

import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.parasoft.demoapp.messages.ConfigMessages;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {
	private final ConfigMessages configMessages = new ConfigMessages();

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
                                                .flows(new OAuthFlows()
                                                            .authorizationCode(new OAuthFlow().tokenUrl(tokenUri)
                                                            .authorizationUrl(authorizationUri)
                                                            .scopes(new Scopes())
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

    @Bean
    public SchemaPropertyCustomizer schemaPropertyCustomizer(GlobalPreferencesService globalPreferencesService, SpringDocConfigProperties springDocConfigProperties) {
        return new SchemaPropertyCustomizer(globalPreferencesService, springDocConfigProperties);
    }

    public static class SchemaPropertyCustomizer implements PropertyCustomizer {
        private final GlobalPreferencesService globalPreferencesService;
        private final SpringDocConfigProperties springDocConfigProperties;

        public SchemaPropertyCustomizer(GlobalPreferencesService globalPreferencesService, SpringDocConfigProperties springDocConfigProperties) {
            this.globalPreferencesService = globalPreferencesService;
            this.springDocConfigProperties = springDocConfigProperties;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Schema<?> customize(Schema schema, AnnotatedType type) {
            springDocConfigProperties.getCache().setDisabled(false); // Enable cache to avoid reloading the schema

            if (isRegionType(type)) {
                IndustryType currentIndustry;
                try {
                    currentIndustry = globalPreferencesService.getCurrentIndustry();
                } catch (GlobalPreferencesNotFoundException | GlobalPreferencesMoreThanOneException e) {
                    // Will not reach here if project is started up successfully
                    throw new RuntimeException(e);
                }
                List<String> regionsForCurrentIndustry = new ArrayList<>();
                List<String> regionsForOtherIndustries = new ArrayList<>();
                for(RegionType regionType : RegionType.values()) {
                    if(regionType.getIndustryType() == currentIndustry) {
                        regionsForCurrentIndustry.add(regionType.name());
                    } else {
                        regionsForOtherIndustries.add(regionType.name());
                    }
                }
                List<String> reorderedRegions = new ArrayList<>(regionsForCurrentIndustry);
                reorderedRegions.addAll(regionsForOtherIndustries);
                schema.setEnum(reorderedRegions);
            }
            return schema;
        }

        private boolean isRegionType(AnnotatedType type) {
            return type != null &&
                   type.getType() != null &&
                   type.getType().getTypeName() != null &&
                   type.getType().getTypeName().equals("[simple type, class " + RegionType.class.getCanonicalName() + "]");
        }

        public void onIndustryChange() {
            springDocConfigProperties.getCache().setDisabled(true); // Disable cache to force reloading the schema
        }
    }
}
