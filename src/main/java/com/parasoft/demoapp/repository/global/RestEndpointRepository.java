package com.parasoft.demoapp.repository.global;

import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RestEndpointRepository extends JpaRepository<RestEndpointEntity,Long> {

    Set<RestEndpointEntity> findAllByRouteIdNot(String routeId);

    RestEndpointEntity findAllByRouteId(String routeId);
}
