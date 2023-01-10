package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import com.parasoft.demoapp.service.RestEndpointService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@Component
public class CartItemGraphQLDataFetcher {

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpServletRequest;

    private final RestEndpointService restEndpointService;

    public DataFetcher<List<CartItemEntity>> getCartItems() {
        return dataFetchingEnvironment -> {
            try {
                ResponseEntity<ResponseResult<List<CartItemEntity>>> entity =
                        restTemplate.exchange(restEndpointService.getCartBaseUrl(),
                            HttpMethod.GET,
                            new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpServletRequest)),
                            new ParameterizedTypeReference<ResponseResult<List<CartItemEntity>>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<CartItemEntity> addItemInCart() {
        return dataFetchingEnvironment -> {
            try {
                ResponseEntity<ResponseResult<CartItemEntity>> entity =
                        restTemplate.exchange(restEndpointService.getCartBaseUrl(),
                                HttpMethod.POST,
                                new HttpEntity<>(dataFetchingEnvironment.getArgument("shoppingCartDTO"),
                                        RestTemplateUtil.createHeaders(httpServletRequest)),
                                new ParameterizedTypeReference<ResponseResult<CartItemEntity>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<CartItemEntity> getCartItemByItemId() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Long> uriVariables = new HashMap<>();
                uriVariables.put("itemId", dataFetchingEnvironment.getArgument("itemId"));
                ResponseEntity<ResponseResult<CartItemEntity>> entity =
                    restTemplate.exchange(restEndpointService.getCartBaseUrl() + "/{itemId}",
                        HttpMethod.GET,
                        new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpServletRequest)),
                        new ParameterizedTypeReference<ResponseResult<CartItemEntity>>() {},
                        uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<Long> removeCartItem() {
        return environment -> {
            try {
                ResponseEntity<ResponseResult<Long>> entity =
                        restTemplate.exchange(restEndpointService.getCartBaseUrl() + "/{itemId}",
                                HttpMethod.DELETE,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpServletRequest)),
                                new ParameterizedTypeReference<ResponseResult<Long>>() {},
                                Collections.singletonMap("itemId", environment.getArgument("itemId")));
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<Boolean> removeAllCartItems() {
        return dataFetchingEnvironment -> {
            try {
                ResponseEntity<ResponseResult<Boolean>> entity =
                        restTemplate.exchange(restEndpointService.getCartBaseUrl(),
                                HttpMethod.DELETE,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpServletRequest)),
                                new ParameterizedTypeReference<ResponseResult<Boolean>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<CartItemEntity> updateItemInCart() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Long> uriVariables = new HashMap<>();
                uriVariables.put("itemId", dataFetchingEnvironment.getArgument("itemId"));
                ResponseEntity<ResponseResult<CartItemEntity>> entity =
                        restTemplate.exchange(restEndpointService.getCartBaseUrl() + "/{itemId}",
                                HttpMethod.PUT,
                                new HttpEntity<>(dataFetchingEnvironment.getArgument("updateShoppingCartItemDTO"),
                                        RestTemplateUtil.createHeaders(httpServletRequest)),
                                new ParameterizedTypeReference<ResponseResult<CartItemEntity>>() {},
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }
}
