package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.RestEndpointService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.web.servlet.function.RequestPredicates.path;

/**
 * A refreshable, database-backed Gateway MVC router. A request always uses one
 * immutable route list, while a refresh atomically replaces that list.
 */
@Slf4j
public class DynamicGatewayRoutes implements RouterFunction<ServerResponse> {

    private final RestEndpointService restEndpointService;
    private final GlobalPreferencesDefaultSettingsService defaultSettingsService;
    private final GlobalPreferencesService globalPreferencesService;
    private final AtomicReference<List<RouterFunction<ServerResponse>>> routes =
            new AtomicReference<>(new ArrayList<>());

    public DynamicGatewayRoutes(RestEndpointService restEndpointService,
                                GlobalPreferencesDefaultSettingsService defaultSettingsService,
                                GlobalPreferencesService globalPreferencesService) {
        this.restEndpointService = restEndpointService;
        this.defaultSettingsService = defaultSettingsService;
        this.globalPreferencesService = globalPreferencesService;
    }

    @Override
    public Optional<HandlerFunction<ServerResponse>> route(ServerRequest request) {
        for (RouterFunction<ServerResponse> router : routes.get()) {
            Optional<HandlerFunction<ServerResponse>> handler = router.route(request);
            if (handler.isPresent()) {
                return handler;
            }
        }
        return Optional.empty();
    }

    public synchronized void refresh() {
        Map<String, RestEndpointEntity> effectiveRoutes = getEffectiveRoutes();
        List<RouterFunction<ServerResponse>> refreshedRoutes = new ArrayList<>();
        Map<String, String> endpointSnapshot = new LinkedHashMap<>();

        for (RestEndpointEntity endpoint : effectiveRoutes.values()) {
            String routePath = normalizePath(endpoint.getPath());
            if (!StringUtils.hasText(routePath) || !StringUtils.hasText(endpoint.getUrl())) {
                continue;
            }
            refreshedRoutes.add(GatewayRouterFunctions.route(endpoint.getRouteId())
                    .route(path(routePath), HandlerFunctions.http())
                    .before(uri(endpoint.getUrl()))
                    .before(stripPrefix(endpoint.isStripPrefix() ? stripPrefixSegments(routePath) : 0))
                    .build());
            if (REST_ENDPOINT_IDS.contains(endpoint.getRouteId())) {
                endpointSnapshot.put(endpoint.getRouteId(), endpoint.getUrl());
            }
        }

        routes.set(refreshedRoutes);
        restEndpointService.refreshRouteRestEndpointsSnapshot(endpointSnapshot);
        log.info("Refreshed {} Gateway MVC endpoint routes", refreshedRoutes.size());
    }

    @SneakyThrows
    private Map<String, RestEndpointEntity> getEffectiveRoutes() {
        Map<String, RestEndpointEntity> effectiveRoutes = new LinkedHashMap<>();
        for (RestEndpointEntity endpoint : restEndpointService.getAllEndpoints()) {
            if (StringUtils.hasText(endpoint.getPath()) && StringUtils.hasText(endpoint.getUrl())) {
                effectiveRoutes.put(normalizePath(endpoint.getPath()), endpoint);
            }
        }

        addDefaultRoute(effectiveRoutes, defaultSettingsService.defaultCategoriesEndpoint());
        addDefaultRoute(effectiveRoutes, defaultSettingsService.defaultItemsEndpoint());
        addDefaultRoute(effectiveRoutes, defaultSettingsService.defaultCartItemsEndpoint());
        addDefaultRoute(effectiveRoutes, defaultSettingsService.defaultOrdersEndpoint());
        addDefaultRoute(effectiveRoutes, defaultSettingsService.defaultLocationsEndpoint());

        String graphQLEndpoint = globalPreferencesService.getCurrentGlobalPreferences().getGraphQLEndpoint();
        addDefaultRoute(effectiveRoutes, new RestEndpointEntity(GRAPHQL_ENDPOINT_ID, GRAPHQL_ENDPOINT_PATH,
                StringUtils.hasText(graphQLEndpoint) ? graphQLEndpoint : defaultSettingsService.defaultGraphQLEndpoint()));
        return effectiveRoutes;
    }

    private void addDefaultRoute(Map<String, RestEndpointEntity> routes, RestEndpointEntity endpoint) {
        routes.putIfAbsent(normalizePath(endpoint.getPath()), endpoint);
    }

    private String normalizePath(String routePath) {
        return routePath.startsWith("/") ? routePath : "/" + routePath;
    }

    static int stripPrefixSegments(String routePath) {
        int segments = 0;
        for (String segment : StringUtils.tokenizeToStringArray(routePath, "/")) {
            if (segment.contains("*") || segment.contains("{")) {
                break;
            }
            segments++;
        }
        return segments;
    }
}
