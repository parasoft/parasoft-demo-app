package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.ImplementedIndustries;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public LocationEntity getLocationByRegion(RegionType region) throws LocationNotFoundException, ParameterException {

        ParameterValidator.requireNonNull(region, OrderMessages.REGION_CANNOT_BE_NULL);

        LocationEntity location = locationRepository.findByRegion(region);
        
        if(location == null){
            throw new LocationNotFoundException(OrderMessages.LOCATION_NOT_FOUND);
        }

        return location;
    }

    public List<LocationEntity> getAllLocations() {

        return locationRepository.findAll();
    }

    public List<RegionType> getRegionsOfCurrentIndustry() throws UnsupportedOperationInCurrentIndustryException {
        IndustryType currentIndustry = IndustryRoutingDataSource.currentIndustry;

        boolean flag = ImplementedIndustries.isIndustryImplemented(currentIndustry);

        if(!flag){
            throw new UnsupportedOperationInCurrentIndustryException(
                    MessageFormat.format(OrderMessages.UNSUPPORTED_OPERATION_IN_CURRENT_INDUSTRY, currentIndustry));
        }

        return RegionType.getRegionsByIndustryType(currentIndustry);
    }

    public boolean isCorrectRegionInCurrentIndustry(RegionType region) throws UnsupportedOperationInCurrentIndustryException{
    	List<RegionType> regions = getRegionsOfCurrentIndustry();
        return regions.contains(region);
    }
}
