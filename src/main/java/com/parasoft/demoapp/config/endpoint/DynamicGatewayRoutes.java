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
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.RetryFilterFunctions.retry;
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
            new AtomicReference<>(List.of());
    private final AtomicReference<List<RouteDescription>> routeDescriptions =
            new AtomicReference<>(List.of());

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
        List<RouteDescription> refreshedRouteDescriptions = new ArrayList<>();
        Map<String, String> endpointSnapshot = new LinkedHashMap<>();

        for (RestEndpointEntity endpoint : effectiveRoutes.values()) {
            String routePath = normalizePath(endpoint.getPath());
            if (!StringUtils.hasText(routePath)
                    || (!StringUtils.hasText(endpoint.getUrl()) && !StringUtils.hasText(endpoint.getServiceId()))) {
                continue;
            }

            int segmentsToStrip = endpoint.isStripPrefix() ? stripPrefixSegments(routePath) : 0;
            String targetUri = StringUtils.hasText(endpoint.getUrl())
                    ? endpoint.getUrl()
                    : "lb://" + endpoint.getServiceId();
            RouterFunctions.Builder routeBuilder = GatewayRouterFunctions.route(endpoint.getRouteId())
                    .route(path(routePath), HandlerFunctions.http())
                    .filter(GatewayErrorResponseFilter.wrapErrors(endpoint.getRouteId(), targetUri));
            if (StringUtils.hasText(endpoint.getUrl())) {
                routeBuilder.before(uri(endpoint.getUrl()));
                routeBuilder.before(request -> rewriteRequestPath(request, endpoint.getUrl(), segmentsToStrip));
                refreshedRouteDescriptions.add(new RouteDescription(endpoint.getRouteId(), routePath,
                        endpoint.getUrl(), endpoint.isStripPrefix(), Boolean.TRUE.equals(endpoint.getRetryable())));
            } else {
                routeBuilder.before(request -> rewriteRequestPath(request, null, segmentsToStrip));
                routeBuilder.filter(lb(endpoint.getServiceId()));
                refreshedRouteDescriptions.add(new RouteDescription(endpoint.getRouteId(), routePath,
                        "lb://" + endpoint.getServiceId(), endpoint.isStripPrefix(),
                        Boolean.TRUE.equals(endpoint.getRetryable())));
            }
            if (Boolean.TRUE.equals(endpoint.getRetryable())) {
                routeBuilder.filter(retry(3));
            }
            refreshedRoutes.add(routeBuilder.build());
            if (REST_ENDPOINT_IDS.contains(endpoint.getRouteId())) {
                endpointSnapshot.put(endpoint.getRouteId(), endpoint.getUrl());
            }
        }

        routes.set(List.copyOf(refreshedRoutes));
        routeDescriptions.set(List.copyOf(refreshedRouteDescriptions));
        restEndpointService.refreshRouteRestEndpointsSnapshot(endpointSnapshot);
        String routeMappings = refreshedRouteDescriptions.stream()
                .map(route -> route.path() + " ---> " + route.uri())
                .collect(Collectors.joining(System.lineSeparator()));
        log.info("Refreshed {} Gateway MVC endpoint routes:{}{}", refreshedRoutes.size(),
                System.lineSeparator(), routeMappings);
    }

    @SneakyThrows
    private Map<String, RestEndpointEntity> getEffectiveRoutes() {
        Map<String, RestEndpointEntity> effectiveRoutes = new LinkedHashMap<>();
        for (RestEndpointEntity endpoint : restEndpointService.getAllEndpoints()) {
            if (StringUtils.hasText(endpoint.getPath())
                    && (StringUtils.hasText(endpoint.getUrl()) || StringUtils.hasText(endpoint.getServiceId()))) {
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

    /**
     * Gateway MVC uses a route URI only for its scheme, host, and port. Rebuild
     * the request path to include the configured target URL's path before the
     * request is proxied.
     */
    private static ServerRequest rewriteRequestPath(ServerRequest request, String endpointUrl, int segmentsToStrip) {
        URI requestUri = request.uri();
        String targetPath = targetRequestPath(endpointUrl, requestUri.getRawPath(), segmentsToStrip);
        URI rewrittenUri = UriComponentsBuilder.fromUri(requestUri)
                .replacePath(targetPath)
                .build(true)
                .toUri();
        return ServerRequest.from(request).uri(rewrittenUri).build();
    }

    static String targetRequestPath(String endpointUrl, String requestPath, int segmentsToStrip) {
        String strippedPath = stripPath(requestPath, segmentsToStrip);
        if (!StringUtils.hasText(endpointUrl)) {
            return strippedPath;
        }

        String basePath = URI.create(endpointUrl).getRawPath();
        if (!StringUtils.hasText(basePath) || "/".equals(basePath) || "/".equals(strippedPath)) {
            return StringUtils.hasText(basePath) && !"/".equals(basePath) ? basePath : strippedPath;
        }

        return basePath.endsWith("/")
                ? basePath + strippedPath.substring(1)
                : basePath + strippedPath;
    }

    private static String stripPath(String requestPath, int segmentsToStrip) {
        String[] segments = StringUtils.tokenizeToStringArray(requestPath, "/");
        StringBuilder strippedPath = new StringBuilder("/");
        for (int index = segmentsToStrip; index < segments.length; index++) {
            if (strippedPath.length() > 1) {
                strippedPath.append('/');
            }
            strippedPath.append(segments[index]);
        }
        return strippedPath.toString();
    }

    public List<RouteDescription> getRouteDescriptions() {
        return routeDescriptions.get();
    }

    public record RouteDescription(String id, String path, String uri, boolean stripPrefix, boolean retryable) {
    }
}
