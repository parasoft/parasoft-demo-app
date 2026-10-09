package com.parasoft.demoapp.config.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Keeps the legacy routes actuator endpoint available after the Zuul to
 * Gateway MVC migration. Use /actuator/routes REST API to view the current route descriptions.
 */
@Component
@Endpoint(id = "routes")
public class GatewayRoutesEndpoint {

    private final DynamicGatewayRoutes gatewayRoutes;

    public GatewayRoutesEndpoint(DynamicGatewayRoutes gatewayRoutes) {
        this.gatewayRoutes = gatewayRoutes;
    }

    @ReadOperation
    public List<DynamicGatewayRoutes.RouteDescription> routes() {
        return gatewayRoutes.getRouteDescriptions();
    }
}
