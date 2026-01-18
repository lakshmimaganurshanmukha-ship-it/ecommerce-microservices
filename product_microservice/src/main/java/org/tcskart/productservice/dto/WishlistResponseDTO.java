package org.tcskart.productservice.dto;

import java.math.BigDecimal;

import java.util.List;

import lombok.Builder;

@Builder
public class WishlistResponseDTO {
	private Long productId;
	private String productName;
	private List<String> imageUrl;
	private BigDecimal price;
}
