package com.parasoft.demoapp.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parasoft.demoapp.controller.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
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
        log.error(e.getMessage(), e);
        if (e instanceof HttpStatusCodeException) {
            try {
                String errorMsg = e.getMessage();
                int index = errorMsg.indexOf(":");
                String jsonStr = errorMsg.substring(index + 1).trim();
                ObjectMapper objectMapper = new ObjectMapper();
                ResponseResult<?> responseResult = objectMapper.readValue(jsonStr.substring(1, jsonStr.length() - 1), ResponseResult.class);
                return new GraphQLException(((HttpStatusCodeException) e).getRawStatusCode(), responseResult.getData(), responseResult.getMessage(), e);
            } catch (Exception ex) {
                return new GraphQLException(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, ex.getMessage(), ex);
            }
        } else {
            return new GraphQLException(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, e.getMessage(), e);
        }
    }
}
