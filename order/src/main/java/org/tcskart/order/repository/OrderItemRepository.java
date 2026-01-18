package org.tcskart.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tcskart.order.beans.OrderItem;
import org.tcskart.order.dto.TopProduct;



public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
//List<OrderItem> findByOrder_userId(Long userid );
	
	@Query("select oi from OrderItem oi where oi.order.userId=:userId")
	List<OrderItem> findall(@Param("userId")Long userId);
	
	 @Query(
	            value = """
	    SELECT
	      product_id AS productId,
	      ROUND(COUNT(DISTINCT order_id) /
	        (SELECT COUNT(DISTINCT order_id) FROM order_item_list) * 100, 2) AS percentage
	    FROM
	      order_item_list
	    GROUP BY
	      product_id
	    ORDER BY
	      percentage DESC
	    LIMIT 3
	    """,
	            nativeQuery = true)
	    List<TopProduct> findTopProductsRaw();

}
