package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.RestEndpointService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class GatewayMvcConfig {

    @Bean
    public DynamicGatewayRoutes gatewayRoutes(RestEndpointService restEndpointService,
                                              GlobalPreferencesDefaultSettingsService defaultSettingsService,
                                              GlobalPreferencesService globalPreferencesService) {
        return new DynamicGatewayRoutes(restEndpointService, defaultSettingsService, globalPreferencesService);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void refreshRoutesWhenApplicationIsReady(ApplicationReadyEvent event) {
        event.getApplicationContext().getBean(DynamicGatewayRoutes.class).refresh();
    }
}
