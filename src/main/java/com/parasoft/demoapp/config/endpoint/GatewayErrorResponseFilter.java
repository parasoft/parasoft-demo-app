package com.parasoft.demoapp.config.endpoint;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.messages.ConfigMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

/**
 * Converts errors produced while proxying a Gateway MVC route to the application's
 * standard JSON envelope when the target did not return JSON. Successful responses
 * and JSON error responses are passed through unchanged.
 */
@Slf4j
final class GatewayErrorResponseFilter {

    private GatewayErrorResponseFilter() {
    }

    static HandlerFilterFunction<ServerResponse, ServerResponse> wrapErrors(String routeId, String targetUri) {
        return (request, next) -> {
            try {
                ServerResponse response = next.handle(request);
                if (!response.statusCode().isError()) {
                    return response;
                }

                String message = MessageFormat.format(ConfigMessages.GATEWAY_TARGET_RETURNED_HTTP_STATUS,
                        response.statusCode().value());
                if (isJsonResponse(response)) {
                    log.info("Gateway route [{}] {} {} to [{}] returned JSON HTTP {}. Passing target response through.",
                            routeId, request.method(), request.path(), targetUri, response.statusCode().value());
                    return response;
                }

                closeDiscardedProxyResponse(request);
                log.info("Gateway route [{}] {} {} to [{}] returned HTTP {}. Returning error response: {}",
                        routeId, request.method(), request.path(), targetUri,
                        response.statusCode().value(), message);
                return jsonError(response.statusCode(), message);
            } catch (Exception exception) {
                String message = messageFor(exception);
                HttpStatus httpStatus = statusFor(exception);
                log.error("Gateway route [{}] {} {} to [{}] failed. Returning error response: {}",
                        routeId, request.method(), request.path(), targetUri, message, exception);
                return jsonError(httpStatus, message);
            }
        };
    }

    private static boolean isJsonResponse(ServerResponse response) {
        MediaType contentType = response.headers().getContentType();
        if (contentType == null) {
            return false;
        }
        String subtype = contentType.getSubtype().toLowerCase(Locale.ROOT);
        return "json".equals(subtype) || subtype.endsWith("+json");
    }

    private static ServerResponse jsonError(HttpStatusCode httpStatus, String message) {
        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.getInstance(ResponseResult.STATUS_ERR, message));
    }

    private static HttpStatus statusFor(Exception exception) {
        return hasCause(exception, SocketTimeoutException.class)
                || hasCause(exception, HttpTimeoutException.class)
                || hasCause(exception, TimeoutException.class)
                ? HttpStatus.GATEWAY_TIMEOUT
                : HttpStatus.BAD_GATEWAY;
    }

    private static boolean hasCause(Throwable throwable, Class<? extends Throwable> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static String messageFor(Exception exception) {
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        String detail = rootCause.getMessage();
        return MessageFormat.format(ConfigMessages.GATEWAY_REQUEST_FAILED,
                detail == null || detail.isBlank() ? rootCause.getClass().getSimpleName() : detail);
    }

    /**
     * A proxy response normally closes its HTTP connection when its body is written.
     * Since an error body is replaced by the JSON envelope, close that response here.
     */
    private static void closeDiscardedProxyResponse(ServerRequest request) {
        Object response = request.attributes().get(MvcUtils.CLIENT_RESPONSE_ATTR);
        if (response instanceof ClientHttpResponse clientResponse) {
            clientResponse.close();
        }
    }
}
