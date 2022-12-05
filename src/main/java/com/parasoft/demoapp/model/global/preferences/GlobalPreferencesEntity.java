package com.parasoft.demoapp.model.global.preferences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Getter
@ToString
@Table(name="tbl_global_preferences")
@NoArgsConstructor
@EqualsAndHashCode
public class GlobalPreferencesEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "data_access_mode")
    @JsonIgnore
    private DataAccessMode dataAccessMode;

    @Setter
    @Column(name = "soap_end_point")
    @JsonIgnore
    private String soapEndPoint;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "industry_type")
    private IndustryType industryType;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "web_service_mode")
    private WebServiceMode webServiceMode;

    @Setter
    @Column(name = "graphql_endpoint")
    private String graphQLEndpoint;

    @Setter
    @Column(name = "ad_enabled")
    private Boolean advertisingEnabled;

    @Setter
    @Column(name = "use_parasoft_jdbc_proxy")
    private Boolean useParasoftJDBCProxy;

    @Setter
    @Column(name = "parasoft_virtualize_server_url")
    private String parasoftVirtualizeServerUrl;

    @Setter
    @Column(name = "parasoft_virtualize_server_path")
    private String parasoftVirtualizeServerPath;

    @Setter
    @Column(name = "parasoft_virtualize_group_id")
    private String parasoftVirtualizeGroupId;

    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "globalPreferences")
    @Cascade(CascadeType.ALL)
    private Set<DemoBugEntity> demoBugs = new TreeSet<>();

    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "globalPreferences")
    @Cascade(CascadeType.ALL)
    private Set<RestEndpointEntity> restEndPoints = new TreeSet<>();

    @Setter
    @Column(name = "labels_overrided")
    @JsonIgnore
    private boolean labelsOverrided = false;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "mq_type")
    private MqType mqType;

    @Setter
    @Column(name = "order_service_destination_queue")
    private String orderServiceDestinationQueue;

    @Setter
    @Column(name = "order_service_reply_to_queue")
    private String orderServiceReplyToQueue;

    @Setter
    @Column(name = "order_service_request_topic")
    private String orderServiceRequestTopic;

    @Setter
    @Column(name = "order_service_response_topic")
    private String orderServiceResponseTopic;

    public GlobalPreferencesEntity(DataAccessMode dataAccessMode, String soapEndpoint,
                                   Set<RestEndpointEntity> restEndpoints,
                                   IndustryType industryType, WebServiceMode webServiceMode,
                                   String graphQLEndpoint, Set<DemoBugEntity> demoBugs,
                                   Boolean advertisingEnabled, Boolean useParasoftJDBCProxy,
                                   String parasoftVirtualizeServerUrl, String parasoftVirtualizeServerPath,
                                   String parasoftVirtualizeGroupId,
                                   MqType mqType,
                                   String orderServiceDestinationQueue,
                                   String orderServiceReplyToQueue,
                                   String orderServiceRequestTopic,
                                   String orderServiceResponseTopic) {
        this.dataAccessMode = dataAccessMode;
        this.soapEndPoint = soapEndpoint;
        this.restEndPoints = restEndpoints;
        this.industryType = industryType;
        this.webServiceMode = webServiceMode;
        this.graphQLEndpoint = graphQLEndpoint;
        this.demoBugs = demoBugs;
        this.advertisingEnabled = advertisingEnabled;
        this.useParasoftJDBCProxy = useParasoftJDBCProxy;
        this.parasoftVirtualizeServerUrl = parasoftVirtualizeServerUrl;
        this.parasoftVirtualizeServerPath = parasoftVirtualizeServerPath;
        this.parasoftVirtualizeGroupId = parasoftVirtualizeGroupId;
        this.mqType = mqType;
        this.orderServiceDestinationQueue = orderServiceDestinationQueue;
        this.orderServiceReplyToQueue = orderServiceReplyToQueue;
        this.orderServiceRequestTopic = orderServiceRequestTopic;
        this.orderServiceResponseTopic = orderServiceResponseTopic;
    }
}
