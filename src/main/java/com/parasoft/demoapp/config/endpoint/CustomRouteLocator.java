package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.RestEndpointService;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;

public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    private RestEndpointService restEndpointService;

    private GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService;

    public CustomRouteLocator(String servletPath, ZuulProperties properties, RestEndpointService restEndpointService,
                              GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService) {

        super(servletPath, properties);
        this.restEndpointService = restEndpointService;
        this.defaultGlobalPreferencesSettingsService = defaultGlobalPreferencesSettingsService;
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        routesMap.putAll(getPersistentRoutesFromDB());
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

        return values;
    }

    private Map<String, ZuulProperties.ZuulRoute> getPersistentRoutesFromDB() {
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
    }
}
