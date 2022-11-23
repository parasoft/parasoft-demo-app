package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.model.global.preferences.*;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs;
import com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.*;

@Service
public class GlobalPreferencesDefaultSettingsService {

    public static final String HOST = "http://localhost:";

    public static final String GRAPHQL_ENDPOINT_ID = "graphql";
    public static final String GRAPHQL_ENDPOINT_PATH = "/proxy/graphql/**";
    public static final String GRAPHQL_ENDPOINT_REAL_PATH = "/graphql";

    public static final String CATEGORIES_ENDPOINT_ID = "categories";
    public static final String CATEGORIES_ENDPOINT_PATH = "/proxy/v1/assets/categories/**";
    public static final String CATEGORIES_ENDPOINT_REAL_PATH = "/v1/assets/categories";

    public static final String ITEMS_ENDPOINT_ID = "items";
    public static final String ITEMS_ENDPOINT_PATH = "/proxy/v1/assets/items/**";
    public static final String ITEMS_ENDPOINT_REAL_PATH = "/v1/assets/items";

    public static final String CART_ENDPOINT_ID = "cart";
    public static final String CART_ENDPOINT_PATH = "/proxy/v1/cartItems/**";
    public static final String CART_ENDPOINT_REAL_PATH = "/v1/cartItems";

    public static final String ORDERS_ENDPOINT_ID = "orders";
    public static final String ORDERS_ENDPOINT_PATH = "/proxy/v1/orders/**";
    public static final String ORDERS_ENDPOINT_REAL_PATH = "/v1/orders";

    public static final String LOCATIONS_ENDPOINT_ID = "locations";
    public static final String LOCATIONS_ENDPOINT_PATH = "/proxy/v1/locations/**";
    public static final String LOCATIONS_ENDPOINT_REAL_PATH = "/v1/locations";

    @Autowired
    private WebConfig webConfig;

    public GlobalPreferencesEntity defaultPreferences(){

        GlobalPreferencesEntity defaultPreferences = new GlobalPreferencesEntity();

        DataAccessMode dataAccessMode = defaultDataAccessMode();
        String soapEndPoint = defaultSoapEndPoint();
        Set<RestEndpointEntity> restEndpoints = defaultEndpoints();
        IndustryType industryType = defaultIndustry();
        WebServiceMode webServiceMode = defaultWebServiceMode();
        String graphQLEndpoint = defaultGraphQLEndpoint();
        Set<DemoBugEntity> demoBugs = defaultDemoBugs();
        Boolean advertisingEnabled = defaultAdvertisingEnabled();
        boolean useParasoftJDBCProxy = defaultUseParasoftJDBCProxy();
        String parasoftVirtualizeServerUrl = defaultParasoftVirtualizeServerUrl();
        String parasoftVirtualizeServerPath = defaultParasoftVirtualizeServerPath();
        String parasoftVirtualizeGroupId = defaultParasoftVirtualizeGroupId();
        Boolean mqProxyEnabled = defaultMqProxyEnabled();
        MqType mqType = defaultMqType();
        String orderServiceDestinationQueue = defaultOrderServiceDestinationQueue();
        String orderServiceReplyToQueue = defaultOrderServiceReplyToQueue();


        defaultPreferences.setDataAccessMode(dataAccessMode);
        defaultPreferences.setSoapEndPoint(soapEndPoint);
        defaultPreferences.setRestEndPoints(restEndpoints);
        defaultPreferences.setIndustryType(industryType);
        defaultPreferences.setGraphQLEndpoint(graphQLEndpoint);
        defaultPreferences.setWebServiceMode(webServiceMode);
        defaultPreferences.setDemoBugs(demoBugs);
        defaultPreferences.setAdvertisingEnabled(advertisingEnabled);
        defaultPreferences.setUseParasoftJDBCProxy(useParasoftJDBCProxy);
        defaultPreferences.setParasoftVirtualizeServerUrl(parasoftVirtualizeServerUrl);
        defaultPreferences.setParasoftVirtualizeServerPath(parasoftVirtualizeServerPath);
        defaultPreferences.setParasoftVirtualizeGroupId(parasoftVirtualizeGroupId);

        defaultPreferences.setMqProxyEnabled(mqProxyEnabled);
        defaultPreferences.setMqType(mqType);
        defaultPreferences.setOrderServiceDestinationQueue(orderServiceDestinationQueue);
        defaultPreferences.setOrderServiceReplyToQueue(orderServiceReplyToQueue);

        return defaultPreferences;
    }

    public DemoBugEntity defaultIncorrectNumberDemoBugs() {

    	return new DemoBugEntity(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS);
    }

    public DemoBugEntity defaultIncorrectLocationDemoBugs() {

    	return new DemoBugEntity(DemoBugsType.INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER);
    }

    public Set<DemoBugEntity> defaultDemoBugs(){
        Set<DemoBugEntity> defaultDemoBugs = new TreeSet<>(new BugsTypeSortOfDemoBugs());

        defaultDemoBugs.add(defaultIncorrectLocationDemoBugs());
        defaultDemoBugs.add(defaultIncorrectNumberDemoBugs());

        return defaultDemoBugs;
    }

    public RestEndpointEntity defaultCategoriesEndpoint(){
        return new RestEndpointEntity(CATEGORIES_ENDPOINT_ID, CATEGORIES_ENDPOINT_PATH,
                HOST + webConfig.getServerPort() + CATEGORIES_ENDPOINT_REAL_PATH);
    }

    public RestEndpointEntity defaultItemsEndpoint(){
        return new RestEndpointEntity(ITEMS_ENDPOINT_ID, ITEMS_ENDPOINT_PATH,
                HOST + webConfig.getServerPort() + ITEMS_ENDPOINT_REAL_PATH);
    }

    public RestEndpointEntity defaultCartItemsEndpoint(){
        return new RestEndpointEntity(CART_ENDPOINT_ID, CART_ENDPOINT_PATH,
                HOST + webConfig.getServerPort() + CART_ENDPOINT_REAL_PATH);
    }

    public RestEndpointEntity defaultOrdersEndpoint(){
        return new RestEndpointEntity(ORDERS_ENDPOINT_ID, ORDERS_ENDPOINT_PATH,
                HOST + webConfig.getServerPort() + ORDERS_ENDPOINT_REAL_PATH);
    }

    public RestEndpointEntity defaultLocationsEndpoint(){
        return new RestEndpointEntity(LOCATIONS_ENDPOINT_ID, LOCATIONS_ENDPOINT_PATH,
                HOST + webConfig.getServerPort() + LOCATIONS_ENDPOINT_REAL_PATH);
    }

    public Set<RestEndpointEntity> defaultEndpoints(){
        Set<RestEndpointEntity> defaultEndpoints = new TreeSet<>(new RouteIdSortOfRestEndpoint());

        defaultEndpoints.add(defaultCategoriesEndpoint());
        defaultEndpoints.add(defaultItemsEndpoint());
        defaultEndpoints.add(defaultCartItemsEndpoint());
        defaultEndpoints.add(defaultOrdersEndpoint());
        defaultEndpoints.add(defaultLocationsEndpoint());

        return defaultEndpoints;
    }

    public IndustryType defaultIndustry(){
        return IndustryRoutingDataSource.DEFAULT_INDUSTRY;
    }

    public WebServiceMode defaultWebServiceMode() {
        return WebServiceMode.REST_API;
    }

    public String defaultGraphQLEndpoint() {
        return HOST + webConfig.getServerPort() + GRAPHQL_ENDPOINT_REAL_PATH;
    }

    public boolean defaultAdvertisingEnabled(){
        return true;
    }

    public DataAccessMode defaultDataAccessMode(){
        return null;
    }

    public String defaultSoapEndPoint(){
        return null;
    }

    public boolean defaultUseParasoftJDBCProxy(){
        return false;
    }

    public String defaultParasoftVirtualizeServerUrl(){
        return PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;
    }

    public String defaultParasoftVirtualizeServerPath(){
        return PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_DEFAULT_VALUE;
    }

    public String defaultParasoftVirtualizeGroupId(){
        return PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_DEFAULT_VALUE;
    }

    public Map<String, String> defaultParasoftJdbcDriverArguments(){
        Map<String, String> arguments = new HashMap<>();

        arguments.put(PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_KEY, PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE);
        arguments.put(PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_KEY, PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_DEFAULT_VALUE);
        arguments.put(PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_KEY, PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_DEFAULT_VALUE);
        arguments.put(PARASOFT_JDBC_PROXY_DIRECT_KEY, PARASOFT_JDBC_PROXY_DIRECT_DEFAULT_VALUE);
        arguments.put(PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_KEY, PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_DEFAULT_VALUE);

        return arguments;
    }

    public Set<String> defaultOverridedLabelNamesForOutdoorIndustry(){
        Set<String> names = defaultCommonOverridedLabelNames();

        names.addAll(regionNamesFor(IndustryType.OUTDOOR));

        return names;
    }

    public Set<String> defaultOverridedLabelNamesForaAerospaceIndustry(){
        Set<String> names = defaultCommonOverridedLabelNames();

        names.addAll(regionNamesFor(IndustryType.AEROSPACE));

        return names;
    }

    public Set<String> defaultOverridedLabelNamesForaDefenseIndustry(){
        Set<String> names = defaultCommonOverridedLabelNames();

        names.addAll(regionNamesFor(IndustryType.DEFENSE));

        return names;
    }

    private List<String> regionNamesFor(IndustryType industryType){
        List<String> regionNames = new ArrayList<>();

        List<RegionType> regionTypes = RegionType.getRegionsByIndustryType(industryType);
        for(RegionType regionType : regionTypes){
            regionNames.add(regionType.name());
        }

        return regionNames;
    }

    private Set<String> defaultCommonOverridedLabelNames(){
        Set<String> names = new HashSet<>();

        // Labels in header
        names.add("PROJECT_NAME");
        names.add("PURCHASER_TITLE");
        names.add("APPROVER_TITLE");
        names.add("HEADER_CART_TITLE");
        names.add("HEADER_ORDER_TITLE");

        // Labels about cart
        names.add("CART_TITLE");
        names.add("ADD_TO_CART");
        names.add("IN_CART");
        names.add("ASSETS_TO_DEPLOY");

        // Labels about location
        names.add("AVAILABILITY_LOCATIONS");

        // Labels about order wizard
        names.add("ORDER_REQUEST");
        names.add("DEPLOYMENT_STEP_IN_WIZARD");
        names.add("ASSIGNMENT_STEP_IN_WIZARD");
        names.add("REVIEW_STEP_IN_WIZARD");
        names.add("ASSIGNMENT_BUTTON");
        names.add("DEPLOY_ASSETS_TO");
        names.add("LOCATION_OPTIONS");
        names.add("RECEIVER_NAME");
        names.add("GPS_COORDINATES");
        names.add("INVOICE_NUMBER");
        names.add("UNIQUE_NUMBER");
        names.add("ORDER");

        // Labels about order
        names.add("ORDER_NUMBER");
        names.add("ORDER_DETAIL_HEADER");
        names.add("ORDER_PAGE_TITLE");
        names.add("ORDER_REQUESTS");

        return names;
    }

    public boolean defaultMqProxyEnabled(){
        return false;
    }

    public MqType defaultMqType() {
        return MqType.ACTIVE_MQ;
    }

    public String defaultOrderServiceDestinationQueue() {
        return ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST;
    }

    public String defaultOrderServiceReplyToQueue() {
        return ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE;
    }
}
