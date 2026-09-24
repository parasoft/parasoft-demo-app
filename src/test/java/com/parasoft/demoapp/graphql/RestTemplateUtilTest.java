package com.parasoft.demoapp.graphql;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

public class RestTemplateUtilTest {

    @Test
    public void createHeaders_doesNotForwardOriginalRequestPayloadLength() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.CONTENT_LENGTH, "206");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");
        request.addHeader("X-Correlation-Id", "request-1");

        HttpHeaders headers = RestTemplateUtil.createHeaders(request);

        assertEquals(-1, headers.getContentLength());
        assertEquals("application/json", headers.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals("Bearer token", headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals("request-1", headers.getFirst("X-Correlation-Id"));
    }
}
