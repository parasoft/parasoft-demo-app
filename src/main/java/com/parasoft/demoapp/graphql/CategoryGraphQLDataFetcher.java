package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.CategoryEntity;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CategoryGraphQLDataFetcher {

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    private final RestEndpointService restEndpointService;

    public DataFetcher<CategoryEntity> getCategoryById() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Long> uriVariables = new HashMap<>();
                uriVariables.put("categoryId", dataFetchingEnvironment.getArgument("categoryId"));
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                    restTemplate.exchange(restEndpointService.getCategoriesBaseUrl() + "/{categoryId}",
                        HttpMethod.GET,
                        new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                        new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {},
                        uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<CategoryEntity> getCategoryByName() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                uriVariables.put("categoryName", dataFetchingEnvironment.getArgument("categoryName"));
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                        restTemplate.exchange(restEndpointService.getCategoriesBaseUrl() + "/name/{categoryName}",
                                HttpMethod.GET,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {},
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<PageInfo<CategoryEntity>> getCategories() {
        return dataFetchingEnvironment -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(restEndpointService.getCategoriesBaseUrl());
                if (dataFetchingEnvironment.containsArgument("searchString")) {
                    builder.queryParam("searchString", (String)dataFetchingEnvironment.getArgument("searchString"));
                }
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
                ResponseEntity<ResponseResult<PageInfo<CategoryEntity>>> entity =
                    restTemplate.exchange(uri,
                        HttpMethod.GET,
                        new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                        new ParameterizedTypeReference<ResponseResult<PageInfo<CategoryEntity>>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<Integer> deleteCategoryById() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Long> uriVariables = new HashMap<>();
                uriVariables.put("categoryId", dataFetchingEnvironment.getArgument("categoryId"));
                ResponseEntity<ResponseResult<Integer>> entity =
                        restTemplate.exchange(restEndpointService.getCategoriesBaseUrl() + "/{categoryId}",
                                HttpMethod.DELETE,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<Integer>>() {},
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<CategoryEntity> addCategory() {
        return environment -> {
            try {
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                        restTemplate.exchange(restEndpointService.getCategoriesBaseUrl(),
                                HttpMethod.POST,
                                new HttpEntity<>(environment.getArgument("categoryDTO"),
                                        RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }

    public DataFetcher<CategoryEntity> updateCategory() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, Long> uriVariables = new HashMap<>();
                uriVariables.put("categoryId", dataFetchingEnvironment.getArgument("categoryId"));
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                        restTemplate.exchange(  restEndpointService.getCategoriesBaseUrl() + "/{categoryId}",
                                HttpMethod.PUT,
                                new HttpEntity<>(dataFetchingEnvironment.getArgument("categoryDto"), RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {},
                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }
}