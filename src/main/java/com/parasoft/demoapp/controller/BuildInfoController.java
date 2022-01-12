package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.BuildInfoDTO;
import com.parasoft.demoapp.service.BuildInfoService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Hidden
public class BuildInfoController {

    @Autowired
    private BuildInfoService buildInfoService;

    @GetMapping("/v1/build-info")
    public ResponseResult<BuildInfoDTO> getBuildInfo() {
        ResponseResult<BuildInfoDTO> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        response.setData(buildInfoService.getBuildInfo());
        return response;
    }
}
