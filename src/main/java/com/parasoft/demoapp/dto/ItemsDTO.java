package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.RegionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsDTO {

	private String name;
	private String description;
	private Long categoryId;
	private Integer inStock;
	private String imagePath;
	private RegionType region;

}
