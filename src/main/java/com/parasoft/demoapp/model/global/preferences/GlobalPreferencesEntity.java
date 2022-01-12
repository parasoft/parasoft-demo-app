package com.parasoft.demoapp.model.global.preferences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


import javax.persistence.*;
import java.util.TreeSet;
import java.util.Set;

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

    public GlobalPreferencesEntity(DataAccessMode dataAccessMode, String soapEndpoint,
                                   Set<RestEndpointEntity> restEndpoints,
                                   IndustryType industryType, Set<DemoBugEntity> demoBugs,
                                   Boolean advertisingEnabled, Boolean useParasoftJDBCProxy,
                                   String parasoftVirtualizeServerUrl, String parasoftVirtualizeServerPath,
                                   String parasoftVirtualizeGroupId) {
        this.dataAccessMode = dataAccessMode;
        this.soapEndPoint = soapEndpoint;
        this.restEndPoints = restEndpoints;
        this.industryType = industryType;
        this.demoBugs = demoBugs;
        this.advertisingEnabled = advertisingEnabled;
        this.useParasoftJDBCProxy = useParasoftJDBCProxy;
        this.parasoftVirtualizeServerUrl = parasoftVirtualizeServerUrl;
        this.parasoftVirtualizeServerPath = parasoftVirtualizeServerPath;
        this.parasoftVirtualizeGroupId = parasoftVirtualizeGroupId;
    }

}
