package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.dto.ParasoftJDBCProxyStatusResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ParasoftJDBCProxyController {

    @Hidden
    @GetMapping("/v1/parasoftJDBCProxy/status")
    @ResponseBody
    public ResponseResult<ParasoftJDBCProxyStatusResponseDTO> getParasoftJDBCProxyStatus(){
        ResponseResult<ParasoftJDBCProxyStatusResponseDTO> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        ParasoftJDBCProxyStatusResponseDTO status = new ParasoftJDBCProxyStatusResponseDTO(
                IndustryRoutingDataSource.useParasoftJDBCProxy,
                IndustryRoutingDataSource.parasoftVirtualizeServerUrl,
                IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected,
                IndustryRoutingDataSource.parasoftVirtualizeServerPath,
                IndustryRoutingDataSource.parasoftVirtualizeGroupId);

        response.setData(status);

        return response;
    }
}
