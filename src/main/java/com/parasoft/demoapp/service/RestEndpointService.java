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

@Service
public class RestEndpointService {

    @Autowired
    private RestEndpointRepository restEndPointRepository;

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
}
