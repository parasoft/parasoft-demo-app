package com.parasoft.demoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartDTO {

	@NotNull
	private Long itemId;

	@NotNull
	private Integer itemQty;

}
