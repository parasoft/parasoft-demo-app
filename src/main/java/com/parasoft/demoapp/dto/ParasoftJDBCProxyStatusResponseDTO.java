package com.parasoft.demoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParasoftJDBCProxyStatusResponseDTO {

    private Boolean useParasoftJDBCProxy;
    private String parasoftVirtualizeServerUrl;
    private Boolean isParasoftVirtualizeServerUrlConnected;
    private String parasoftVirtualizeServerPath;
    private String parasoftVirtualizeGroupId;

}
