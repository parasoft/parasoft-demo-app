package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.model.industry.ShippingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private RegionType region;
    private String location;
    private ShippingEntity shipping;
    private String eventId;
    private String eventNumber;
}
