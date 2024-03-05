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
public class ItemsDTO {

	@NotBlank
	private String name;
	@NotBlank
	private String description;
	@NotNull
	private Long categoryId;
	@NotNull
	private Integer inStock;
	private String imagePath;
	@NotNull
	private RegionType region;

}
