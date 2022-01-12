package com.parasoft.demoapp.repository.global;

import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestEndpointRepository extends JpaRepository<RestEndpointEntity,Long> {

}
