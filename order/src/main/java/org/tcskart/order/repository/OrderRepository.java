package org.tcskart.order.repository;

import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.tcskart.order.beans.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tcskart.order.beans.Order;



public interface OrderRepository extends JpaRepository<Order, Integer>{
	
	public List<Order> findByUserId(Long userId);
	
	List<Order> findAllByUserId(Long userId); 
	
//	List<Order> findByUserId(Long userId);
	
	 @Query(
             value = "SELECT ROUND(COUNT(*) / COUNT(DISTINCT DATE(order_date)), 2) FROM order_list",
             nativeQuery = true
)
int getAverageOrdersPerDay();

@Query(
             value = "select count(distinct (user_id)) from order_list",
             nativeQuery=true
)
int getTotalOrders();

}                                                                                                     







