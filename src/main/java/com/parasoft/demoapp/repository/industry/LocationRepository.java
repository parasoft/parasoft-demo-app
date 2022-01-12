package com.parasoft.demoapp.repository.industry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.RegionType;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    LocationEntity findByRegion(RegionType region);
}
