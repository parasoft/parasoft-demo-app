package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.global.preferences.DataAccessMode;
import com.parasoft.demoapp.model.global.preferences.DemoBugsType;
import com.parasoft.demoapp.model.global.preferences.IndustryType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema
public class GlobalPreferencesDTO {

    @Hidden
    private DataAccessMode dataAccessMode;
    @Hidden
    private String soapEndPoint;

    private IndustryType industryType;

    private Boolean advertisingEnabled;

    private DemoBugsType[] demoBugs;

    private String categoriesRestEndpoint;

    private String itemsRestEndpoint;

    private String cartItemsRestEndpoint;

    private String ordersRestEndpoint;

    private String locationsRestEndpoint;

    private Boolean useParasoftJDBCProxy;

    @Schema(description = "Only works when useParasoftJDBCProxy field is enabled. Default value is http://localhost:9080 if this field is not set.")
    private String parasoftVirtualizeServerUrl;

    @Schema(description = "Only works when useParasoftJDBCProxy field is enabled. Default value is /virtualDb if this field is not set.<br/>" +
                          "The path must start with '/' and only the following characters can be used: 0-9, a-z, A-Z, - and _ .")
    private String parasoftVirtualizeServerPath;

    @Schema(description = "Only works when useParasoftJDBCProxy field is enabled. Default value is /pda if this field is not set.<br/>" +
                          "Only the following characters can be used for the group id: 0-9, a-z, A-Z, - and _ .")
    private String parasoftVirtualizeGroupId;

}
