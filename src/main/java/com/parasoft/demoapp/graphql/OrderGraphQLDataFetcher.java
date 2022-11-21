package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.OrderEntity;
import graphql.schema.DataFetcher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.HOST;

@Component
public class OrderGraphQLDataFetcher {

    private final String orderBaseUrl;

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    public OrderGraphQLDataFetcher(RestTemplate restTemplate, HttpServletRequest httpRequest, WebConfig webConfig) {

        this.restTemplate = restTemplate;
        this.httpRequest = httpRequest;
        this.orderBaseUrl = HOST + webConfig.getServerPort() +"/v1/orders";
    }

    public DataFetcher<OrderEntity> getOrderByOrderNumber() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                uriVariables.put("orderNumber", dataFetchingEnvironment.getArgument("orderNumber"));
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

    public DataFetcher<PageInfo<OrderEntity>> showAllOrders() {
        return dataFetchingEnvironment -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(orderBaseUrl);
                if (dataFetchingEnvironment.containsArgument("size")) {
                    builder.queryParam("size", Integer.toString(dataFetchingEnvironment.getArgument("size")));
                }
                if (dataFetchingEnvironment.containsArgument("page")) {
                    builder.queryParam("page", Integer.toString(dataFetchingEnvironment.getArgument("page")));
                }
                if (dataFetchingEnvironment.containsArgument("sort")) {
                    Object sort = dataFetchingEnvironment.getArgument("sort");
                    if (sort instanceof Collection<?>) {
                        builder.queryParam("sort", (Collection<?>)sort);
                    }
                }
                URI uri = builder.build().encode().toUri();
                ResponseEntity<ResponseResult<PageInfo<OrderEntity>>> entity =
                        restTemplate.exchange(uri,
                                HttpMethod.GET,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<PageInfo<OrderEntity>>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

}
