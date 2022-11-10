package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.EndpointInvalidException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class EndpointService {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteLocator routeLocator;

    public void refreshEndpoint() {
        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(routeLocator);
        publisher.publishEvent(routesRefreshedEvent);
    }

    public void validateUrl(String urlStr, String exceptionMessage) throws EndpointInvalidException, ParameterException {

        ParameterValidator.requireNonBlank(urlStr, GlobalPreferencesMessages.BLANK_URL);

        if(!UrlUtil.isGoodHttpForm(urlStr)) {
            throw new EndpointInvalidException(MessageFormat.format(exceptionMessage, urlStr));
        }
    }
}
