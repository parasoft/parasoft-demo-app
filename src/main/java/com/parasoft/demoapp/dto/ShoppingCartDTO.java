package com.parasoft.demoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartDTO {
	
	private Long itemId;
	
	private Integer itemQty;
}
