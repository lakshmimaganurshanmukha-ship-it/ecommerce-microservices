package org.tcskart.productservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tcskart.productservice.bean.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	// You can add custom query methods here if needed
	Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category) OR (LOWER(p.name) "
			+ "LIKE %:search% OR LOWER(p.description) LIKE %:search%)")
	Page<Product> searchByCategoryORKeyword(@Param("category") String category, @Param("search") String search,
			Pageable pageable);

	Page<Product> findByCategoryIgnoreCaseAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String category,
			String name, String description, Pageable pageable);

	Optional<Product> findByName(String name);

}
