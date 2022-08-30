package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.model.industry.ShippingEntity;
import lombok.Data;

@Data
public class OrderDTO {
	
    private RegionType region;
    private String location;
    private ShippingEntity shipping;
    private String eventId;
    private String eventNumber;
}
