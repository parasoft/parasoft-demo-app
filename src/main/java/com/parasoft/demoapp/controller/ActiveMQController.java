package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.config.ActiveMQConfig;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Hidden
public class ActiveMQController {
    @Autowired
    private ActiveMQConfig activeMQConfig;

    @GetMapping("/v1/MQConnectorUrl")
    public ResponseResult<String> getWSTransportConnectorUrl() {

        ResponseResult<String> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        response.setData(activeMQConfig.getWsUrl());
        return response;
    }
}
