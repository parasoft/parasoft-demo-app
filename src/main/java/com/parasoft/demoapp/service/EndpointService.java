package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.EndpointInvalidException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.util.UrlUtil;
import com.parasoft.demoapp.config.endpoint.DynamicGatewayRoutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class EndpointService {

    @Autowired
    private DynamicGatewayRoutes gatewayRoutes;

    public void refreshEndpoint() {
        gatewayRoutes.refresh();
    }

    public void validateUrl(String urlStr, String exceptionMessage) throws EndpointInvalidException, ParameterException {

        ParameterValidator.requireNonBlank(urlStr, GlobalPreferencesMessages.BLANK_URL);

        if(!UrlUtil.isGoodHttpForm(urlStr)) {
            throw new EndpointInvalidException(MessageFormat.format(exceptionMessage, urlStr));
        }
    }
}
