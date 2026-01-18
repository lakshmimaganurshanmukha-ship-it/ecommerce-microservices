package org.tcskart.cart.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	private Long productId;
	
	private Integer quantity;
	
	private Long price;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	@JsonIgnore
	private Cart cart;	
	
	public long getTotalPrice() {
		return price*quantity;
	}
	
	
}
