package org.tcskart.productservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tcskart.productservice.bean.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByProductId(Long id);
}
