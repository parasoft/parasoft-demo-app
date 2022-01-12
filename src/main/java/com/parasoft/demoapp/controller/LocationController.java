package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Tag(name = "locations")
@Controller
@RequestMapping(value = {"/v1/locations", "/proxy/v1/locations"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Operation(description = "Obtain location information.")
    @ApiResponse(responseCode = "200",
                 description = "Location information under current conditions was returned.")
    @ApiResponse(responseCode = "403", description = "You do not have permission.",
	 			 content = {@Content(schema = @Schema(hidden = true)) })
    @ApiResponse(responseCode = "404", description = "Location information not found.",
                 content = {@Content(schema = @Schema(hidden = true)) })
    @GetMapping("/location")
    @ResponseBody
    public ResponseResult<LocationEntity> getLocation(@RequestParam RegionType region)
            throws ParameterException, LocationNotFoundException {

        ResponseResult<LocationEntity> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
                ResponseResult.MESSAGE_OK);

        response.setData(locationService.getLocationByRegion(region));

        return response;
    }

    @Operation(description = "Obtain all regions of the current industry.")
    @ApiResponse(responseCode = "200",
            description = "All regions of the current industry were returned.")
    @ApiResponse(responseCode = "400",
            description = "Unsupported request.",
            content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "403", description = "You do not have permission.",
            content = {@Content(schema = @Schema(hidden = true)) })
    @GetMapping("/regions")
    @ResponseBody
    public ResponseResult<List<RegionType>> getAllRegionTypesOfCurrentIndustry()
            throws UnsupportedOperationInCurrentIndustryException {

        ResponseResult<List<RegionType>> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
                ResponseResult.MESSAGE_OK);

        response.setData(locationService.getRegionsOfCurrentIndustry());

        return response;
    }

}
