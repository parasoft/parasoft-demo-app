package com.parasoft.demoapp.config.interceptor;

import com.parasoft.demoapp.config.datasource.IndustryDataSourceConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.service.ParasoftJDBCProxyService;
import com.parasoft.demoapp.util.HttpServletResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@DependsOn("industryDataSource")
public class ParasoftJDBCProxyValidateInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IndustryDataSourceConfig industryDataSourceConfig;

    @Autowired
    private ParasoftJDBCProxyService parasoftJDBCProxyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean needResponseDirectly = false;
        String errorMessage = "";

        try{
            if(IndustryRoutingDataSource.useParasoftJDBCProxy){
                parasoftJDBCProxyService.validateVirtualizeServerUrl(IndustryRoutingDataSource.parasoftVirtualizeServerUrl);
            }
        }catch (Exception e){
            e.printStackTrace();
            needResponseDirectly = true;
            errorMessage = e.getMessage();
        }

        if(needResponseDirectly){
            HttpServletResponseUtil.returnJsonErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ResponseResult.STATUS_ERR, errorMessage, IndustryRoutingDataSource.parasoftVirtualizeServerUrl);
            return false;
        }else{
            IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected = true;
            if(!industryDataSourceConfig.getIndustryDataSources().containsKey(parasoftJDBCProxyService.getProxyKeyOfCurrentIndustry())){
                refreshParasoftJDBCProxyDataSourceSynchronized();
            }
        }

        return true;
    }

    public synchronized void refreshParasoftJDBCProxyDataSourceSynchronized(){

        if(!industryDataSourceConfig.getIndustryDataSources().containsKey(parasoftJDBCProxyService.getProxyKeyOfCurrentIndustry())){
            parasoftJDBCProxyService.refreshParasoftJDBCProxyDataSource();
        }
    }
}
