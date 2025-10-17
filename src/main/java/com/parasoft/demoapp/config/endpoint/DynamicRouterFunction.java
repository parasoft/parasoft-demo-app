package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.RestEndpointService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.RouterFunctions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.CART_ENDPOINT_PATH;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.CATEGORIES_ENDPOINT_PATH;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.GRAPHQL_ENDPOINT_ID;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.GRAPHQL_ENDPOINT_PATH;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.HOST;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.ITEMS_ENDPOINT_PATH;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.LOCATIONS_ENDPOINT_PATH;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.ORDERS_ENDPOINT_PATH;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.PDA_API_ENDPOINT_PATH;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
@Slf4j
public class DynamicRouterFunction implements RouterFunction<ServerResponse> {

    @Autowired
    private RestEndpointService restEndpointService;

    @Autowired
    private GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private WebConfig webConfig;

    private volatile RouterFunction<ServerResponse> routes;

    @Bean
    @DependsOn("defaultDataInitialization")
    public RouterFunction<ServerResponse> InitializeRoutes() {
        refresh();
        return this.routes;
    }

    @Override
    public Optional<HandlerFunction<ServerResponse>> route(ServerRequest request) {
        return this.routes.route(request);
    }

    @Override
    public void accept(RouterFunctions.Visitor visitor) {
        if (this.routes != null) {
            this.routes.accept(visitor);
        }
    }

    public void refresh() {
        locateRoutes();
        Map<String, String> routeRestEndpointsMap = getRoutesMap().entrySet().stream()
                .filter(entry -> entry.getValue() != null && REST_ENDPOINT_IDS.contains(entry.getValue().getRouteId()))
                .collect(Collectors.toMap(entry -> entry.getValue().getRouteId(), entry -> entry.getValue().getUrl()));
        restEndpointService.refreshRouteRestEndpointsSnapshot(routeRestEndpointsMap);
    }

    @SneakyThrows
    protected Map<String, RestEndpointEntity> getRoutesMap() {
        LinkedHashMap<String, RestEndpointEntity> routesMap = new LinkedHashMap<>(getPersistentRoutes());
        fullFillDefaultRoutes(routesMap);

        LinkedHashMap<String, RestEndpointEntity> values = new LinkedHashMap<>();
        for(Map.Entry<String, RestEndpointEntity> entry : routesMap.entrySet()) {
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

    private void locateRoutes() {
        Map<String, RestEndpointEntity> routesMap = getRoutesMap();
        RouterFunction<ServerResponse> combinedRouter = routesMap.values().stream()
                .map(restEndpointEntity -> {
                    String baseUrl = getBaseUrl(restEndpointEntity.getUrl());
                    String subPath = restEndpointEntity.getUrl().substring(baseUrl.length());
                    String prefixPath = restEndpointEntity.getPath().substring(0, restEndpointEntity.getPath().indexOf("/**"));

                    return GatewayRouterFunctions.route(restEndpointEntity.getRouteId())
                            .route(path(restEndpointEntity.getPath()), http())
                            .before(uri(baseUrl))
                            .before(rewritePath(prefixPath+"(?<segment>.*)", subPath + "${segment}"))
                            .build();
                })
                .reduce(RouterFunction::and).get();
        this.routes = combinedRouter;
    }

    private String getBaseUrl(String url) {
        String baseUrl;
        try {
            URL uri = new URL(url);
            baseUrl = uri.getProtocol() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                baseUrl += ":" + uri.getPort();
            }
        } catch (MalformedURLException e) {
            throw new ResourceAccessException(e.getMessage(), e);
        }
        return baseUrl;
    }

    private Map<String, RestEndpointEntity> getPersistentRoutes() throws GlobalPreferencesNotFoundException, GlobalPreferencesMoreThanOneException {
        Map<String, RestEndpointEntity> routes = new LinkedHashMap<>();

        // Handle pda api endpoint, this is fixed and internal endpoint, user can not change it.
        routes.put(PDA_API_ENDPOINT_PATH, new RestEndpointEntity(PDA_API_ENDPOINT_ID, PDA_API_ENDPOINT_PATH, HOST + webConfig.getServerPort()));

        // RestEndpointEntity is a copy of ZuulProperties.ZuulRoute, they are saved in database
        List<RestEndpointEntity> results = restEndpointService.getAllEndpoints();

        for (RestEndpointEntity result : results) {
            if(StringUtils.isEmpty(result.getPath())) {
                continue;
            }
            if(StringUtils.isEmpty(result.getServiceId()) && StringUtils.isEmpty(result.getUrl())) {
                continue;
            }

            routes.put(result.getPath(), result);
        }

        // Handle graphql endpoint
        String graphQLEndpoint = globalPreferencesService.getCurrentGlobalPreferences().getGraphQLEndpoint();
        if(!graphQLEndpoint.isEmpty()) {
            routes.put(GRAPHQL_ENDPOINT_PATH, new RestEndpointEntity(GRAPHQL_ENDPOINT_ID, GRAPHQL_ENDPOINT_PATH, graphQLEndpoint));
        }

        return routes;
    }

    private void fullFillDefaultRoutes(
            LinkedHashMap<String, RestEndpointEntity> routesMap){

        if(routesMap.get(CATEGORIES_ENDPOINT_PATH) == null){
            routesMap.put(CATEGORIES_ENDPOINT_PATH, defaultGlobalPreferencesSettingsService.defaultCategoriesEndpoint());
        }

        if(routesMap.get(ITEMS_ENDPOINT_PATH) == null){
            routesMap.put(ITEMS_ENDPOINT_PATH, defaultGlobalPreferencesSettingsService.defaultItemsEndpoint());
        }

        if(routesMap.get(CART_ENDPOINT_PATH) == null){
            routesMap.put(CART_ENDPOINT_PATH, defaultGlobalPreferencesSettingsService.defaultCartItemsEndpoint());
        }

        if(routesMap.get(ORDERS_ENDPOINT_PATH) == null){
            routesMap.put(ORDERS_ENDPOINT_PATH, defaultGlobalPreferencesSettingsService.defaultOrdersEndpoint());
        }

        if(routesMap.get(LOCATIONS_ENDPOINT_PATH) == null){
            routesMap.put(LOCATIONS_ENDPOINT_PATH, defaultGlobalPreferencesSettingsService.defaultLocationsEndpoint());
        }

        if(routesMap.get(GRAPHQL_ENDPOINT_PATH) == null){
            routesMap.put(GRAPHQL_ENDPOINT_PATH, new RestEndpointEntity(GRAPHQL_ENDPOINT_ID, GRAPHQL_ENDPOINT_PATH, defaultGlobalPreferencesSettingsService.defaultGraphQLEndpoint()));
        }
    }
}
