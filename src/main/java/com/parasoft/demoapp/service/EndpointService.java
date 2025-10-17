package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.endpoint.DynamicRouterFunction;
import com.parasoft.demoapp.exception.EndpointInvalidException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class EndpointService {

    @Autowired
    @Lazy
    private DynamicRouterFunction dynamicRouterFunction;

    public void refreshEndpoint() {
        dynamicRouterFunction.refresh();
    }

    public void validateUrl(String urlStr, String exceptionMessage) throws EndpointInvalidException, ParameterException {

        ParameterValidator.requireNonBlank(urlStr, GlobalPreferencesMessages.BLANK_URL);

        if(!UrlUtil.isGoodHttpForm(urlStr)) {
            throw new EndpointInvalidException(MessageFormat.format(exceptionMessage, urlStr));
        }
    }
}
