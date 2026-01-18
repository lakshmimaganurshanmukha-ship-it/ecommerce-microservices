package org.tcskart.productservice.dto;

import lombok.Data;

@Data
public class OrderProduct {
	private Long productId;
	private int quantity;
	private int amount;
}
