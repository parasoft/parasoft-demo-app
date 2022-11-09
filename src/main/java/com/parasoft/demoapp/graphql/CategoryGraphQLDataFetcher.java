package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.CategoryEntity;
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
public class CategoryGraphQLDataFetcher {
    private final String categoryBaseUrl;

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;


    public CategoryGraphQLDataFetcher(RestTemplate restTemplate, HttpServletRequest httpRequest, WebConfig webConfig) {
        this.restTemplate = restTemplate;
        this.httpRequest = httpRequest;
        this.categoryBaseUrl = "http://localhost:" + webConfig.getServerPort() +"/v1/assets/categories";
    }

    public DataFetcher<CategoryEntity> getCategoryById() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                if (dataFetchingEnvironment.containsArgument("categoryId")) {
                    uriVariables.put("categoryId", dataFetchingEnvironment.getArgument("categoryId"));
                }
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                        restTemplate.exchange(categoryBaseUrl + "/{categoryId}",
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
}