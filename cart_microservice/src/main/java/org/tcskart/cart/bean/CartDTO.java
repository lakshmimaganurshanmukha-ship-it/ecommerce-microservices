package org.tcskart.cart.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
	Integer quantity;
	Long UserId;
	Long ProductId;
	Long TotalPrice;
}
