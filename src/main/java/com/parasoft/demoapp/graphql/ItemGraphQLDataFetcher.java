package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.service.RestEndpointService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;

@RequiredArgsConstructor
@Component
public class ItemGraphQLDataFetcher {

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    private final RestEndpointService restEndpointService;

    public DataFetcher<PageInfo<ItemEntity>> getItems() {
        return dataFetchingEnvironment -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(restEndpointService.getItemsBaseUrl());
                if (dataFetchingEnvironment.containsArgument("categoryId")) {
                    builder.queryParam("categoryId", (Long) dataFetchingEnvironment.getArgument("categoryId"));
                }
                if (dataFetchingEnvironment.containsArgument("regions")) {
                    ArrayList<String> regions = dataFetchingEnvironment.getArgument("regions");
                    if (regions != null && regions.size() != 0) {
                        for (String region : regions) {
                            builder.queryParam("regions", region);
                        }
                    }
                }
                if (dataFetchingEnvironment.containsArgument("searchString")) {
                    builder.queryParam("searchString", (Object) dataFetchingEnvironment.getArgument("searchString"));
                }
                if (dataFetchingEnvironment.containsArgument("page")) {
                    builder.queryParam("page", (Object) dataFetchingEnvironment.getArgument("page"));
                }
                if (dataFetchingEnvironment.containsArgument("size")) {
                    builder.queryParam("size", (Object) dataFetchingEnvironment.getArgument("size"));
                }
                if (dataFetchingEnvironment.containsArgument("sort")) {
                    Object sort = dataFetchingEnvironment.getArgument("sort");
                    if (sort instanceof Collection<?>) {
                        builder.queryParam("sort", (Collection<?>) sort);
                    }
                }
                URI uri = builder.build().encode().toUri();
                ResponseEntity<ResponseResult<PageInfo<ItemEntity>>> entity =
                    restTemplate.exchange(uri,
                        HttpMethod.GET,
                        new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                        new ParameterizedTypeReference<ResponseResult<PageInfo<ItemEntity>>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<ItemEntity> getItemByItemId() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Long> uriVariables = new HashMap<>();
                uriVariables.put("itemId", dataFetchingEnvironment.getArgument("itemId"));
                ResponseEntity<ResponseResult<ItemEntity>> entity =
                        restTemplate.exchange(restEndpointService.getItemsBaseUrl() + "/{itemId}",
                                HttpMethod.GET,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<ItemEntity>>() {
                                },
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<ItemEntity> updateItemInStockByItemId() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Object> uriVariables = new HashMap<>();
                uriVariables.put("itemId", dataFetchingEnvironment.getArgument("itemId"));
                uriVariables.put("newInStock", dataFetchingEnvironment.getArgument("newInStock"));
                ResponseEntity<ResponseResult<ItemEntity>> entity =
                        restTemplate.exchange(restEndpointService.getItemsBaseUrl() + "/inStock/{itemId}?newInStock={newInStock}",
                                HttpMethod.PUT,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<ItemEntity>>() {}, uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<String> deleteItemByName() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Object> uriVariables = new HashMap<>();
                uriVariables.put("itemName", dataFetchingEnvironment.getArgument("itemName"));
                ResponseEntity<ResponseResult<String>> entity =
                        restTemplate.exchange(restEndpointService.getItemsBaseUrl() + "/name/{itemName}",
                                HttpMethod.DELETE,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<String>>() {}, uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<ItemEntity> getItemByName() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                uriVariables.put("itemName", dataFetchingEnvironment.getArgument("itemName"));
                ResponseEntity<ResponseResult<ItemEntity>> entity =
                        restTemplate.exchange(restEndpointService.getItemsBaseUrl() + "/name/{itemName}",
                                HttpMethod.GET,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<ItemEntity>>() {},
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<ItemEntity> addNewItem() {
        return dataFetchingEnvironment -> {
            try {
                ResponseEntity<ResponseResult<ItemEntity>> entity =
                        restTemplate.exchange(restEndpointService.getItemsBaseUrl(),
                                HttpMethod.POST,
                                new HttpEntity<>(dataFetchingEnvironment.getArgument("itemsDTO"),
                                        RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<ItemEntity>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<Long> deleteItemByItemId() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Object> uriVariables = new HashMap<>();
                uriVariables.put("itemId", dataFetchingEnvironment.getArgument("itemId"));
                ResponseEntity<ResponseResult<Long>> entity =
                        restTemplate.exchange(restEndpointService.getItemsBaseUrl() + "/{itemId}",
                                HttpMethod.DELETE,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<Long>>() {},
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<ItemEntity> updateItemByItemId() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Object> uriVariables = new HashMap<>();
                uriVariables.put("itemId", dataFetchingEnvironment.getArgument("itemId"));
                ResponseEntity<ResponseResult<ItemEntity>> entity =
                        restTemplate.exchange(restEndpointService.getItemsBaseUrl() + "/{itemId}",
                                HttpMethod.PUT,
                                new HttpEntity<>(dataFetchingEnvironment.getArgument("itemsDTO"),
                                        RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<ItemEntity>>() {},
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }
}
