package org.tcskart.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tcskart.productservice.bean.ProductAvailability;

public interface ProductAvailabilityRepository extends JpaRepository<ProductAvailability, Long> {
	boolean existsByProductIdAndPincode(Long productId, String pincode);
}

