package com.parasoft.demoapp.config.endpoint;

import com.sun.net.httpserver.HttpServer;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertThrows;

public class GatewayHttpClientConfigTest {

    @Test
    public void configuredGatewayRequestFactoryTimesOutWhileWaitingForTheResponse() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.setExecutor(executor);
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(500);
                exchange.sendResponseHeaders(200, -1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        server.start();

        try {
            ClientHttpRequestFactory requestFactory = new GatewayHttpClientConfig()
                    .gatewayClientHttpRequestFactory(Duration.ofSeconds(1), Duration.ofMillis(100));
            URI slowEndpoint = URI.create("http://localhost:" + server.getAddress().getPort() + "/slow");

            assertThrows(IOException.class, () -> requestFactory.createRequest(slowEndpoint, HttpMethod.GET).execute());
        } finally {
            server.stop(0);
            executor.shutdownNow();
        }
    }
}
