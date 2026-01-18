package org.tcskart.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProduct {
	
	
	private Long productId;
	private int quantity;
	
}
