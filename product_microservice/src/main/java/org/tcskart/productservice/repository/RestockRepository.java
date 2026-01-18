package org.tcskart.productservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tcskart.productservice.bean.Product;
import org.tcskart.productservice.bean.Restock;

@Repository
public interface RestockRepository extends JpaRepository<Restock, Integer>{
	
public Restock findByProduct(Product product);

public boolean existsByProduct(Product product);

public void deleteByProduct(Product product);
@Query("SELECT r.product FROM Restock r")
List<Product> findAllRestockProducts();
}
