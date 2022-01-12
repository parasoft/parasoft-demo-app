package com.parasoft.demoapp.config.datasource;

import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_DEFAULT_VALUE;
import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_DEFAULT_VALUE;
import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.parasoft.demoapp.model.global.preferences.IndustryType;

public class IndustryRoutingDataSource extends AbstractRoutingDataSource {

    public static final IndustryType DEFAULT_INDUSTRY = IndustryType.OUTDOOR;
    public volatile static IndustryType currentIndustry = DEFAULT_INDUSTRY;

    public static final String PARASOFT_JDBC_PROXY_DATA_SOURCE_SUFFIX = "_virtualize";
    public volatile static boolean useParasoftJDBCProxy = false;
    public volatile static boolean isParasoftVirtualizeServerUrlConnected;
    public volatile static String parasoftVirtualizeServerUrl = PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;
    public volatile static String parasoftVirtualizeServerPath = PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_DEFAULT_VALUE;
    public volatile static String parasoftVirtualizeGroupId = PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_DEFAULT_VALUE;

    @Override
    public Object determineCurrentLookupKey() {
        if(useParasoftJDBCProxy && isParasoftVirtualizeServerUrlConnected){
            return currentIndustry.getValue() + PARASOFT_JDBC_PROXY_DATA_SOURCE_SUFFIX;
        }else{
            return currentIndustry.getValue();
        }
    }
}
