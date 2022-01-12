package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.RegionType;
import lombok.Data;

@Data
public class OrderDTO {
	
    private RegionType region;
    private String location;
    private String receiverId;
    private String eventId;
    private String eventNumber;
}
