package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.dto.CategoryDTO;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.service.RestEndpointService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class CategoryGraphQLController {

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    private final RestEndpointService restEndpointService;

    @QueryMapping
    public CategoryEntity getCategoryById(@Argument Long categoryId) {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("categoryId", categoryId);
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
    }

    @QueryMapping
    public CategoryEntity getCategoryByName(@Argument String categoryName) {
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("categoryName", categoryName);
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
    }

    @QueryMapping
    public PageInfo<CategoryEntity> getCategories(@Argument String searchString,
                                               @Argument Integer page,
                                               @Argument Integer size,
                                               @Argument Collection<String> sort,
                                               DataFetchingEnvironment environment
    ) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(restEndpointService.getCategoriesBaseUrl());
            if (environment.containsArgument("searchString") && searchString != null) {
                builder.queryParam("searchString", searchString);
            }
            if (environment.containsArgument("size") && size != null) {
                builder.queryParam("size", size);
            }
            if (environment.containsArgument("page") && page != null) {
                builder.queryParam("page", page);
            }
            if (environment.containsArgument("sort") && sort != null) {
                builder.queryParam("sort", sort);
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
    }

    @MutationMapping
    public Integer deleteCategoryById(@Argument Long categoryId) {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("categoryId", categoryId);
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
    }

    @MutationMapping
    public CategoryEntity addCategory(@Argument CategoryDTO categoryDTO) {
        try {
            ResponseEntity<ResponseResult<CategoryEntity>> entity =
                    restTemplate.exchange(restEndpointService.getCategoriesBaseUrl(),
                            HttpMethod.POST,
                            new HttpEntity<>(categoryDTO,
                                    RestTemplateUtil.createHeaders(httpRequest)),
                            new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {});
            return Objects.requireNonNull(entity.getBody()).getData();
        } catch (Exception e) {
            throw RestTemplateUtil.convertException(e);
        }
    }

    @MutationMapping
    public CategoryEntity updateCategory(@Argument Long categoryId, @Argument CategoryDTO categoryDto) {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("categoryId", categoryId);
            ResponseEntity<ResponseResult<CategoryEntity>> entity =
                    restTemplate.exchange(  restEndpointService.getCategoriesBaseUrl() + "/{categoryId}",
                            HttpMethod.PUT,
                            new HttpEntity<>(categoryDto, RestTemplateUtil.createHeaders(httpRequest)),
                            new ParameterizedTypeReference<ResponseResult<CategoryEntity>>() {},
                            uriVariables);
            return Objects.requireNonNull(entity.getBody()).getData();
        } catch (Exception e) {
            throw RestTemplateUtil.convertException(e);
        }
    }
}