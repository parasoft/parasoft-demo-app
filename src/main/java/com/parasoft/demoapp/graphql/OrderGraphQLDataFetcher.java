package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.OrderEntity;
import graphql.schema.DataFetcher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class OrderGraphQLDataFetcher {

    private final String orderBaseUrl;

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    public OrderGraphQLDataFetcher(RestTemplate restTemplate, HttpServletRequest httpRequest, WebConfig webConfig) {

        this.restTemplate = restTemplate;
        this.httpRequest = httpRequest;
        this.orderBaseUrl = "http://localhost:" + webConfig.getServerPort() +"/v1/orders";
    }

    public DataFetcher<OrderEntity> getOrderByOrderNumber() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                if (dataFetchingEnvironment.containsArgument("orderNumber")) {
                    uriVariables.put("orderNumber", dataFetchingEnvironment.getArgument("orderNumber"));
                }
                ResponseEntity<ResponseResult<OrderEntity>> entity  =
                        restTemplate.exchange(orderBaseUrl + "/{orderNumber}",
                                HttpMethod.GET,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<OrderEntity>>() {},
                                uriVariables);

                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<OrderEntity> createOrder() {
        return dataFetchingEnvironment -> {
            try {
                ResponseEntity<ResponseResult<OrderEntity>> entity =
                        restTemplate.exchange(orderBaseUrl,
                                HttpMethod.POST,
                                new HttpEntity<>(dataFetchingEnvironment.getArgument("orderDTO"),
                                        RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<OrderEntity>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

}
