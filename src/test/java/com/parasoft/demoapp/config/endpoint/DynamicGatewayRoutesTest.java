package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.RestEndpointService;
import org.junit.Test;

import java.util.Arrays;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DynamicGatewayRoutesTest {

    @Test
    public void refreshUsesPersistedEndpointsAndUpdatesTheSnapshot() throws Throwable {
        RestEndpointService endpointService = mock(RestEndpointService.class);
        GlobalPreferencesDefaultSettingsService defaultSettings = mock(GlobalPreferencesDefaultSettingsService.class);
        GlobalPreferencesService preferencesService = mock(GlobalPreferencesService.class);
        GlobalPreferencesEntity preferences = mock(GlobalPreferencesEntity.class);
        RestEndpointEntity categories = new RestEndpointEntity(CATEGORIES_ENDPOINT_ID, CATEGORIES_ENDPOINT_PATH,
                "http://categories:8080/v1/assets/categories");

        when(endpointService.getAllEndpoints()).thenReturn(Arrays.asList(categories));
        when(preferencesService.getCurrentGlobalPreferences()).thenReturn(preferences);
        when(preferences.getGraphQLEndpoint()).thenReturn("");
        when(defaultSettings.defaultCategoriesEndpoint()).thenReturn(categories);
        when(defaultSettings.defaultItemsEndpoint()).thenReturn(
                new RestEndpointEntity(ITEMS_ENDPOINT_ID, ITEMS_ENDPOINT_PATH, "http://items/v1/assets/items"));
        when(defaultSettings.defaultCartItemsEndpoint()).thenReturn(
                new RestEndpointEntity(CART_ENDPOINT_ID, CART_ENDPOINT_PATH, "http://cart/v1/cartItems"));
        when(defaultSettings.defaultOrdersEndpoint()).thenReturn(
                new RestEndpointEntity(ORDERS_ENDPOINT_ID, ORDERS_ENDPOINT_PATH, "http://orders/v1/orders"));
        when(defaultSettings.defaultLocationsEndpoint()).thenReturn(
                new RestEndpointEntity(LOCATIONS_ENDPOINT_ID, LOCATIONS_ENDPOINT_PATH, "http://locations/v1/locations"));
        when(defaultSettings.defaultGraphQLEndpoint()).thenReturn("http://graphql/graphql");

        new DynamicGatewayRoutes(endpointService, defaultSettings, preferencesService);

        verify(endpointService).refreshRouteRestEndpointsSnapshot(argThat(snapshot ->
                snapshot.size() == 5
                        && "http://categories:8080/v1/assets/categories".equals(snapshot.get(CATEGORIES_ENDPOINT_ID))
                        && "http://items/v1/assets/items".equals(snapshot.get(ITEMS_ENDPOINT_ID))));
    }

    @Test
    public void stripPrefixCountsOnlyTheConcreteRouteSegments() {
        assertEquals(4, DynamicGatewayRoutes.stripPrefixSegments("/proxy/v1/assets/categories/**"));
        assertEquals(1, DynamicGatewayRoutes.stripPrefixSegments("/proxy/{resource}/**"));
        assertEquals(0, DynamicGatewayRoutes.stripPrefixSegments("/**"));
    }
}
