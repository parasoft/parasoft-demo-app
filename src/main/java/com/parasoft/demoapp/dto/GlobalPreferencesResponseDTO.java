package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.global.preferences.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Schema
public class GlobalPreferencesResponseDTO {

    private IndustryType industryType;

    private WebServiceMode webServiceMode;

    private String graphQLEndpoint;

    private Boolean advertisingEnabled;

    private Set<DemoBugEntity> demoBugs;

    private Set<RestEndpointEntity> restEndPoints;

    private Boolean useParasoftJDBCProxy;

    private String parasoftVirtualizeServerUrl;

    private String parasoftVirtualizeServerPath;

    private String parasoftVirtualizeGroupId;

    private MqType mqType;

    private MqConfigDTO activeMqConfig = new MqConfigDTO();

    private MqConfigDTO kafkaConfig = new MqConfigDTO();

    private MqConfigDTO rabbitMqConfig = new MqConfigDTO();

    public GlobalPreferencesResponseDTO(GlobalPreferencesEntity globalPreferencesEntity) {
        this.setIndustryType(globalPreferencesEntity.getIndustryType());
        this.setWebServiceMode(globalPreferencesEntity.getWebServiceMode());
        this.setGraphQLEndpoint(globalPreferencesEntity.getGraphQLEndpoint());
        this.setAdvertisingEnabled(globalPreferencesEntity.getAdvertisingEnabled());
        this.setDemoBugs(globalPreferencesEntity.getDemoBugs());
        this.setRestEndPoints(globalPreferencesEntity.getRestEndPoints());
        this.setUseParasoftJDBCProxy(globalPreferencesEntity.getUseParasoftJDBCProxy());
        this.setParasoftVirtualizeServerUrl(globalPreferencesEntity.getParasoftVirtualizeServerUrl());
        this.setParasoftVirtualizeServerPath(globalPreferencesEntity.getParasoftVirtualizeServerPath());
        this.setParasoftVirtualizeGroupId(globalPreferencesEntity.getParasoftVirtualizeGroupId());
        this.setMqType(globalPreferencesEntity.getMqType());
        this.getActiveMqConfig().setOrderServiceSendTo(globalPreferencesEntity.getOrderServiceDestinationQueue());
        this.getActiveMqConfig().setOrderServiceListenOn(globalPreferencesEntity.getOrderServiceReplyToQueue());
        this.getKafkaConfig().setOrderServiceSendTo(globalPreferencesEntity.getOrderServiceRequestTopic());
        this.getKafkaConfig().setOrderServiceListenOn(globalPreferencesEntity.getOrderServiceResponseTopic());
        this.getRabbitMqConfig().setOrderServiceSendTo(globalPreferencesEntity.getOrderServiceRequestQueue());
        this.getRabbitMqConfig().setOrderServiceListenOn(globalPreferencesEntity.getOrderServiceResponseQueue());
    }
}
