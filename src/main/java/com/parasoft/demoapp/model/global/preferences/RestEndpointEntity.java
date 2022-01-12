package com.parasoft.demoapp.model.global.preferences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A copy of {@link ZuulProperties.ZuulRoute}.<br/>
 *
 * We can't save {@link ZuulProperties.ZuulRoute},
 * so we save {@RestEndpointEntity} into database, when they are needed, transfer them to {@link ZuulProperties.ZuulRoute}
 */
@Data
@Entity
@NoArgsConstructor
@Table(name="tbl_endpoint")
@EqualsAndHashCode(exclude = "globalPreferences")
@ToString(exclude = "globalPreferences")
public class RestEndpointEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    /**
     * The ID of the route (the same as its map key by default).
     */
    @Column(name = "route_id")
    private String routeId;

    /**a
     * The path (pattern) for the route, e.g. /foo/**.
     */
    @Column(name = "route_path")
    private String path;

    /**
     * The service ID (if any) to map to this route. You can specify a physical URL or
     * a service, but not both.
     */
    @Column(name = "service_id")
    @JsonIgnore
    private String serviceId = null;

    /**
     * A full physical URL to map to the route. An alternative is to use a service ID
     * and service discovery to find the physical address.
     */
    @Column(name = "route_url")
    private String url;

    /**
     * Flag to determine whether the prefix for this route (the path, minus pattern
     * patcher) should be stripped before forwarding.
     */
    @Column(name = "strip_prefix")
    @JsonIgnore
    private boolean stripPrefix = true; // parasoft-suppress OPT.CTLV "expected"

    /**
     * Flag to indicate that this route should be retryable (if supported). Generally
     * retry requires a service ID and ribbon.
     */
    @JsonIgnore
    private Boolean retryable = false;

    @ManyToOne
    @JoinColumn(name = "global_preferences_id")
    @JsonIgnore
    private GlobalPreferencesEntity globalPreferences;

    public RestEndpointEntity(
            String routeId, String path, String url, GlobalPreferencesEntity globalPreferences) {
        this.routeId = routeId;
        this.path = path;
        this.url = url;
        this.globalPreferences = globalPreferences;
    }

    public RestEndpointEntity(String routeId, String path, String url) {
        this.routeId = routeId;
        this.path = path;
        this.url = url;
    }

    public ZuulProperties.ZuulRoute toRealZuulRoute(){
        Set<String> sensitiveHeaders = new HashSet<>();
        return new ZuulProperties.ZuulRoute(
                this.routeId, this.path, this.serviceId, this.url, this.stripPrefix, this.retryable, sensitiveHeaders);
    }
}
