package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.RestEndpointService;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@EnableZuulProxy
public class ZuulConfig {

    @Bean
    @DependsOn("defaultDataInitialization")
    public CustomRouteLocator routeLocator(RestEndpointService restEndpointService,
                                           GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService) {

        return new CustomRouteLocator("/", new ZuulProperties(), restEndpointService,
                                        defaultGlobalPreferencesSettingsService);
    }
}
