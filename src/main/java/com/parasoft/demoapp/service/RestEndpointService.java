package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.RestEndpointNotFoundException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import com.parasoft.demoapp.repository.global.RestEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;

@Service
public class RestEndpointService {

    @Autowired
    private RestEndpointRepository restEndPointRepository;

    private final AtomicReference<Map<String, String>> routeRestEndpoints = new AtomicReference<>();

    public List<RestEndpointEntity> getAllEndpoints() {
        return restEndPointRepository.findAll();
    }

    public void removeAllEndpoints() {
        restEndPointRepository.deleteAll();
    }

    public RestEndpointEntity updateEndpoint(RestEndpointEntity restEndpointEntity)
                                            throws RestEndpointNotFoundException, ParameterException {

        if(restEndpointEntity == null){
            return null;
        }

        Long id = restEndpointEntity.getId();
        if(id == null){
            throw new ParameterException(GlobalPreferencesMessages.REST_ENDPOINT_ENTITY_ID_CAN_NOT_BE_NULL);
        }

        if(!restEndPointRepository.existsById(id)){
            throw new RestEndpointNotFoundException(
                    MessageFormat.format(GlobalPreferencesMessages.ID_OF_REST_ENDPOINT_ENTITY_NOT_FOUND, id));
        }

        return restEndPointRepository.save(restEndpointEntity);
    }

    public void refreshRouteRestEndpoints(Map<String, String> routeRestEndpointsMap) {
        routeRestEndpoints.set(routeRestEndpointsMap);
    }

    public String getCategoriesBaseUrl() {
        return getRouteRestEndpointUrl(CATEGORIES_ENDPOINT_ID);
    }

    public String getItemsBaseUrl() {
        return getRouteRestEndpointUrl(ITEMS_ENDPOINT_ID);
    }

    public String getCartBaseUrl() {
        return getRouteRestEndpointUrl(CART_ENDPOINT_ID);
    }

    public String getOrdersBaseUrl() {
        return getRouteRestEndpointUrl(ORDERS_ENDPOINT_ID);
    }

    public String getLocationsBaseUrl() {
        return getRouteRestEndpointUrl(LOCATIONS_ENDPOINT_ID);
    }

    private String getRouteRestEndpointUrl(String endpointId) {
        Map<String, String> routeRestEndpointsMap = routeRestEndpoints.get();
        return routeRestEndpointsMap == null ? null : routeRestEndpointsMap.get(endpointId);
    }
}
