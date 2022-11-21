package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.CartItemEntity;
import graphql.schema.DataFetcher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.HOST;

@Component
public class CartItemGraphQLDataFetcher {
    private final String cartItemBaseUrl;

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpServletRequest;

    public CartItemGraphQLDataFetcher(RestTemplate restTemplate, HttpServletRequest httpServletRequest, WebConfig webConfig) {
        this.restTemplate = restTemplate;
        this.httpServletRequest = httpServletRequest;
        this.cartItemBaseUrl = HOST + webConfig.getServerPort() + "/v1/cartItems";
    }

    public DataFetcher<List<CartItemEntity>> getCartItems() {
        return dataFetchingEnvironment -> {
            try {
                ResponseEntity<ResponseResult<List<CartItemEntity>>> entity =
                        restTemplate.exchange(cartItemBaseUrl,
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
                        restTemplate.exchange(cartItemBaseUrl,
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

    public DataFetcher<Long> removeCartItem() {
        return environment -> {
            try {
                ResponseEntity<ResponseResult<Long>> entity =
                        restTemplate.exchange(cartItemBaseUrl + "/{itemId}",
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
                        restTemplate.exchange(cartItemBaseUrl,
                                HttpMethod.DELETE,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpServletRequest)),
                                new ParameterizedTypeReference<ResponseResult<Boolean>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }
}
