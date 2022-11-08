package com.parasoft.demoapp.provider;

import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class GraphQLDataFetcher {
    // TODO should get from database
    private static final String BASEURL = "http://localhost:8080/v1/assets";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest httpRequest;

    public DataFetcher<CategoryEntity> getCategoryById() {
        return dataFetchingEnvironment -> {
            try {
                Long id = Long.parseLong(dataFetchingEnvironment.getArgument("categoryId"));
                return restTemplate
                    .exchange(BASEURL + "/categories/" + id, HttpMethod.GET, new HttpEntity(createHeaders()), new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {
                }).getBody().getData();
            } catch (Exception e) {
                throw new GraphQLException(e.getMessage(), e);
            }
        };
    }

    public DataFetcher<CategoryEntity> getCategoryByName() {
        return dataFetchingEnvironment -> {
            try {
                String name = dataFetchingEnvironment.getArgument("categoryName");
                return restTemplate
                    .exchange(BASEURL + "/categories/name/" + name, HttpMethod.GET, new HttpEntity(createHeaders()), new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {
                    }).getBody().getData();
            } catch (Exception e) {
                throw new GraphQLException(e.getMessage(), e);
            }
        };
    }

    public DataFetcher<PageInfo<CategoryEntity>> getCategories() {
        return dataFetchingEnvironment -> {
            try {
                return restTemplate
                    .exchange(BASEURL + "/categories", HttpMethod.GET, new HttpEntity(createHeaders()), new ParameterizedTypeReference<ResponseResult<PageInfo<CategoryEntity>>>() {
                    }).getBody().getData();
            } catch (Exception e) {
                throw new GraphQLException(e.getMessage(), e);
            }
        };
    }

    public DataFetcher<CategoryEntity> updateCategory() {
        return dataFetchingEnvironment -> {
            try {
                Long id = Long.parseLong(dataFetchingEnvironment.getArgument("categoryId"));
                HttpEntity httpEntity = new HttpEntity(dataFetchingEnvironment.getArgument("categoryDto"), createHeaders());
                return restTemplate
                    .exchange(BASEURL + "/categories/" + id, HttpMethod.PUT, httpEntity, new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {
                    }).getBody().getData();
            } catch (Exception e) {
                throw new GraphQLException(e.getMessage(), e);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    private HttpHeaders createHeaders(){
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.add(name, httpRequest.getHeader(name));
            }
        }
        return headers;
    }
}
