package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.model.industry.ShippingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @NotNull
    private RegionType region;
    @NotBlank
    private String location;
    @NotNull
    private ShippingEntity shipping;
    @NotBlank
    private String eventId;
    @NotBlank
    private String eventNumber;
}
