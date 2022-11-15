package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.LocationEntity;
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
import java.util.Objects;

import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.HOST;

@RequiredArgsConstructor
@Component
public class LocationGraphQLDataFetcher {

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    private final WebConfig webConfig;

    private String locationBaseUrl;

    @PostConstruct
    private void init() {
        locationBaseUrl = HOST + webConfig.getServerPort() + "/v1/locations/location";
    }

    public DataFetcher<LocationEntity> getLocation() {
        return environment -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationBaseUrl);
                Object regionType = (Object) environment.getArgument("region");
                if (regionType != null) {
                    builder.queryParam("region", regionType);
                }
                URI uri = builder.build().encode().toUri();
                ResponseEntity<ResponseResult<LocationEntity>> response =
                        restTemplate.exchange(uri,
                                HttpMethod.GET,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<LocationEntity>>() {});
                return Objects.requireNonNull(response.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }
}
