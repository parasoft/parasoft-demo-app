package com.parasoft.demoapp.graphql;

import tools.jackson.databind.ObjectMapper;
import com.parasoft.demoapp.controller.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

@Slf4j
public class RestTemplateUtil {

    private static final Set<String> NON_FORWARDABLE_HEADERS = Set.of(
            "connection", "content-encoding", "content-length", "host", "keep-alive",
            "proxy-authenticate", "proxy-authorization", "te", "trailer", "transfer-encoding", "upgrade");

    public static HttpHeaders createHeaders(HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (NON_FORWARDABLE_HEADERS.contains(name.toLowerCase(Locale.ROOT))) {
                    continue;
                }

                Enumeration<String> headerValues = httpRequest.getHeaders(name);
                while (headerValues.hasMoreElements()) {
                    headers.add(name, headerValues.nextElement());
                }
            }
        }
        return headers;
    }

    public static GraphQLException convertException(Exception e) {
        log.error(e.getMessage(), e);
        if (e instanceof HttpStatusCodeException) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ResponseResult<?> responseResult = objectMapper.readValue(
                        ((HttpStatusCodeException) e).getResponseBodyAsString(), ResponseResult.class);
                return new GraphQLException(((HttpStatusCodeException) e).getStatusCode().value(), responseResult.getData(), responseResult.getMessage(), e);
            } catch (Exception ex) {
                return new GraphQLException(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, ex.getMessage(), ex);
            }
        } else {
            return new GraphQLException(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, e.getMessage(), e);
        }
    }
}
