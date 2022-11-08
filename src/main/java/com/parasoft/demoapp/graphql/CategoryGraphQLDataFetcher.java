package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import graphql.schema.DataFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;

@Slf4j
@Component
public class CategoryGraphQLDataFetcher {
    private static final String CATEGORY_BASE_URL = "http://localhost:8080/v1/assets/categories";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest httpRequest;

    public DataFetcher<CategoryEntity> getCategoryById() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                uriVariables.put("categoryId", dataFetchingEnvironment.getArgument("categoryId"));
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                        restTemplate.exchange(CATEGORY_BASE_URL + "/{categoryId}",
                                                HttpMethod.GET,
                                                new HttpEntity<Void>(createHeaders()),
                                                new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {},
                                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw convertException(e);
            }
        };
    }

    public DataFetcher<CategoryEntity> getCategoryByName() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                uriVariables.put("categoryName", dataFetchingEnvironment.getArgument("categoryName"));
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                    restTemplate.exchange(  CATEGORY_BASE_URL + "/name/{categoryName}",
                                                HttpMethod.GET, new HttpEntity<Void>(createHeaders()),
                                                new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {},
                                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw convertException(e);
            }
        };
    }

    public DataFetcher<PageInfo<CategoryEntity>> getCategories() {
        return dataFetchingEnvironment -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(CATEGORY_BASE_URL)
                        .queryParam("searchString", (Object)dataFetchingEnvironment.getArgument("searchString"))
                        .queryParam("size", (Object)dataFetchingEnvironment.getArgument("size"))
                        .queryParam("page", (Object)dataFetchingEnvironment.getArgument("page"))
                        .queryParam("sort", (Collection<?>) dataFetchingEnvironment.getArgument("sort"));

                URI uri = builder.build().encode().toUri();
                ResponseEntity<ResponseResult<PageInfo<CategoryEntity>>> entity =
                    restTemplate.exchange(  uri,
                                            HttpMethod.GET,
                                            new HttpEntity<Void>(createHeaders()),
                                            new ParameterizedTypeReference<ResponseResult<PageInfo<CategoryEntity>>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw convertException(e);
            }
        };
    }

    public DataFetcher<CategoryEntity> updateCategory() {
        return dataFetchingEnvironment -> {
            try {
                Map<String, String> uriVariables = new HashMap<>();
                uriVariables.put("categoryId", dataFetchingEnvironment.getArgument("categoryId"));

                HttpEntity<Object> httpEntity = new HttpEntity<>(dataFetchingEnvironment.getArgument("categoryDto"), createHeaders());
                ResponseEntity<ResponseResult<CategoryEntity>> entity =
                    restTemplate.exchange(  CATEGORY_BASE_URL + "/{categoryId}",
                                                HttpMethod.PUT, httpEntity,
                                                new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {},
                                                uriVariables);
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw convertException(e);
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

    private GraphQLException convertException(Exception e) {
        if (e instanceof HttpClientErrorException) {
            return new GraphQLException(((HttpClientErrorException)e).getRawStatusCode(), e.getMessage(), e);
        } else if(e instanceof HttpServerErrorException) {
            return new GraphQLException(((HttpServerErrorException)e).getRawStatusCode(), e.getMessage(), e);
        } else {
            return new GraphQLException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e);
        }
    }
}
