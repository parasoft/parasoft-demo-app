package com.parasoft.demoapp.graphql;

import com.parasoft.demoapp.controller.ResponseResult;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.RegionType;
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
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class LocationGraphQLDataFetcher {

    private final RestTemplate restTemplate;

    private final HttpServletRequest httpRequest;

    private final RestEndpointService restEndpointService;

    public DataFetcher<LocationEntity> getLocation() {
        return environment -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restEndpointService.getLocationsBaseUrl() + "/location");
                Object regionType = environment.getArgument("region");
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

    public DataFetcher<List<RegionType>> getAllRegionTypesOfCurrentIndustry() {
        return environment -> {
            try {
                ResponseEntity<ResponseResult<List<RegionType>>> entity =
                        restTemplate.exchange(restEndpointService.getLocationsBaseUrl() + "/regions",
                                HttpMethod.GET,
                                new HttpEntity<Void>(RestTemplateUtil.createHeaders(httpRequest)),
                                new ParameterizedTypeReference<ResponseResult<List<RegionType>>>() {});
                return Objects.requireNonNull(entity.getBody()).getData();
            } catch (Exception e) {
                throw RestTemplateUtil.convertException(e);
            }
        };
    }
}
