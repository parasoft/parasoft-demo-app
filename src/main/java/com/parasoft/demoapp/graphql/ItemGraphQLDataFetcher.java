package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.controller.PageInfo;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.ItemEntity;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.HOST;

@RequiredArgsConstructor
@Component
public class ItemGraphQLDataFetcher {

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    private final WebConfig webConfig;

    private String itemBaseUrl;

    @PostConstruct
    private void init() {
        itemBaseUrl = HOST + webConfig.getServerPort() + "/v1/assets/items";
    }

    public DataFetcher<PageInfo<ItemEntity>> getItems() {
        return dataFetchingEnvironment -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(itemBaseUrl);
                if (dataFetchingEnvironment.containsArgument("categoryId")) {
                    builder.queryParam("categoryId", (Object) dataFetchingEnvironment.getArgument("categoryId"));
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
                Map<String, String> uriVariables = new HashMap<>();
                if (dataFetchingEnvironment.containsArgument("itemId")) {
                    uriVariables.put("itemId", dataFetchingEnvironment.getArgument("itemId"));
                }
                ResponseEntity<ResponseResult<ItemEntity>> entity  =
                        restTemplate.exchange(itemBaseUrl + "/{itemId}",
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
}
