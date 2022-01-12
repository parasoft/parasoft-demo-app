package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.datasource.DataSourceConfigurationProperties;
import com.parasoft.demoapp.config.datasource.DataSourceConfigurationProperties.*;
import com.parasoft.demoapp.config.datasource.IndustryDataSourceConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.VirtualizeServerUrlException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.util.UrlUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.*;
import static com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource.*;

@Service
public class ParasoftJDBCProxyService {

    public static final String WELL_VIRTUALIZE_GROUP_ID_REGEX = "^[a-zA-Z0-9-_]+$";

    public static final String WELL_VIRTUALIZE_SERVER_PATH_REGEX = "^/[a-zA-Z0-9-_]+$";

    @Autowired
    private GlobalPreferencesDefaultSettingsService globalPreferencesDefaultSettingsService;

    @Autowired
    private IndustryDataSourceConfig industryDataSourceConfig;

    @Autowired
    private DataSourceConfigurationProperties.Industry industryDataSourceProperties;

    @Autowired
    @Qualifier("industryDataSource")
    private IndustryRoutingDataSource industryRoutingDataSource;

    public void refreshParasoftJDBCProxyDataSource(){

        unloadParasoftJDBCProxyDataSource();

        if(IndustryRoutingDataSource.useParasoftJDBCProxy &&
                IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected){

            setSystemArguments();
            loadParasoftJDBCProxyDataSource();
        }

        industryRoutingDataSource.afterPropertiesSet();
    }

    private void setSystemArguments(){
        Map<String, String> arguments = globalPreferencesDefaultSettingsService.defaultParasoftJdbcDriverArguments();
        for(String key : arguments.keySet()){
            if(System.getProperty(key) == null){
                System.setProperty(key, arguments.get(key));
            }
        }

        // overwrite the default value
        System.setProperty(PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_KEY, IndustryRoutingDataSource.parasoftVirtualizeServerUrl);
        System.setProperty(PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_KEY, IndustryRoutingDataSource.parasoftVirtualizeServerPath);
        System.setProperty(PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_KEY, IndustryRoutingDataSource.parasoftVirtualizeGroupId);
    }

    private void loadParasoftJDBCProxyDataSource(){
        DataSource proxy = generateParasoftJDBCProxyDataSourceForCurrentIndustry();
        // Add Parasoft proxy data source into current data sources container.
        industryDataSourceConfig.getIndustryDataSources().put(getProxyKeyOfCurrentIndustry(), proxy);
    }

    private DataSource generateParasoftJDBCProxyDataSourceForCurrentIndustry(){
        DataSourceProperties currentIndustryDataSourceProperties =
                industryDataSourceProperties.getConfigurations().get(IndustryRoutingDataSource.currentIndustry.getValue());

        String proxyUrl = "jdbc:parasoft:proxydriver:" +
                currentIndustryDataSourceProperties.getDriverClassName() + ":@" + currentIndustryDataSourceProperties.getUrl();

        DataSourceProperties proxy = new DataSourceProperties();
        proxy.setDriverClassName("com.parasoft.xtest.jdbc.virt.driver.JDBCProxyDriver");
        proxy.setUrl(proxyUrl);
        proxy.setUsername(currentIndustryDataSourceProperties.getUsername());
        proxy.setPassword(currentIndustryDataSourceProperties.getPassword());

        return proxy.createDataSource();
    }

    private void unloadParasoftJDBCProxyDataSource(){
        Set<String> keysToRemove = new HashSet<>();
        for(Object key : industryDataSourceConfig.getIndustryDataSources().keySet()){
            String keyString = (String)key;
            if(keyString.endsWith(PARASOFT_JDBC_PROXY_DATA_SOURCE_SUFFIX)){
                keysToRemove.add(keyString);
            }
        }

        for(String key : keysToRemove){
            HikariDataSource dataSourceToClose = (HikariDataSource)industryDataSourceConfig.getIndustryDataSources().get(key);
            dataSourceToClose.close();
            industryDataSourceConfig.getIndustryDataSources().remove(key);
        }
    }

    public String getProxyKeyOfCurrentIndustry(){
        return IndustryRoutingDataSource.currentIndustry.getValue() + PARASOFT_JDBC_PROXY_DATA_SOURCE_SUFFIX;
    }

    public void validateVirtualizeServerUrl(String virtualizeServerUrl) throws VirtualizeServerUrlException {

        if(virtualizeServerUrl != null){
            if(!virtualizeServerUrl.endsWith("/")){
                virtualizeServerUrl = virtualizeServerUrl + "/";
            }
            virtualizeServerUrl = virtualizeServerUrl + "servlet/VirtualDatabase/recorder";
        }

        try {
            int responseCode = UrlUtil.validateUrl(virtualizeServerUrl);

            if(responseCode != 200){
                throw new Exception(Integer.toString(responseCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new VirtualizeServerUrlException(
                    MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_URL,
                                            virtualizeServerUrl,  e.getMessage()));
        }
    }

    public void validateVirtualizeServerPath(String virtualizeServerPath) throws ParameterException {
        boolean isValid = true;

        if(virtualizeServerPath == null){
            isValid = false;
        }else{
            Pattern pattern = Pattern.compile(WELL_VIRTUALIZE_SERVER_PATH_REGEX);
            Matcher matcher = pattern.matcher(virtualizeServerPath);

            if(!matcher.matches()){
                isValid = false;
            }
        }

        if(!isValid){
            throw new ParameterException(MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_SERVER_PATH,
                    virtualizeServerPath));
        }
    }

    public void validateVirtualizeGroupId(String virtualizeGroupId) throws ParameterException {
        boolean isValid = true;

        if(virtualizeGroupId == null){
            isValid = false;
        }else{
            Pattern pattern = Pattern.compile(WELL_VIRTUALIZE_GROUP_ID_REGEX);
            Matcher matcher = pattern.matcher(virtualizeGroupId);

            if(!matcher.matches()){
                isValid = false;
            }
        }

        if(!isValid){
            throw new ParameterException(MessageFormat.format(GlobalPreferencesMessages.INVALIDATE_PARASOFT_VIRTUALIZE_GROUP_ID,
                    virtualizeGroupId));
        }
    }
}
