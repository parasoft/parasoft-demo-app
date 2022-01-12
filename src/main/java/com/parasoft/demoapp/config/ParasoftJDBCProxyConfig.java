package com.parasoft.demoapp.config;

public class ParasoftJDBCProxyConfig {

    public static final String PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_KEY = "parasoft.virtualize.server.url";
    public static final String PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE = "http://localhost:9080";

    public static final String PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_KEY = "parasoft.virtualize.group.id";
    public static final String PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_DEFAULT_VALUE = "pda";

    public static final String PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_KEY = "parasoft.virtualize.driver.register.jdbcproxydriver.in.drivermanager";
    public static final String PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_DEFAULT_VALUE = "true";

    public static final String PARASOFT_JDBC_PROXY_DIRECT_KEY = "parasoft.virtualize.driver.proxy.direct";
    public static final String PARASOFT_JDBC_PROXY_DIRECT_DEFAULT_VALUE = "true";

    public static final String PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_KEY = "parasoft.virtualize.server.path";
    public static final String PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_DEFAULT_VALUE = "/virtualDb";
}
