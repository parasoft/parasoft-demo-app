package com.parasoft.demoapp.provider;

import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GraphQLDataFetcher {
    private static final String BASEURL = "http://localhost:8080/v1/assets";
    @Autowired
    private RestTemplate restTemplate;

    public DataFetcher<ResponseResult<CategoryEntity>> getCategoryById() {
        return dataFetchingEnvironment -> {
            Long id = Long.parseLong(dataFetchingEnvironment.getArgument("categoryId"));
            return restTemplate
                    .exchange(BASEURL + "/categories/" + id, HttpMethod.GET, null, new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {
                    }).getBody();
        };
    }

    public DataFetcher<ResponseResult<CategoryEntity>> getCategoryByName() {
        return dataFetchingEnvironment -> {
            String name = dataFetchingEnvironment.getArgument("categoryName");
            return restTemplate
                    .exchange(BASEURL + "/categories/name/" + name, HttpMethod.GET, null, new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {
                    }).getBody();
        };
    }

    public DataFetcher<ResponseResult<PageInfo<CategoryEntity>>> getCategories() {
        return dataFetchingEnvironment -> {
            return restTemplate
                    .exchange(BASEURL + "/categories", HttpMethod.GET, null, new ParameterizedTypeReference<ResponseResult<PageInfo<CategoryEntity>>>() {
                    }).getBody();
        };
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
