package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.global.preferences.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@Schema
public class GlobalPreferencesResponseDTO {

    @Hidden
    private DataAccessMode dataAccessMode;
    @Hidden
    private String soapEndPoint;

    private IndustryType industryType;

    private WebServiceMode webServiceMode;

    private String graphQLEndpoint;

    private Boolean advertisingEnabled;

    private Set<DemoBugEntity> demoBugs = new TreeSet<>();

    private Boolean useParasoftJDBCProxy;

    @Schema(description = "Only works when useParasoftJDBCProxy field is enabled. Default value is http://localhost:9080 if this field is not set.")
    private String parasoftVirtualizeServerUrl;

    @Schema(description = "Only works when useParasoftJDBCProxy field is enabled. Default value is /virtualDb if this field is not set.<br/>" +
            "The path must start with '/' and only the following characters can be used: 0-9, a-z, A-Z, - and _ .")
    private String parasoftVirtualizeServerPath;

    @Schema(description = "Only works when useParasoftJDBCProxy field is enabled. Default value is /pda if this field is not set.<br/>" +
            "Only the following characters can be used for the group id: 0-9, a-z, A-Z, - and _ .")
    private String parasoftVirtualizeGroupId;

    private MqType mqType;

    private String orderServiceSendTo;

    private String orderServiceListenOn;

    private KafkaConfigEntity kafkaConfig = new KafkaConfigEntity();

    public GlobalPreferencesResponseDTO(GlobalPreferencesEntity globalPreferencesEntity) {
        this.setDataAccessMode(globalPreferencesEntity.getDataAccessMode());
        this.setSoapEndPoint(globalPreferencesEntity.getSoapEndPoint());
        this.setIndustryType(globalPreferencesEntity.getIndustryType());
        this.setWebServiceMode(globalPreferencesEntity.getWebServiceMode());
        this.setGraphQLEndpoint(globalPreferencesEntity.getGraphQLEndpoint());
        this.setAdvertisingEnabled(globalPreferencesEntity.getAdvertisingEnabled());
        this.setDemoBugs(globalPreferencesEntity.getDemoBugs());
        this.setUseParasoftJDBCProxy(globalPreferencesEntity.getUseParasoftJDBCProxy());
        this.setParasoftVirtualizeServerUrl(globalPreferencesEntity.getParasoftVirtualizeServerUrl());
        this.setParasoftVirtualizeServerPath(globalPreferencesEntity.getParasoftVirtualizeServerPath());
        this.setParasoftVirtualizeGroupId(globalPreferencesEntity.getParasoftVirtualizeGroupId());
        this.setMqType(globalPreferencesEntity.getMqType());
        this.setOrderServiceSendTo(globalPreferencesEntity.getOrderServiceDestinationQueue());
        this.setOrderServiceListenOn(globalPreferencesEntity.getOrderServiceReplyToQueue());
        this.getKafkaConfig().setOrderServiceSendTo(globalPreferencesEntity.getOrderServiceRequest());
        this.getKafkaConfig().setOrderServiceListenOn(globalPreferencesEntity.getOrderServiceResponse());
    }
}
