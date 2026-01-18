package org.tcskart.cart.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.tcskart.cart.bean.CartItem;

public interface CartItemRepository extends CrudRepository<CartItem,Long> {
	
	Optional<CartItem> findByCart_UserIdAndProductId(Long userId,Long prodcutId);
	void deleteByCart_UserIdAndProductId(Long userId,Long productId);
	List<CartItem> findByCart_UserId(Long userId);
	void deleteByCart_UserId(Long userId);
}
