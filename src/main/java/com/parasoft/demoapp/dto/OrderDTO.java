package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.RegionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @NotNull
    private RegionType region;
    @NotBlank
    private String location;
    @NotBlank
    private String receiverId;
    @NotBlank
    private String eventId;
    @NotBlank
    private String eventNumber;
}
