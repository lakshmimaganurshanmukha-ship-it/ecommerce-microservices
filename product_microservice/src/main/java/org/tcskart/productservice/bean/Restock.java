package org.tcskart.productservice.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Restock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int restockId;

	@OneToOne
	@JoinColumn(name = "id")
	private Product product;

}
