package com.parasoft.demoapp.config.endpoint;

import org.junit.Test;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.UncheckedIOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GatewayErrorResponseFilterTest {

    @Test
    public void wrapsDownstreamErrorInJsonAndClosesTheDiscardedResponse() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        ClientHttpResponse downstreamResponse = mock(ClientHttpResponse.class);
        attributes.put(MvcUtils.CLIENT_RESPONSE_ATTR, downstreamResponse);
        ServerRequest request = requestWith(attributes);

        ServerResponse result = GatewayErrorResponseFilter.wrapErrors("items", "http://items/v1/assets/items").filter(request,
                ignored -> ServerResponse.status(HttpStatus.NOT_FOUND).body("not found"));

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode());
        assertEquals(MediaType.APPLICATION_JSON, result.headers().getContentType());
        verify(downstreamResponse).close();
    }

    @Test
    public void wrapsTimeoutAsGatewayTimeout() throws Exception {
        Map<String, Object> attributes = new HashMap<>();

        ServerResponse result = GatewayErrorResponseFilter.wrapErrors("items", "http://items/v1/assets/items")
                .filter(requestWith(attributes),
                ignored -> {
                    throw new UncheckedIOException(new SocketTimeoutException("Read timed out"));
                });

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, result.statusCode());
        assertEquals(MediaType.APPLICATION_JSON, result.headers().getContentType());
    }

    @Test
    public void preservesJsonErrorResponsesFromTheTarget() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        ClientHttpResponse downstreamResponse = mock(ClientHttpResponse.class);
        attributes.put(MvcUtils.CLIENT_RESPONSE_ATTR, downstreamResponse);
        ServerResponse targetResponse = ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\":\"Category not found\"}");

        ServerResponse result = GatewayErrorResponseFilter
                .wrapErrors("categories", "http://categories/v1/assets/categories")
                .filter(requestWith(attributes), ignored -> targetResponse);

        assertSame(targetResponse, result);
        verify(downstreamResponse, org.mockito.Mockito.never()).close();
    }

    @Test
    public void leavesSuccessfulResponsesUntouched() throws Exception {
        ServerResponse successfulResponse = ServerResponse.ok().body("ok");

        ServerResponse result = GatewayErrorResponseFilter.wrapErrors("items", "http://items/v1/assets/items")
                .filter(requestWith(new HashMap<>()),
                ignored -> successfulResponse);

        assertSame(successfulResponse, result);
    }

    private ServerRequest requestWith(Map<String, Object> attributes) {
        ServerRequest request = mock(ServerRequest.class);
        when(request.attributes()).thenReturn(attributes);
        when(request.method()).thenReturn(HttpMethod.GET);
        when(request.path()).thenReturn("/proxy/v1/assets/items");
        return request;
    }
}
