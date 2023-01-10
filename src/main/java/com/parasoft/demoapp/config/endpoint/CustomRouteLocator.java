package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.RestEndpointService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;

@Slf4j
public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    private final RestEndpointService restEndpointService;

    private final GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService;

    private final GlobalPreferencesService globalPreferencesService;

    public CustomRouteLocator(String servletPath, ZuulProperties properties, RestEndpointService restEndpointService,
                              GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService,
                              GlobalPreferencesService globalPreferencesService) {

        super(servletPath, properties);
        this.restEndpointService = restEndpointService;
        this.defaultGlobalPreferencesSettingsService = defaultGlobalPreferencesSettingsService;
        this.globalPreferencesService = globalPreferencesService;
    }

    @Override
    public void refresh() {
        doRefresh();
        Map<String, String> routeRestEndpointsMap = getRoutesMap().entrySet().stream()
                .filter(entry -> entry.getValue() != null && REST_ENDPOINT_IDS.contains(entry.getValue().getId()))
                .collect(Collectors.toMap(entry -> entry.getValue().getId(), entry -> entry.getValue().getUrl()));
        restEndpointService.refreshRouteRestEndpointsSnapshot(routeRestEndpointsMap);
    }

    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>(getPersistentRoutes());
        fullFillDefaultRoutes(routesMap);

        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for(Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            // Prepend with slash if not already present.
            if(!path.startsWith("/")) {
                path = "/" + path;
            }
            values.put(path, entry.getValue());
        }

        StringBuilder sb = new StringBuilder();
        for(String key: values.keySet()) {
            sb.append("\n")
              .append(values.get(key).getPath())
              .append(" ---> ")
              .append(values.get(key).getUrl());
        }
        log.info("Endpoints routes:" + sb);

        return values;
    }

    @SneakyThrows
    private Map<String, ZuulProperties.ZuulRoute> getPersistentRoutes() {
        // RestEndpointEntity is a copy of ZuulProperties.ZuulRoute, they are saved in database
        List<RestEndpointEntity> results = restEndpointService.getAllEndpoints();

        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        for (RestEndpointEntity result : results) {
            if(StringUtils.isEmpty(result.getPath())) {
                continue;
            }
            if(StringUtils.isEmpty(result.getServiceId()) && StringUtils.isEmpty(result.getUrl())) {
                continue;
            }
            ZuulProperties.ZuulRoute zuulRoute = result.toRealZuulRoute();

            routes.put(zuulRoute.getPath(), zuulRoute);
        }

        // Handle graphql endpoint
        String graphQLEndpoint = globalPreferencesService.getCurrentGlobalPreferences().getGraphQLEndpoint();
        if(!graphQLEndpoint.isEmpty()) {
            Set<String> sensitiveHeaders = new HashSet<>();
            routes.put(GRAPHQL_ENDPOINT_PATH,
                    new ZuulProperties.ZuulRoute(
                            GRAPHQL_ENDPOINT_ID, GRAPHQL_ENDPOINT_PATH, null,
                            graphQLEndpoint, true, false, sensitiveHeaders));
        }

        return routes;
    }

    private void fullFillDefaultRoutes(
            LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap){

        if(routesMap.get(CATEGORIES_ENDPOINT_PATH) == null){
            routesMap.put(CATEGORIES_ENDPOINT_PATH,
                    defaultGlobalPreferencesSettingsService.defaultCategoriesEndpoint().toRealZuulRoute());
        }

        if(routesMap.get(ITEMS_ENDPOINT_PATH) == null){
            routesMap.put(ITEMS_ENDPOINT_PATH,
                    defaultGlobalPreferencesSettingsService.defaultItemsEndpoint().toRealZuulRoute());
        }

        if(routesMap.get(CART_ENDPOINT_PATH) == null){
            routesMap.put(CART_ENDPOINT_PATH,
                    defaultGlobalPreferencesSettingsService.defaultCartItemsEndpoint().toRealZuulRoute());
        }

        if(routesMap.get(ORDERS_ENDPOINT_PATH) == null){
            routesMap.put(ORDERS_ENDPOINT_PATH,
                    defaultGlobalPreferencesSettingsService.defaultOrdersEndpoint().toRealZuulRoute());
        }

        if(routesMap.get(LOCATIONS_ENDPOINT_PATH) == null){
            routesMap.put(LOCATIONS_ENDPOINT_PATH,
                    defaultGlobalPreferencesSettingsService.defaultLocationsEndpoint().toRealZuulRoute());
        }

        if(routesMap.get(GRAPHQL_ENDPOINT_PATH) == null){
            Set<String> sensitiveHeaders = new HashSet<>();
            routesMap.put(GRAPHQL_ENDPOINT_PATH,
                    new ZuulProperties.ZuulRoute(
                            GRAPHQL_ENDPOINT_ID, GRAPHQL_ENDPOINT_PATH, null,
                            defaultGlobalPreferencesSettingsService.defaultGraphQLEndpoint(),
                            true, false, sensitiveHeaders));
        }
    }
}
