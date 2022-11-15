package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.RegionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private RegionType region;
    private String location;
    private String receiverId;
    private String eventId;
    private String eventNumber;
}
