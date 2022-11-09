package com.parasoft.demoapp.graphql;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class RestTemplateUtil {

    public static HttpHeaders createHeaders(HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.add(name, httpRequest.getHeader(name));
            }
        }
        return headers;
    }

    public static GraphQLException convertException(Exception e) {
        if (e instanceof HttpStatusCodeException) {
            return new GraphQLException(((HttpStatusCodeException)e).getRawStatusCode(), e.getMessage(), e);
        } else {
            return new GraphQLException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e);
        }
    }
}
